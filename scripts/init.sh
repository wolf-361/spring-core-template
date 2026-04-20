#!/usr/bin/env bash
set -euo pipefail

TEMPLATE_GROUP="com.template"
TEMPLATE_ARTIFACT="core"
TEMPLATE_PACKAGE="com.template.core"
TEMPLATE_PROJECT="spring-core-template"

echo "=============================================="
echo "  Spring Core Template — Project Initializer"
echo "=============================================="
echo ""

# --- Collect inputs ---
read -p "GitHub org or username (e.g. acme): " GITHUB_OWNER
read -p "GitHub repo name (e.g. my-service): " GITHUB_REPO
read -p "Group ID (e.g. com.acme): " GROUP
read -p "Artifact / project name (e.g. my-service): " ARTIFACT
read -p "Base package (e.g. com.acme.myservice) [leave blank to derive from group.artifact]: " PACKAGE
read -p "Keep PR template? [Y/n] " KEEP_PR_TEMPLATE
read -p "Keep issue templates? [Y/n] " KEEP_ISSUE_TEMPLATES
read -p "Install ktlint pre-commit hook? [Y/n] " INSTALL_HOOK

if [[ -z "$PACKAGE" ]]; then
  PACKAGE="${GROUP}.$(echo "$ARTIFACT" | tr '-' '.')"
fi

echo ""
echo "  GitHub:   $GITHUB_OWNER/$GITHUB_REPO"
echo "  Group:    $GROUP"
echo "  Artifact: $ARTIFACT"
echo "  Package:  $PACKAGE"
echo ""
read -p "Proceed? [y/N] " CONFIRM
if [[ ! "$CONFIRM" =~ ^[Yy]$ ]]; then
  echo "Aborted."
  exit 0
fi

echo ""
echo "→ Renaming package references..."

find src -type f \( -name "*.kt" -o -name "*.xml" -o -name "*.yml" -o -name "*.yaml" -o -name "*.properties" \) \
  -exec sed -i "s|${TEMPLATE_PACKAGE}|${PACKAGE}|g" {} +

sed -i "s|${TEMPLATE_GROUP}|${GROUP}|g" build.gradle.kts
sed -i "s|\"${TEMPLATE_PROJECT}\"|\"${ARTIFACT}\"|g" settings.gradle.kts
sed -i "s|name: core-service|name: ${ARTIFACT}|g" src/main/resources/application.yml

echo "→ Moving source directories..."

TEMPLATE_PATH="src/main/kotlin/$(echo "$TEMPLATE_PACKAGE" | tr '.' '/')"
TARGET_PATH="src/main/kotlin/$(echo "$PACKAGE" | tr '.' '/')"
TEST_TEMPLATE_PATH="src/test/kotlin/$(echo "$TEMPLATE_PACKAGE" | tr '.' '/')"
TEST_TARGET_PATH="src/test/kotlin/$(echo "$PACKAGE" | tr '.' '/')"

mkdir -p "$(dirname "$TARGET_PATH")"
mv "$TEMPLATE_PATH" "$TARGET_PATH"

mkdir -p "$(dirname "$TEST_TARGET_PATH")"
mv "$TEST_TEMPLATE_PATH" "$TEST_TARGET_PATH"

find src -type d -empty -delete

echo "→ Updating README badges..."
sed -i "s|your-org/spring-core-template|${GITHUB_OWNER}/${GITHUB_REPO}|g" README.md

if [[ "$KEEP_PR_TEMPLATE" =~ ^[Nn]$ ]]; then
  rm -f .github/PULL_REQUEST_TEMPLATE.md
fi

if [[ "$KEEP_ISSUE_TEMPLATES" =~ ^[Nn]$ ]]; then
  rm -rf .github/ISSUE_TEMPLATE/
fi

if [[ ! "$INSTALL_HOOK" =~ ^[Nn]$ ]]; then
  echo "→ Installing ktlint pre-commit hook (with auto-format)..."
  ./gradlew addKtlintCheckGitPreCommitHook --no-daemon

  # Upgrade the generated hook to auto-format staged files before checking
  cat > .git/hooks/pre-commit << 'HOOK'
#!/usr/bin/env bash
set -e
CHANGED_FILES=$(git diff --cached --name-only --diff-filter=ACMR | grep -E '\.kts?$' | xargs -I {} echo "{}" | tr '\n' ' ')
if [ -z "$CHANGED_FILES" ]; then
  exit 0
fi
./gradlew --quiet ktlintFormat -PinternalKtlintGitFilter="$CHANGED_FILES"
echo "$CHANGED_FILES" | xargs git add
./gradlew --quiet ktlintCheck -PinternalKtlintGitFilter="$CHANGED_FILES"
HOOK
  chmod +x .git/hooks/pre-commit
fi

echo "→ Cleaning up initializer..."
rm -- "$0"

echo ""
echo "Done! Your project is ready."
echo "Next steps:"
echo "  1. Copy .env.example to .env and fill in your values"
echo "  2. Start Postgres: docker compose up -d"
echo "  3. Run the service: ./gradlew bootRun --args='--spring.profiles.active=dev'"
if [[ "$INSTALL_HOOK" =~ ^[Nn]$ ]]; then
  echo ""
  echo "  To install the ktlint pre-commit hook later:"
  echo "    ./gradlew addKtlintCheckGitPreCommitHook"
fi

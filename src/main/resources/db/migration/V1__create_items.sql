CREATE TABLE items (
  id          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  user_id     UUID        NOT NULL,
  name        VARCHAR(255) NOT NULL,
  description TEXT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_items_user_id ON items (user_id);

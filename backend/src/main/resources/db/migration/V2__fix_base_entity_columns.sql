-- V2: Add missing created_at / updated_at columns required by BaseEntity
-- to tables that were created without them in V1.

-- availability_slots: missing both
ALTER TABLE availability_slots
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

-- professionals: missing updated_at
ALTER TABLE professionals
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

-- reviews: missing updated_at
ALTER TABLE reviews
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

-- conversations: missing updated_at
ALTER TABLE conversations
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

-- messages: missing created_at and updated_at
-- (has sent_at, but BaseEntity maps createdAt -> created_at)
ALTER TABLE messages
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

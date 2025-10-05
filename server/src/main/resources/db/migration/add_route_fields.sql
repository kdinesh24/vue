-- Migration: Add missing columns to route table for full route management
-- Date: 2025-10-05
-- Description: Adds distance, transportation_mode, and cost columns to support complete route information

-- Add distance column (in kilometers)
ALTER TABLE route ADD COLUMN IF NOT EXISTS distance DECIMAL(10, 2);

-- Add transportation_mode column
ALTER TABLE route ADD COLUMN IF NOT EXISTS transportation_mode VARCHAR(50);

-- Add cost column (in USD)
ALTER TABLE route ADD COLUMN IF NOT EXISTS cost DECIMAL(12, 2);

-- Add comments for documentation
COMMENT ON COLUMN route.distance IS 'Distance in kilometers';
COMMENT ON COLUMN route.transportation_mode IS 'Transportation mode: Sea, Air, Land, Rail, Multimodal';
COMMENT ON COLUMN route.cost IS 'Route cost in USD';

-- Verify the changes
SELECT column_name, data_type, character_maximum_length, numeric_precision, numeric_scale
FROM information_schema.columns
WHERE table_name = 'route'
ORDER BY ordinal_position;

-- SQL script to fix customers with null payment_frequency
-- This should be run on your database to prevent the NullPointerException

UPDATE customers 
SET payment_frequency = 'MONTHLY' 
WHERE payment_frequency IS NULL;

-- Verify the update
SELECT COUNT(*) as customers_with_null_frequency 
FROM customers 
WHERE payment_frequency IS NULL;

-- This should return 0 after the update

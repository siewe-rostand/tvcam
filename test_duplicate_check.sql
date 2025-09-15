-- Test script to verify the duplicate check functionality
-- Run this after implementing the feature

-- 1. Insert test customer if not exists
INSERT INTO customers (name, address, telephone, payment_frequency, is_active, created_at, updated_at) 
VALUES ('Test Customer', '123 Test St', '123456789', 'MONTHLY', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE name = name;

-- 2. Get the customer ID
SET @customer_id = (SELECT customer_id FROM customers WHERE telephone = '123456789' LIMIT 1);

-- 3. Insert a test bill for current month
INSERT INTO bills (customers_customer_id, month, year, monthly_payment, net_to_pay, payment_status, created_at, updated_at, current_period_bill)
VALUES (@customer_id, LOWER(MONTHNAME(NOW())), YEAR(NOW()), 2000.00, 2000.00, 'UNPAID', NOW(), NOW(), true);

-- 4. Verify the test data
SELECT 
    c.customer_id,
    c.name as customer_name,
    b.bill_id,
    b.month,
    b.year,
    b.monthly_payment,
    b.net_to_pay,
    b.payment_status
FROM customers c 
LEFT JOIN bills b ON c.customer_id = b.customers_customer_id
WHERE c.telephone = '123456789';

-- Now test the API with this customer ID
-- POST /bills/generate-with-duplicate-check
-- {
--   "customerIds": [@customer_id],
--   "shouldGenerate": true,
--   "forceUpdate": null
-- }
-- Should return status 409 with existing bill information

-- Test with forceUpdate: true
-- {
--   "customerIds": [@customer_id],
--   "shouldGenerate": true,  
--   "forceUpdate": true
-- }
-- Should return status 201 with updated bill information

-- Clean up test data (optional)
-- DELETE FROM bills WHERE customers_customer_id = @customer_id;
-- DELETE FROM customers WHERE telephone = '123456789';

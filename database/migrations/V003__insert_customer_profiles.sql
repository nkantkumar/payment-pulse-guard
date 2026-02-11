INSERT INTO customer_risk_profiles (customer_id, base_risk_score, transaction_count_30d, avg_transaction_amount, max_transaction_amount, high_risk_countries, kyc_status, pep_status, sanction_hit, updated_at) VALUES
('cust_12345', 0.2, 10, 250.00, 1000.00, '["RU", "CN"]', 'VERIFIED', false, false, NOW()),
('cust_67890', 0.8, 50, 5000.00, 15000.00, '["IR", "KP"]', 'PENDING', true, true, NOW()),
('cust_abcde', 0.1, 5, 100.00, 200.00, '[]', 'VERIFIED', false, false, NOW()),
('cust_fghij', 0.4, 20, 800.00, 2000.00, '["NG"]', 'VERIFIED', false, false, NOW()),
('cust_klmno', 0.6, 30, 1200.00, 3000.00, '["VE", "CU"]', 'RESTRICTED', false, true, NOW()),
('cust_pqrst', 0.3, 15, 400.00, 800.00, '[]', 'VERIFIED', false, false, NOW()),
('cust_uvwxy', 0.9, 100, 10000.00, 50000.00, '["SY", "UA"]', 'PENDING', true, false, NOW()),
('cust_z1234', 0.05, 2, 50.00, 100.00, '[]', 'VERIFIED', false, false, NOW()),
('cust_56789', 0.5, 25, 900.00, 2500.00, '["AF"]', 'VERIFIED', false, false, NOW()),
('cust_00001', 0.7, 40, 2000.00, 5000.00, '["LB", "YE"]', 'VERIFIED', true, false, NOW());

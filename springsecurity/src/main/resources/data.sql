/*
 * Inserting roles into the tb_roles table, if they don't exist.
 */
INSERT IGNORE INTO tb_roles (role_id, name) VALUES (1, 'admin');
INSERT IGNORE INTO tb_roles (role_id, name) VALUES (2, 'basic');
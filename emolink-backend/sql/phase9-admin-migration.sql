ALTER TABLE `user`
    ADD COLUMN `role` VARCHAR(10) DEFAULT 'user' COMMENT '角色：user-普通用户 admin-管理员';

UPDATE `user`
SET `role` = 'user'
WHERE `role` IS NULL OR `role` = '';

INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`, `role`, `status`)
SELECT 'admin',
       '$2a$10$DEXe3ANf0qbmeO.oCKVf9./sD9z75PsAlqpXDqcDNitSjbVD48vDK',
       '系统管理员',
       '/avatars/admin.png',
       'admin',
       1
WHERE NOT EXISTS (
    SELECT 1
    FROM `user`
    WHERE `username` = 'admin'
);

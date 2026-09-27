SET @cover_column_exists = (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'article'
    AND COLUMN_NAME = 'cover_image'
);

SET @drop_cover_sql = IF(
  @cover_column_exists > 0,
  'ALTER TABLE `article` DROP COLUMN `cover_image`',
  'DO 0'
);

PREPARE remove_article_cover_stmt FROM @drop_cover_sql;
EXECUTE remove_article_cover_stmt;
DEALLOCATE PREPARE remove_article_cover_stmt;

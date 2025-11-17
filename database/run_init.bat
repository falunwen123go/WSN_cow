@echo off
chcp 65001
mysql -u root -p --default-character-set=utf8mb4 < database\init.sql
pause

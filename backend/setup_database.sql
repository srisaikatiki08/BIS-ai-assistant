-- ==========================================================
-- Bureau of Indian Standards (BIS) Assistant Database Setup
-- Run this SQL in PostgreSQL 18:
--   psql -U postgres -f setup_database.sql
-- ==========================================================

-- 1. Create bis_assistant database if it doesn't already exist
SELECT 'CREATE DATABASE bis_assistant'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'bis_assistant')\gexec

\c bis_assistant;

-- Tables will be automatically created and maintained by Spring Boot Hibernate (ddl-auto=update)

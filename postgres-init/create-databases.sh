#!/bin/bash
set -e

# Create the databases for the Jobinder application services if they do not exist already
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE jobinder_identity_db;
    CREATE DATABASE jobinder_matching_db;
    CREATE DATABASE jobinder_chat_db;
EOSQL
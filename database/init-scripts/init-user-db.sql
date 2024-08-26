CREATE USER microsappuser WITH LOGIN PASSWORD 'microservices-database-pwd';
CREATE DATABASE quiz OWNER microsappuser;
CREATE DATABASE gamification OWNER microsappuser;
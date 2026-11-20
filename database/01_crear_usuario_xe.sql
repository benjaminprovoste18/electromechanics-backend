-- Solo si usan Oracle XE instalado en el PC (sin Docker).
-- Ejecutar en SQL Developer conectados como SYSTEM. Pide la contraseña del nuevo usuario.

ALTER SESSION SET CONTAINER = XEPDB1;

CREATE USER emterreno IDENTIFIED BY "&&clave_emterreno"
    DEFAULT TABLESPACE USERS
    QUOTA UNLIMITED ON USERS;

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO emterreno;

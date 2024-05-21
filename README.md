Create tenant table in public schema

create table tenant
(
    tenant_id  varchar(30),
    schema_id  varchar(30),
    is_created boolean
);

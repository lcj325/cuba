create table SEC_LOCALIZED_CONSTRAINT_MESSAGE (
    ID uuid not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    VERSION integer,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    ENTITY_NAME varchar(255) not null,
    OPERATION_TYPE varchar(50) not null,
    MESSAGES text,
    --
    primary key (ID)
)^

create unique index IDX_SEC_LOCALIZED_CONSTRAINT_MESSAGE_UNIQ_ENTITY_NAME_OPERATION_TYPE
    on SEC_LOCALIZED_CONSTRAINT_MESSAGE (ENTITY_NAME, OPERATION_TYPE)
    where DELETE_TS is null^
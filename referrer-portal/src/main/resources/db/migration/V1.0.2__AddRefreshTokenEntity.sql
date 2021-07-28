use
IMED_AUDITDB
go

create table REFRESH_TOKEN
(
    ID            bigint identity primary key,
    refresh_token varchar(255),
    user_id       varchar(255),
    valid         bit
)
    go

create

index user_id_idx
    on REFRESH_TOKEN (user_id)
go
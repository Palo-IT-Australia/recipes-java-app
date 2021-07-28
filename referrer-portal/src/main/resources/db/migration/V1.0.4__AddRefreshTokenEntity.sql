if
    not exists(select *
               from sysobjects
               where name = 'REFRESH_TOKEN'
                 and xtype = 'U')
create table REFRESH_TOKEN
(
    ID            bigint identity primary key,
    refresh_token varchar(255),
    user_id       varchar(255),
    valid         bit
)
go
IF NOT EXISTS(SELECT 1 FROM sys.columns
          WHERE Name = N'iv_contrast_details'
          AND Object_ID = Object_ID(N'dbo.REFERRER_EREFERRAL'))
BEGIN
alter table dbo.REFERRER_EREFERRAL
    add iv_contrast_details varchar(255)
END
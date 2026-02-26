create table sys_oss
(
    oss_id        bigint                                     not null
        constraint sys_oss_pk
            primary key,
    tenant_id     varchar(20)  default '000000'::character varying,
    file_name     varchar(255) default ''::character varying not null,
    original_name varchar(255) default ''::character varying not null,
    file_suffix   varchar(10)  default ''::character varying not null,
    url           varchar(500) default ''::character varying not null,
    ext1          varchar(500) default ''::character varying,
    create_dept   bigint,
    create_by     bigint,
    create_time   timestamp,
    update_by     bigint,
    update_time   timestamp,
    service       varchar(20)  default 'minio'::character varying
);

comment on table sys_oss is 'OSS对象存储表';

comment on column sys_oss.oss_id is '对象存储主键';

comment on column sys_oss.tenant_id is '租户编码';

comment on column sys_oss.file_name is '文件名';

comment on column sys_oss.original_name is '原名';

comment on column sys_oss.file_suffix is '文件后缀名';

comment on column sys_oss.url is 'URL地址';

comment on column sys_oss.ext1 is '扩展字段';

comment on column sys_oss.create_dept is '创建部门';

comment on column sys_oss.create_by is '上传人';

comment on column sys_oss.create_time is '创建时间';

comment on column sys_oss.update_by is '更新者';

comment on column sys_oss.update_time is '更新时间';

comment on column sys_oss.service is '服务商';

alter table sys_oss
    owner to astra;


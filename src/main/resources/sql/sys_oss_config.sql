create table sys_oss_config
(
    oss_config_id bigint                                     not null
        constraint sys_oss_config_pk
            primary key,
    tenant_id     varchar(20)  default '000000'::character varying,
    config_key    varchar(20)  default ''::character varying not null,
    access_key    varchar(255) default ''::character varying,
    secret_key    varchar(255) default ''::character varying,
    bucket_name   varchar(255) default ''::character varying,
    prefix        varchar(255) default ''::character varying,
    endpoint      varchar(255) default ''::character varying,
    domain        varchar(255) default ''::character varying,
    is_https      char         default 'N'::bpchar,
    region        varchar(255) default ''::character varying,
    access_policy char         default '1'::bpchar           not null,
    status        char         default '1'::bpchar,
    ext1          varchar(255) default ''::character varying,
    create_dept   bigint,
    create_by     bigint,
    create_time   timestamp,
    update_by     bigint,
    update_time   timestamp,
    remark        varchar(500) default ''::character varying
);

comment on table sys_oss_config is '对象存储配置表';

comment on column sys_oss_config.oss_config_id is '主键';

comment on column sys_oss_config.tenant_id is '租户编码';

comment on column sys_oss_config.config_key is '配置key';

comment on column sys_oss_config.access_key is 'accessKey';

comment on column sys_oss_config.secret_key is '秘钥';

comment on column sys_oss_config.bucket_name is '桶名称';

comment on column sys_oss_config.prefix is '前缀';

comment on column sys_oss_config.endpoint is '访问站点';

comment on column sys_oss_config.domain is '自定义域名';

comment on column sys_oss_config.is_https is '是否https（Y=是,N=否）';

comment on column sys_oss_config.region is '域';

comment on column sys_oss_config.access_policy is '桶权限类型(0=private 1=public 2=custom)';

comment on column sys_oss_config.status is '是否默认（0=是,1=否）';

comment on column sys_oss_config.ext1 is '扩展字段';

comment on column sys_oss_config.create_dept is '创建部门';

comment on column sys_oss_config.create_by is '创建者';

comment on column sys_oss_config.create_time is '创建时间';

comment on column sys_oss_config.update_by is '更新者';

comment on column sys_oss_config.update_time is '更新时间';

comment on column sys_oss_config.remark is '备注';

alter table sys_oss_config
    owner to astra;

INSERT INTO public.sys_oss_config (oss_config_id, tenant_id, config_key, access_key, secret_key, bucket_name, prefix, endpoint, domain, is_https, region, access_policy, status, ext1, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', 'minio', 'ruoyi', 'ruoyi123', 'ruoyi', '', '127.0.0.1:9000', '', 'N', '', '1', '0', '', 103, 1, '2025-11-25 12:11:27.699012', 1, '2025-11-25 12:11:27.699012', null);
INSERT INTO public.sys_oss_config (oss_config_id, tenant_id, config_key, access_key, secret_key, bucket_name, prefix, endpoint, domain, is_https, region, access_policy, status, ext1, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2, '000000', 'qiniu', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi', '', 's3-cn-north-1.qiniucs.com', '', 'N', '', '1', '1', '', 103, 1, '2025-11-25 12:11:27.699012', 1, '2025-11-25 12:11:27.699012', null);
INSERT INTO public.sys_oss_config (oss_config_id, tenant_id, config_key, access_key, secret_key, bucket_name, prefix, endpoint, domain, is_https, region, access_policy, status, ext1, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (3, '000000', 'aliyun', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi', '', 'oss-cn-beijing.aliyuncs.com', '', 'N', '', '1', '1', '', 103, 1, '2025-11-25 12:11:27.699012', 1, '2025-11-25 12:11:27.699012', null);
INSERT INTO public.sys_oss_config (oss_config_id, tenant_id, config_key, access_key, secret_key, bucket_name, prefix, endpoint, domain, is_https, region, access_policy, status, ext1, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (4, '000000', 'qcloud', 'XXXXXXXXXXXXXXX', 'XXXXXXXXXXXXXXX', 'ruoyi-1240000000', '', 'cos.ap-beijing.myqcloud.com', '', 'N', 'ap-beijing', '1', '1', '', 103, 1, '2025-11-25 12:11:27.699012', 1, '2025-11-25 12:11:27.699012', null);
INSERT INTO public.sys_oss_config (oss_config_id, tenant_id, config_key, access_key, secret_key, bucket_name, prefix, endpoint, domain, is_https, region, access_policy, status, ext1, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (5, '000000', 'image', 'ruoyi', 'ruoyi123', 'ruoyi', 'image', '127.0.0.1:9000', '', 'N', '', '1', '1', '', 103, 1, '2025-11-25 12:11:27.699012', 1, '2025-11-25 12:11:27.699012', null);

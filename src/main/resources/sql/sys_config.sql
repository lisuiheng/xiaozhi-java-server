create table sys_config
(
    config_id    bigint not null
        constraint sys_config_pk
            primary key,
    tenant_id    varchar(20)  default '000000'::character varying,
    config_name  varchar(100) default ''::character varying,
    config_key   varchar(100) default ''::character varying,
    config_value varchar(500) default ''::character varying,
    config_type  char         default 'N'::bpchar,
    create_dept  bigint,
    create_by    bigint,
    create_time  timestamp,
    update_by    bigint,
    update_time  timestamp,
    remark       varchar(500) default NULL::character varying
);

comment on table sys_config is '参数配置表';

comment on column sys_config.config_id is '参数主键';

comment on column sys_config.tenant_id is '租户编号';

comment on column sys_config.config_name is '参数名称';

comment on column sys_config.config_key is '参数键名';

comment on column sys_config.config_value is '参数键值';

comment on column sys_config.config_type is '系统内置（Y是 N否）';

comment on column sys_config.create_dept is '创建部门';

comment on column sys_config.create_by is '创建者';

comment on column sys_config.create_time is '创建时间';

comment on column sys_config.update_by is '更新者';

comment on column sys_config.update_time is '更新时间';

comment on column sys_config.remark is '备注';

alter table sys_config
    owner to astra;

INSERT INTO public.sys_config (config_id, tenant_id, config_name, config_key, config_value, config_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 103, 1, '2025-11-25 12:11:25.110122', null, null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO public.sys_config (config_id, tenant_id, config_name, config_key, config_value, config_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2, '000000', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 103, 1, '2025-11-25 12:11:25.110122', null, null, '初始化密码 123456');
INSERT INTO public.sys_config (config_id, tenant_id, config_name, config_key, config_value, config_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (3, '000000', '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 103, 1, '2025-11-25 12:11:25.110122', null, null, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO public.sys_config (config_id, tenant_id, config_name, config_key, config_value, config_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (5, '000000', '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 103, 1, '2025-11-25 12:11:25.110122', null, null, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO public.sys_config (config_id, tenant_id, config_name, config_key, config_value, config_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (11, '000000', 'OSS预览列表资源开关', 'sys.oss.previewListResource', 'true', 'Y', 103, 1, '2025-11-25 12:11:25.110122', null, null, 'true:开启, false:关闭');

create table sys_dict_type
(
    dict_id     bigint not null
        constraint sys_dict_type_pk
            primary key,
    tenant_id   varchar(20)  default '000000'::character varying,
    dict_name   varchar(100) default ''::character varying,
    dict_type   varchar(100) default ''::character varying,
    create_dept bigint,
    create_by   bigint,
    create_time timestamp,
    update_by   bigint,
    update_time timestamp,
    remark      varchar(500) default NULL::character varying
);

comment on table sys_dict_type is '字典类型表';

comment on column sys_dict_type.dict_id is '字典主键';

comment on column sys_dict_type.tenant_id is '租户编号';

comment on column sys_dict_type.dict_name is '字典名称';

comment on column sys_dict_type.dict_type is '字典类型';

comment on column sys_dict_type.create_dept is '创建部门';

comment on column sys_dict_type.create_by is '创建者';

comment on column sys_dict_type.create_time is '创建时间';

comment on column sys_dict_type.update_by is '更新者';

comment on column sys_dict_type.update_time is '更新时间';

comment on column sys_dict_type.remark is '备注';

alter table sys_dict_type
    owner to astra;

create unique index sys_dict_type_index1
    on sys_dict_type (tenant_id, dict_type);

INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', '用户性别', 'sys_user_sex', 103, 1, '2025-11-25 12:11:24.266823', null, null, '用户性别列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2, '000000', '菜单状态', 'sys_show_hide', 103, 1, '2025-11-25 12:11:24.266823', null, null, '菜单状态列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (3, '000000', '系统开关', 'sys_normal_disable', 103, 1, '2025-11-25 12:11:24.266823', null, null, '系统开关列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (6, '000000', '系统是否', 'sys_yes_no', 103, 1, '2025-11-25 12:11:24.266823', null, null, '系统是否列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (7, '000000', '通知类型', 'sys_notice_type', 103, 1, '2025-11-25 12:11:24.266823', null, null, '通知类型列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (8, '000000', '通知状态', 'sys_notice_status', 103, 1, '2025-11-25 12:11:24.266823', null, null, '通知状态列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (9, '000000', '操作类型', 'sys_oper_type', 103, 1, '2025-11-25 12:11:24.266823', null, null, '操作类型列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (11, '000000', '授权类型', 'sys_grant_type', 103, 1, '2025-11-25 12:11:24.266823', null, null, '认证授权类型');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (12, '000000', '设备类型', 'sys_device_type', 103, 1, '2025-11-25 12:11:24.266823', null, null, '客户端设备类型');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (13, '000000', '业务状态', 'wf_business_status', 103, 1, '2025-11-25 12:12:29.477549', null, null, '业务状态列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (14, '000000', '表单类型', 'wf_form_type', 103, 1, '2025-11-25 12:12:29.477549', null, null, '表单类型列表');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (15, '000000', '任务状态', 'wf_task_status', 103, 1, '2025-11-25 12:12:29.477549', null, null, '任务状态');
INSERT INTO public.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (10, '000000', '系统状态', 'sys_common_status', 103, 1, '2025-11-25 12:11:24.266823', null, '2026-01-14 14:10:08.960853', '登录状态列表');

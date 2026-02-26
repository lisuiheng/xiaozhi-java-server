create table sys_dict_data
(
    dict_code   bigint not null
        constraint sys_dict_data_pk
            primary key,
    tenant_id   varchar(20)  default '000000'::character varying,
    dict_sort   integer      default 0,
    dict_label  varchar(100) default ''::character varying,
    dict_value  varchar(100) default ''::character varying,
    dict_type   varchar(100) default ''::character varying,
    css_class   varchar(100) default NULL::character varying,
    list_class  varchar(100) default NULL::character varying,
    is_default  char         default 'N'::bpchar,
    create_dept bigint,
    create_by   bigint,
    create_time timestamp,
    update_by   bigint,
    update_time timestamp,
    remark      varchar(500) default NULL::character varying
);

comment on table sys_dict_data is '字典数据表';

comment on column sys_dict_data.dict_code is '字典编码';

comment on column sys_dict_data.dict_sort is '字典排序';

comment on column sys_dict_data.dict_label is '字典标签';

comment on column sys_dict_data.dict_value is '字典键值';

comment on column sys_dict_data.dict_type is '字典类型';

comment on column sys_dict_data.css_class is '样式属性（其他样式扩展）';

comment on column sys_dict_data.list_class is '表格回显样式';

comment on column sys_dict_data.is_default is '是否默认（Y是 N否）';

comment on column sys_dict_data.create_dept is '创建部门';

comment on column sys_dict_data.create_by is '创建者';

comment on column sys_dict_data.create_time is '创建时间';

comment on column sys_dict_data.update_by is '更新者';

comment on column sys_dict_data.update_time is '更新时间';

comment on column sys_dict_data.remark is '备注';

alter table sys_dict_data
    owner to astra;

INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', 1, '男', '0', 'sys_user_sex', '', '', 'Y', 103, 1, '2025-11-25 12:11:24.755365', null, null, '性别男');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2, '000000', 2, '女', '1', 'sys_user_sex', '', '', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '性别女');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (3, '000000', 3, '未知', '2', 'sys_user_sex', '', '', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '性别未知');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (4, '000000', 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', 103, 1, '2025-11-25 12:11:24.755365', null, null, '显示菜单');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (5, '000000', 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '隐藏菜单');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (6, '000000', 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', 103, 1, '2025-11-25 12:11:24.755365', null, null, '正常状态');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (7, '000000', 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '停用状态');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (12, '000000', 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', 103, 1, '2025-11-25 12:11:24.755365', null, null, '系统默认是');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (13, '000000', 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '系统默认否');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (14, '000000', 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', 103, 1, '2025-11-25 12:11:24.755365', null, null, '通知');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (15, '000000', 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '公告');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (16, '000000', 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', 103, 1, '2025-11-25 12:11:24.755365', null, null, '正常状态');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (17, '000000', 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '关闭状态');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (29, '000000', 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '其他操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (18, '000000', 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '新增操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (19, '000000', 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '修改操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (20, '000000', 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '删除操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (21, '000000', 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '授权操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (22, '000000', 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '导出操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (23, '000000', 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '导入操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (24, '000000', 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '强退操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (25, '000000', 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '生成操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (26, '000000', 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '清空操作');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (27, '000000', 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '正常状态');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (28, '000000', 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '停用状态');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (30, '000000', 0, '密码认证', 'password', 'sys_grant_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '密码认证');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (31, '000000', 0, '短信认证', 'sms', 'sys_grant_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '短信认证');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (32, '000000', 0, '邮件认证', 'email', 'sys_grant_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '邮件认证');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (33, '000000', 0, '小程序认证', 'xcx', 'sys_grant_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '小程序认证');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (34, '000000', 0, '三方登录认证', 'social', 'sys_grant_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '三方登录认证');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (35, '000000', 0, 'PC', 'pc', 'sys_device_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, 'PC');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (36, '000000', 0, '安卓', 'android', 'sys_device_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '安卓');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (37, '000000', 0, 'iOS', 'ios', 'sys_device_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, 'iOS');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (38, '000000', 0, '小程序', 'xcx', 'sys_device_type', '', 'default', 'N', 103, 1, '2025-11-25 12:11:24.755365', null, null, '小程序');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (39, '000000', 1, '已撤销', 'cancel', 'wf_business_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '已撤销');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (40, '000000', 2, '草稿', 'draft', 'wf_business_status', '', 'info', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '草稿');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (41, '000000', 3, '待审核', 'waiting', 'wf_business_status', '', 'primary', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '待审核');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (42, '000000', 4, '已完成', 'finish', 'wf_business_status', '', 'success', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '已完成');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (43, '000000', 5, '已作废', 'invalid', 'wf_business_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '已作废');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (44, '000000', 6, '已退回', 'back', 'wf_business_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '已退回');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (45, '000000', 7, '已终止', 'termination', 'wf_business_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '已终止');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (46, '000000', 1, '自定义表单', 'static', 'wf_form_type', '', 'success', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '自定义表单');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (47, '000000', 2, '动态表单', 'dynamic', 'wf_form_type', '', 'primary', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '动态表单');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (48, '000000', 1, '撤销', 'cancel', 'wf_task_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '撤销');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (49, '000000', 2, '通过', 'pass', 'wf_task_status', '', 'success', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '通过');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (50, '000000', 3, '待审核', 'waiting', 'wf_task_status', '', 'primary', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '待审核');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (51, '000000', 4, '作废', 'invalid', 'wf_task_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '作废');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (52, '000000', 5, '退回', 'back', 'wf_task_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '退回');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (53, '000000', 6, '终止', 'termination', 'wf_task_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '终止');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (54, '000000', 7, '转办', 'transfer', 'wf_task_status', '', 'primary', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '转办');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (55, '000000', 8, '委托', 'depute', 'wf_task_status', '', 'primary', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '委托');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (56, '000000', 9, '抄送', 'copy', 'wf_task_status', '', 'primary', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '抄送');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (57, '000000', 10, '加签', 'sign', 'wf_task_status', '', 'primary', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '加签');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (58, '000000', 11, '减签', 'sign_off', 'wf_task_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '减签');
INSERT INTO public.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (59, '000000', 11, '超时', 'timeout', 'wf_task_status', '', 'danger', 'N', 103, 1, '2025-11-25 12:12:29.477549', null, null, '超时');

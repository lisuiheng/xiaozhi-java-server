create table sys_dept
(
    dept_id       bigint not null
        constraint sys_dept_pk
            primary key,
    tenant_id     varchar(20)  default '000000'::character varying,
    parent_id     bigint       default 0,
    ancestors     varchar(500) default ''::character varying,
    dept_name     varchar(30)  default ''::character varying,
    dept_category varchar(100) default NULL::character varying,
    order_num     integer      default 0,
    leader        bigint,
    phone         varchar(11)  default NULL::character varying,
    email         varchar(50)  default NULL::character varying,
    status        char         default '0'::bpchar,
    del_flag      char         default '0'::bpchar,
    create_dept   bigint,
    create_by     bigint,
    create_time   timestamp,
    update_by     bigint,
    update_time   timestamp
);

comment on table sys_dept is '部门表';

comment on column sys_dept.dept_id is '部门ID';

comment on column sys_dept.tenant_id is '租户编号';

comment on column sys_dept.parent_id is '父部门ID';

comment on column sys_dept.ancestors is '祖级列表';

comment on column sys_dept.dept_name is '部门名称';

comment on column sys_dept.dept_category is '部门类别编码';

comment on column sys_dept.order_num is '显示顺序';

comment on column sys_dept.leader is '负责人';

comment on column sys_dept.phone is '联系电话';

comment on column sys_dept.email is '邮箱';

comment on column sys_dept.status is '部门状态（0正常 1停用）';

comment on column sys_dept.del_flag is '删除标志（0代表存在 1代表删除）';

comment on column sys_dept.create_dept is '创建部门';

comment on column sys_dept.create_by is '创建者';

comment on column sys_dept.create_time is '创建时间';

comment on column sys_dept.update_by is '更新者';

comment on column sys_dept.update_time is '更新时间';

alter table sys_dept
    owner to astra;

INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (100, '000000', 0, '0', 'XXX科技', null, 0, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (101, '000000', 100, '0,100', '深圳总公司', null, 1, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (102, '000000', 100, '0,100', '长沙分公司', null, 2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (103, '000000', 101, '0,100,101', '研发部门', null, 1, 1, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (105, '000000', 101, '0,100,101', '测试部门', null, 3, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (106, '000000', 101, '0,100,101', '财务部门', null, 4, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (107, '000000', 101, '0,100,101', '运维部门', null, 5, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (108, '000000', 102, '0,100,102', '市场部门', null, 1, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (109, '000000', 102, '0,100,102', '财务部门', null, 2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.849886', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (104, '000000', 101, '0,100,101', '市场部门', null, 2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, '2025-11-25 12:11:20.000000', null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (2011301465507872769, '000000', 101, '0,100,101', 'test', null, 0, null, null, null, '0', '1', null, null, null, null, null);
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (2011312301479653377, '000000', 103, '0,100,101,103', 'test', null, 0, null, null, null, '0', '1', null, null, null, null, '2026-01-14 13:50:50.683704');
INSERT INTO public.sys_dept (dept_id, tenant_id, parent_id, ancestors, dept_name, dept_category, order_num, leader, phone, email, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (2011315126926434306, '000000', 101, '0,100,101', 'test', null, 0, null, null, null, '0', '1', null, null, '2026-01-14 13:51:09.346016', null, '2026-01-14 13:51:15.827471');

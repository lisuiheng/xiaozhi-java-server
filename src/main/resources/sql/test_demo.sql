create table test_demo
(
    id          bigint,
    tenant_id   varchar(20) default '000000'::character varying,
    dept_id     bigint,
    user_id     bigint,
    order_num   integer     default 0,
    test_key    varchar(255),
    value       varchar(255),
    version     integer     default 0,
    create_dept bigint,
    create_time timestamp,
    create_by   bigint,
    update_time timestamp,
    update_by   bigint,
    del_flag    integer     default 0
);

comment on table test_demo is '测试单表';

comment on column test_demo.id is '主键';

comment on column test_demo.tenant_id is '租户编号';

comment on column test_demo.dept_id is '部门id';

comment on column test_demo.user_id is '用户id';

comment on column test_demo.order_num is '排序号';

comment on column test_demo.test_key is 'key键';

comment on column test_demo.value is '值';

comment on column test_demo.version is '版本';

comment on column test_demo.create_dept is '创建部门';

comment on column test_demo.create_time is '创建时间';

comment on column test_demo.create_by is '创建人';

comment on column test_demo.update_time is '更新时间';

comment on column test_demo.update_by is '更新人';

comment on column test_demo.del_flag is '删除标志';

alter table test_demo
    owner to astra;

INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (1, '000000', 102, 4, 1, '测试数据权限', '测试', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (2, '000000', 102, 3, 2, '子节点1', '111', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (3, '000000', 102, 3, 3, '子节点2', '222', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (4, '000000', 108, 4, 4, '测试数据', 'demo', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (5, '000000', 108, 3, 13, '子节点11', '1111', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (6, '000000', 108, 3, 12, '子节点22', '2222', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (7, '000000', 108, 3, 11, '子节点33', '3333', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (8, '000000', 108, 3, 10, '子节点44', '4444', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (9, '000000', 108, 3, 9, '子节点55', '5555', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (10, '000000', 108, 3, 8, '子节点66', '6666', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (11, '000000', 108, 3, 7, '子节点77', '7777', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (12, '000000', 108, 3, 6, '子节点88', '8888', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_demo (id, tenant_id, dept_id, user_id, order_num, test_key, value, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (13, '000000', 108, 3, 5, '子节点99', '9999', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);

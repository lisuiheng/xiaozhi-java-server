create table test_tree
(
    id          bigint,
    tenant_id   varchar(20) default '000000'::character varying,
    parent_id   bigint      default 0,
    dept_id     bigint,
    user_id     bigint,
    tree_name   varchar(255),
    version     integer     default 0,
    create_dept bigint,
    create_time timestamp,
    create_by   bigint,
    update_time timestamp,
    update_by   bigint,
    del_flag    integer     default 0
);

comment on table test_tree is '测试树表';

comment on column test_tree.id is '主键';

comment on column test_tree.tenant_id is '租户编号';

comment on column test_tree.parent_id is '父id';

comment on column test_tree.dept_id is '部门id';

comment on column test_tree.user_id is '用户id';

comment on column test_tree.tree_name is '值';

comment on column test_tree.version is '版本';

comment on column test_tree.create_dept is '创建部门';

comment on column test_tree.create_time is '创建时间';

comment on column test_tree.create_by is '创建人';

comment on column test_tree.update_time is '更新时间';

comment on column test_tree.update_by is '更新人';

comment on column test_tree.del_flag is '删除标志';

alter table test_tree
    owner to astra;

INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (1, '000000', 0, 102, 4, '测试数据权限', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (2, '000000', 1, 102, 3, '子节点1', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (3, '000000', 2, 102, 3, '子节点2', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (4, '000000', 0, 108, 4, '测试树1', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (5, '000000', 4, 108, 3, '子节点11', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (6, '000000', 4, 108, 3, '子节点22', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (7, '000000', 4, 108, 3, '子节点33', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (8, '000000', 5, 108, 3, '子节点44', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (9, '000000', 6, 108, 3, '子节点55', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (10, '000000', 7, 108, 3, '子节点66', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (11, '000000', 7, 108, 3, '子节点77', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (12, '000000', 10, 108, 3, '子节点88', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);
INSERT INTO public.test_tree (id, tenant_id, parent_id, dept_id, user_id, tree_name, version, create_dept, create_time, create_by, update_time, update_by, del_flag) VALUES (13, '000000', 10, 108, 3, '子节点99', 0, 103, '2025-11-25 12:11:28.870428', 1, null, null, 0);

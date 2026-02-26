create table sys_role
(
    role_id             bigint       not null
        constraint sys_role_pk
            primary key,
    tenant_id           varchar(20)  default '000000'::character varying,
    role_name           varchar(30)  not null,
    role_key            varchar(100) not null,
    role_sort           integer      not null,
    data_scope          char         default '1'::bpchar,
    menu_check_strictly boolean      default true,
    dept_check_strictly boolean      default true,
    status              char         not null,
    del_flag            char         default '0'::bpchar,
    create_dept         bigint,
    create_by           bigint,
    create_time         timestamp,
    update_by           bigint,
    update_time         timestamp,
    remark              varchar(500) default NULL::character varying
);

comment on table sys_role is '角色信息表';

comment on column sys_role.role_id is '角色ID';

comment on column sys_role.tenant_id is '租户编号';

comment on column sys_role.role_name is '角色名称';

comment on column sys_role.role_key is '角色权限字符串';

comment on column sys_role.role_sort is '显示顺序';

comment on column sys_role.data_scope is '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）';

comment on column sys_role.menu_check_strictly is '菜单树选择项是否关联显示';

comment on column sys_role.dept_check_strictly is '部门树选择项是否关联显示';

comment on column sys_role.status is '角色状态（0正常 1停用）';

comment on column sys_role.del_flag is '删除标志（0代表存在 1代表删除）';

comment on column sys_role.create_dept is '创建部门';

comment on column sys_role.create_by is '创建者';

comment on column sys_role.create_time is '创建时间';

comment on column sys_role.update_by is '更新者';

comment on column sys_role.update_time is '更新时间';

comment on column sys_role.remark is '备注';

alter table sys_role
    owner to astra;

INSERT INTO public.sys_role (role_id, tenant_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', '超级管理员', 'superadmin', 1, '1', true, true, '0', '0', 103, 1, '2025-11-25 12:11:22.214692', null, null, '超级管理员');
INSERT INTO public.sys_role (role_id, tenant_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (3, '000000', '本部门及以下', 'test1', 3, '4', true, true, '0', '0', 103, 1, '2025-11-25 12:11:22.214692', 1, null, '1');

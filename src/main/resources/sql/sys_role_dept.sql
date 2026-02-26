create table sys_role_dept
(
    role_id bigint not null,
    dept_id bigint not null,
    constraint sys_role_dept_pk
        primary key (role_id, dept_id)
);

comment on table sys_role_dept is '角色和部门关联表';

comment on column sys_role_dept.role_id is '角色ID';

comment on column sys_role_dept.dept_id is '部门ID';

alter table sys_role_dept
    owner to astra;


create table sys_tenant_package
(
    package_id          bigint not null
        constraint pk_sys_tenant_package
            primary key,
    package_name        varchar(20)   default ''::character varying,
    menu_ids            varchar(3000) default ''::character varying,
    remark              varchar(200)  default ''::character varying,
    menu_check_strictly boolean       default true,
    status              char          default '0'::bpchar,
    del_flag            char          default '0'::bpchar,
    create_dept         bigint,
    create_by           bigint,
    create_time         timestamp,
    update_by           bigint,
    update_time         timestamp
);

comment on table sys_tenant_package is '租户套餐表';

comment on column sys_tenant_package.package_id is '租户套餐id';

comment on column sys_tenant_package.package_name is '套餐名称';

comment on column sys_tenant_package.menu_ids is '关联菜单id';

comment on column sys_tenant_package.remark is '备注';

comment on column sys_tenant_package.status is '状态（0正常 1停用）';

comment on column sys_tenant_package.del_flag is '删除标志（0代表存在 1代表删除）';

comment on column sys_tenant_package.create_dept is '创建部门';

comment on column sys_tenant_package.create_by is '创建者';

comment on column sys_tenant_package.create_time is '创建时间';

comment on column sys_tenant_package.update_by is '更新者';

comment on column sys_tenant_package.update_time is '更新时间';

alter table sys_tenant_package
    owner to astra;


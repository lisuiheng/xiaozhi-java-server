create table sys_tenant
(
    id                bigint      not null
        constraint pk_sys_tenant
            primary key,
    tenant_id         varchar(20) not null,
    contact_user_name varchar(20)  default NULL::character varying,
    contact_phone     varchar(20)  default NULL::character varying,
    company_name      varchar(30)  default NULL::character varying,
    license_number    varchar(30)  default NULL::character varying,
    address           varchar(200) default NULL::character varying,
    intro             varchar(200) default NULL::character varying,
    domain            varchar(200) default NULL::character varying,
    remark            varchar(200) default NULL::character varying,
    package_id        bigint,
    expire_time       timestamp,
    account_count     integer      default '-1'::integer,
    status            char         default '0'::bpchar,
    del_flag          char         default '0'::bpchar,
    create_dept       bigint,
    create_by         bigint,
    create_time       timestamp,
    update_by         bigint,
    update_time       timestamp
);

comment on table sys_tenant is '租户表';

comment on column sys_tenant.tenant_id is '租户编号';

comment on column sys_tenant.contact_phone is '联系电话';

comment on column sys_tenant.company_name is '联系人';

comment on column sys_tenant.license_number is '统一社会信用代码';

comment on column sys_tenant.address is '地址';

comment on column sys_tenant.intro is '企业简介';

comment on column sys_tenant.domain is '域名';

comment on column sys_tenant.remark is '备注';

comment on column sys_tenant.package_id is '租户套餐编号';

comment on column sys_tenant.expire_time is '过期时间';

comment on column sys_tenant.account_count is '用户数量（-1不限制）';

comment on column sys_tenant.status is '租户状态（0正常 1停用）';

comment on column sys_tenant.del_flag is '删除标志（0代表存在 1代表删除）';

comment on column sys_tenant.create_dept is '创建部门';

comment on column sys_tenant.create_by is '创建者';

comment on column sys_tenant.create_time is '创建时间';

comment on column sys_tenant.update_by is '更新者';

comment on column sys_tenant.update_time is '更新时间';

alter table sys_tenant
    owner to astra;

INSERT INTO public.sys_tenant (id, tenant_id, contact_user_name, contact_phone, company_name, license_number, address, intro, domain, remark, package_id, expire_time, account_count, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (1, '000000', '管理组', '15888888888', 'XXX有限公司', null, null, '多租户通用后台管理管理系统', null, null, null, null, -1, '0', '0', 103, 1, '2025-11-25 12:11:20.134328', null, null);

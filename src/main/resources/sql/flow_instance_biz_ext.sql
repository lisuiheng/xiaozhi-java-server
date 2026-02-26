create table flow_instance_biz_ext
(
    id             bigint not null
        primary key,
    tenant_id      varchar(20) default '000000'::character varying,
    create_dept    bigint,
    create_by      bigint,
    create_time    timestamp,
    update_by      bigint,
    update_time    timestamp,
    business_code  varchar(255),
    business_title varchar(1000),
    del_flag       char        default '0'::bpchar,
    instance_id    bigint,
    business_id    varchar(255)
);

comment on table flow_instance_biz_ext is '流程实例业务扩展表';

comment on column flow_instance_biz_ext.id is '主键id';

comment on column flow_instance_biz_ext.tenant_id is '租户编号';

comment on column flow_instance_biz_ext.create_dept is '创建部门';

comment on column flow_instance_biz_ext.create_by is '创建者';

comment on column flow_instance_biz_ext.create_time is '创建时间';

comment on column flow_instance_biz_ext.update_by is '更新者';

comment on column flow_instance_biz_ext.update_time is '更新时间';

comment on column flow_instance_biz_ext.business_code is '业务编码';

comment on column flow_instance_biz_ext.business_title is '业务标题';

comment on column flow_instance_biz_ext.del_flag is '删除标志（0代表存在 1代表删除）';

comment on column flow_instance_biz_ext.instance_id is '流程实例Id';

comment on column flow_instance_biz_ext.business_id is '业务Id';

alter table flow_instance_biz_ext
    owner to astra;


create table flow_user
(
    id           bigint not null
        constraint flow_user_pk
            primary key,
    type         char   not null,
    processed_by varchar(80),
    associated   bigint not null,
    create_time  timestamp,
    create_by    varchar(64) default ''::character varying,
    update_time  timestamp,
    update_by    varchar(64) default ''::character varying,
    del_flag     char        default '0'::character varying,
    tenant_id    varchar(40)
);

comment on table flow_user is '流程用户表';

comment on column flow_user.id is '主键id';

comment on column flow_user.type is '人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）';

comment on column flow_user.processed_by is '权限人';

comment on column flow_user.associated is '任务表id';

comment on column flow_user.create_time is '创建时间';

comment on column flow_user.create_by is '创建人';

comment on column flow_user.update_time is '更新时间';

comment on column flow_user.update_by is '更新人';

comment on column flow_user.del_flag is '删除标志';

comment on column flow_user.tenant_id is '租户id';

alter table flow_user
    owner to astra;

create index user_processed_type
    on flow_user (processed_by, type);

create index user_associated_idx
    on flow_user (associated);


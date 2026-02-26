create table flow_instance
(
    id              bigint                not null
        primary key,
    definition_id   bigint                not null,
    business_id     varchar(40)           not null,
    node_type       smallint              not null,
    node_code       varchar(40)           not null,
    node_name       varchar(100),
    variable        text,
    flow_status     varchar(20)           not null,
    activity_status smallint    default 1 not null,
    def_json        text,
    create_time     timestamp,
    create_by       varchar(64) default ''::character varying,
    update_time     timestamp,
    update_by       varchar(64) default ''::character varying,
    ext             varchar(500),
    del_flag        char        default '0'::character varying,
    tenant_id       varchar(40)
);

comment on table flow_instance is '流程实例表';

comment on column flow_instance.id is '主键id';

comment on column flow_instance.definition_id is '对应flow_definition表的id';

comment on column flow_instance.business_id is '业务id';

comment on column flow_instance.node_type is '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';

comment on column flow_instance.node_code is '流程节点编码';

comment on column flow_instance.node_name is '流程节点名称';

comment on column flow_instance.variable is '任务变量';

comment on column flow_instance.flow_status is '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';

comment on column flow_instance.activity_status is '流程激活状态（0挂起 1激活）';

comment on column flow_instance.def_json is '流程定义json';

comment on column flow_instance.create_time is '创建时间';

comment on column flow_instance.create_by is '创建人';

comment on column flow_instance.update_time is '更新时间';

comment on column flow_instance.update_by is '更新人';

comment on column flow_instance.ext is '扩展字段，预留给业务系统使用';

comment on column flow_instance.del_flag is '删除标志';

comment on column flow_instance.tenant_id is '租户id';

alter table flow_instance
    owner to astra;


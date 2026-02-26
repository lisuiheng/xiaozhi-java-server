create table flow_task
(
    id            bigint       not null
        primary key,
    definition_id bigint       not null,
    instance_id   bigint       not null,
    node_code     varchar(100) not null,
    node_name     varchar(100),
    node_type     smallint     not null,
    flow_status   varchar(20)  not null,
    form_custom   char        default 'N'::character varying,
    form_path     varchar(100),
    create_time   timestamp,
    create_by     varchar(64) default ''::character varying,
    update_time   timestamp,
    update_by     varchar(64) default ''::character varying,
    del_flag      char        default '0'::character varying,
    tenant_id     varchar(40)
);

comment on table flow_task is '待办任务表';

comment on column flow_task.id is '主键id';

comment on column flow_task.definition_id is '对应flow_definition表的id';

comment on column flow_task.instance_id is '对应flow_instance表的id';

comment on column flow_task.node_code is '节点编码';

comment on column flow_task.node_name is '节点名称';

comment on column flow_task.node_type is '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';

comment on column flow_task.flow_status is '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';

comment on column flow_task.form_custom is '审批表单是否自定义（Y是 N否）';

comment on column flow_task.form_path is '审批表单路径';

comment on column flow_task.create_time is '创建时间';

comment on column flow_task.create_by is '创建人';

comment on column flow_task.update_time is '更新时间';

comment on column flow_task.update_by is '更新人';

comment on column flow_task.del_flag is '删除标志';

comment on column flow_task.tenant_id is '租户id';

alter table flow_task
    owner to astra;


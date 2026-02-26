create table flow_his_task
(
    id               bigint             not null
        primary key,
    definition_id    bigint             not null,
    instance_id      bigint             not null,
    task_id          bigint             not null,
    node_code        varchar(100),
    node_name        varchar(100),
    node_type        smallint,
    target_node_code varchar(200),
    target_node_name varchar(200),
    approver         varchar(40),
    cooperate_type   smallint default 0 not null,
    collaborator     varchar(500),
    skip_type        varchar(10),
    flow_status      varchar(20)        not null,
    form_custom      char     default 'N'::character varying,
    form_path        varchar(100),
    ext              text,
    message          varchar(500),
    variable         text,
    create_time      timestamp,
    update_time      timestamp,
    del_flag         char     default '0'::character varying,
    tenant_id        varchar(40)
);

comment on table flow_his_task is '历史任务记录表';

comment on column flow_his_task.id is '主键id';

comment on column flow_his_task.definition_id is '对应flow_definition表的id';

comment on column flow_his_task.instance_id is '对应flow_instance表的id';

comment on column flow_his_task.task_id is '对应flow_task表的id';

comment on column flow_his_task.node_code is '开始节点编码';

comment on column flow_his_task.node_name is '开始节点名称';

comment on column flow_his_task.node_type is '开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';

comment on column flow_his_task.target_node_code is '目标节点编码';

comment on column flow_his_task.target_node_name is '结束节点名称';

comment on column flow_his_task.approver is '审批者';

comment on column flow_his_task.cooperate_type is '协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)';

comment on column flow_his_task.collaborator is '协作人';

comment on column flow_his_task.skip_type is '流转类型（PASS通过 REJECT退回 NONE无动作）';

comment on column flow_his_task.flow_status is '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';

comment on column flow_his_task.form_custom is '审批表单是否自定义（Y是 N否）';

comment on column flow_his_task.form_path is '审批表单路径';

comment on column flow_his_task.ext is '扩展字段，预留给业务系统使用';

comment on column flow_his_task.message is '审批意见';

comment on column flow_his_task.variable is '任务变量';

comment on column flow_his_task.create_time is '任务开始时间';

comment on column flow_his_task.update_time is '审批完成时间';

comment on column flow_his_task.del_flag is '删除标志';

comment on column flow_his_task.tenant_id is '租户id';

alter table flow_his_task
    owner to astra;


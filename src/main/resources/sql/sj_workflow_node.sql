create table sj_workflow_node
(
    id                   bigserial
        primary key,
    namespace_id         varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    node_name            varchar(64)                                                                not null,
    group_name           varchar(64)                                                                not null,
    job_id               bigint                                                                     not null,
    workflow_id          bigint                                                                     not null,
    node_type            smallint     default 1                                                     not null,
    expression_type      smallint     default 0                                                     not null,
    fail_strategy        smallint     default 1                                                     not null,
    workflow_node_status smallint     default 1                                                     not null,
    priority_level       integer      default 1                                                     not null,
    node_info            text,
    version              integer                                                                    not null,
    ext_attrs            varchar(256) default ''::character varying,
    deleted              smallint     default 0                                                     not null,
    create_dt            timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt            timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_workflow_node is '工作流节点';

comment on column sj_workflow_node.id is '主键';

comment on column sj_workflow_node.namespace_id is '命名空间id';

comment on column sj_workflow_node.node_name is '节点名称';

comment on column sj_workflow_node.group_name is '组名称';

comment on column sj_workflow_node.job_id is '任务信息id';

comment on column sj_workflow_node.workflow_id is '工作流ID';

comment on column sj_workflow_node.node_type is '1、任务节点 2、条件节点';

comment on column sj_workflow_node.expression_type is '1、SpEl、2、Aviator 3、QL';

comment on column sj_workflow_node.fail_strategy is '失败策略 1、跳过 2、阻塞';

comment on column sj_workflow_node.workflow_node_status is '工作流节点状态 0、关闭、1、开启';

comment on column sj_workflow_node.priority_level is '优先级';

comment on column sj_workflow_node.node_info is '节点信息 ';

comment on column sj_workflow_node.version is '版本号';

comment on column sj_workflow_node.ext_attrs is '扩展字段';

comment on column sj_workflow_node.deleted is '逻辑删除 1、删除';

comment on column sj_workflow_node.create_dt is '创建时间';

comment on column sj_workflow_node.update_dt is '修改时间';

alter table sj_workflow_node
    owner to astra;

create index idx_sj_workflow_node_01
    on sj_workflow_node (create_dt);

create index idx_sj_workflow_node_02
    on sj_workflow_node (namespace_id, group_name);


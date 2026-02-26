create table sj_workflow
(
    id               bigserial
        primary key,
    workflow_name    varchar(64)                                                                not null,
    namespace_id     varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name       varchar(64)                                                                not null,
    workflow_status  smallint     default 1                                                     not null,
    trigger_type     smallint                                                                   not null,
    trigger_interval varchar(255)                                                               not null,
    next_trigger_at  bigint                                                                     not null,
    block_strategy   smallint     default 1                                                     not null,
    executor_timeout integer      default 0                                                     not null,
    description      varchar(256) default ''::character varying                                 not null,
    flow_info        text,
    wf_context       text,
    notify_ids       varchar(128) default ''::character varying                                 not null,
    bucket_index     integer      default 0                                                     not null,
    version          integer                                                                    not null,
    owner_id         bigint,
    ext_attrs        varchar(256) default ''::character varying,
    deleted          smallint     default 0                                                     not null,
    create_dt        timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt        timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_workflow is '工作流';

comment on column sj_workflow.id is '主键';

comment on column sj_workflow.workflow_name is '工作流名称';

comment on column sj_workflow.namespace_id is '命名空间id';

comment on column sj_workflow.group_name is '组名称';

comment on column sj_workflow.workflow_status is '工作流状态 0、关闭、1、开启';

comment on column sj_workflow.trigger_type is '触发类型 1.CRON 表达式 2. 固定时间';

comment on column sj_workflow.trigger_interval is '间隔时长';

comment on column sj_workflow.next_trigger_at is '下次触发时间';

comment on column sj_workflow.block_strategy is '阻塞策略 1、丢弃 2、覆盖 3、并行';

comment on column sj_workflow.executor_timeout is '任务执行超时时间，单位秒';

comment on column sj_workflow.description is '描述';

comment on column sj_workflow.flow_info is '流程信息';

comment on column sj_workflow.wf_context is '上下文';

comment on column sj_workflow.notify_ids is '通知告警场景配置id列表';

comment on column sj_workflow.bucket_index is 'bucket';

comment on column sj_workflow.version is '版本号';

comment on column sj_workflow.owner_id is '负责人id';

comment on column sj_workflow.ext_attrs is '扩展字段';

comment on column sj_workflow.deleted is '逻辑删除 1、删除';

comment on column sj_workflow.create_dt is '创建时间';

comment on column sj_workflow.update_dt is '修改时间';

alter table sj_workflow
    owner to astra;

create index idx_sj_workflow_01
    on sj_workflow (create_dt);

create index idx_sj_workflow_02
    on sj_workflow (namespace_id, group_name);


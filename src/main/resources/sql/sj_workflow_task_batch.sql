create table sj_workflow_task_batch
(
    id                bigserial
        primary key,
    namespace_id      varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name        varchar(64)                                                                not null,
    workflow_id       bigint                                                                     not null,
    task_batch_status smallint     default 0                                                     not null,
    operation_reason  smallint     default 0                                                     not null,
    flow_info         text,
    wf_context        text,
    execution_at      bigint       default 0                                                     not null,
    ext_attrs         varchar(256) default ''::character varying,
    version           integer      default 1                                                     not null,
    deleted           smallint     default 0                                                     not null,
    create_dt         timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt         timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_workflow_task_batch is '工作流批次';

comment on column sj_workflow_task_batch.id is '主键';

comment on column sj_workflow_task_batch.namespace_id is '命名空间id';

comment on column sj_workflow_task_batch.group_name is '组名称';

comment on column sj_workflow_task_batch.workflow_id is '工作流任务id';

comment on column sj_workflow_task_batch.task_batch_status is '任务批次状态 0、失败 1、成功';

comment on column sj_workflow_task_batch.operation_reason is '操作原因';

comment on column sj_workflow_task_batch.flow_info is '流程信息';

comment on column sj_workflow_task_batch.wf_context is '全局上下文';

comment on column sj_workflow_task_batch.execution_at is '任务执行时间';

comment on column sj_workflow_task_batch.ext_attrs is '扩展字段';

comment on column sj_workflow_task_batch.version is '版本号';

comment on column sj_workflow_task_batch.deleted is '逻辑删除 1、删除';

comment on column sj_workflow_task_batch.create_dt is '创建时间';

comment on column sj_workflow_task_batch.update_dt is '修改时间';

alter table sj_workflow_task_batch
    owner to astra;

create index idx_sj_workflow_task_batch_01
    on sj_workflow_task_batch (workflow_id, task_batch_status);

create index idx_sj_workflow_task_batch_02
    on sj_workflow_task_batch (create_dt);

create index idx_sj_workflow_task_batch_03
    on sj_workflow_task_batch (namespace_id, group_name);


create table sj_retry_task
(
    id               bigserial
        primary key,
    namespace_id     varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name       varchar(64)                                                                not null,
    scene_name       varchar(64)                                                                not null,
    retry_id         bigint                                                                     not null,
    ext_attrs        text                                                                       not null,
    task_status      smallint     default 1                                                     not null,
    task_type        smallint     default 1                                                     not null,
    operation_reason smallint     default 0                                                     not null,
    client_info      varchar(128) default NULL::character varying,
    create_dt        timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt        timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_retry_task is '重试任务表';

comment on column sj_retry_task.id is '主键';

comment on column sj_retry_task.namespace_id is '命名空间id';

comment on column sj_retry_task.group_name is '组名称';

comment on column sj_retry_task.scene_name is '场景名称';

comment on column sj_retry_task.retry_id is '重试信息Id';

comment on column sj_retry_task.ext_attrs is '扩展字段';

comment on column sj_retry_task.task_status is '重试状态';

comment on column sj_retry_task.task_type is '任务类型 1、重试数据 2、回调数据';

comment on column sj_retry_task.operation_reason is '操作原因';

comment on column sj_retry_task.client_info is '客户端地址 clientId#ip:port';

comment on column sj_retry_task.create_dt is '创建时间';

comment on column sj_retry_task.update_dt is '修改时间';

alter table sj_retry_task
    owner to astra;

create index idx_sj_retry_task_01
    on sj_retry_task (namespace_id, group_name, scene_name);

create index idx_sj_retry_task_02
    on sj_retry_task (task_status);

create index idx_sj_retry_task_03
    on sj_retry_task (create_dt);

create index idx_sj_retry_task_04
    on sj_retry_task (retry_id);


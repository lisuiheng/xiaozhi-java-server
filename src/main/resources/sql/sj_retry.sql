create table sj_retry
(
    id              bigserial
        primary key,
    namespace_id    varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name      varchar(64)                                                                not null,
    group_id        bigint                                                                     not null,
    scene_name      varchar(64)                                                                not null,
    scene_id        bigint                                                                     not null,
    idempotent_id   varchar(64)                                                                not null,
    biz_no          varchar(64)  default ''::character varying                                 not null,
    executor_name   varchar(512) default ''::character varying                                 not null,
    args_str        text                                                                       not null,
    ext_attrs       text                                                                       not null,
    serializer_name varchar(32)  default 'jackson'::character varying                          not null,
    next_trigger_at bigint                                                                     not null,
    retry_count     integer      default 0                                                     not null,
    retry_status    smallint     default 0                                                     not null,
    task_type       smallint     default 1                                                     not null,
    bucket_index    integer      default 0                                                     not null,
    parent_id       bigint       default 0                                                     not null,
    deleted         bigint       default 0                                                     not null,
    create_dt       timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt       timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_retry is '重试信息表';

comment on column sj_retry.id is '主键';

comment on column sj_retry.namespace_id is '命名空间id';

comment on column sj_retry.group_name is '组名称';

comment on column sj_retry.group_id is '组Id';

comment on column sj_retry.scene_name is '场景名称';

comment on column sj_retry.scene_id is '场景ID';

comment on column sj_retry.idempotent_id is '幂等id';

comment on column sj_retry.biz_no is '业务编号';

comment on column sj_retry.executor_name is '执行器名称';

comment on column sj_retry.args_str is '执行方法参数';

comment on column sj_retry.ext_attrs is '扩展字段';

comment on column sj_retry.serializer_name is '执行方法参数序列化器名称';

comment on column sj_retry.next_trigger_at is '下次触发时间';

comment on column sj_retry.retry_count is '重试次数';

comment on column sj_retry.retry_status is '重试状态 0、重试中 1、成功 2、最大重试次数';

comment on column sj_retry.task_type is '任务类型 1、重试数据 2、回调数据';

comment on column sj_retry.bucket_index is 'bucket';

comment on column sj_retry.parent_id is '父节点id';

comment on column sj_retry.deleted is '逻辑删除';

comment on column sj_retry.create_dt is '创建时间';

comment on column sj_retry.update_dt is '修改时间';

alter table sj_retry
    owner to astra;

create unique index uk_sj_retry_01
    on sj_retry (scene_id, task_type, idempotent_id, deleted);

create index idx_sj_retry_01
    on sj_retry (biz_no);

create index idx_sj_retry_02
    on sj_retry (idempotent_id);

create index idx_sj_retry_03
    on sj_retry (retry_status, bucket_index);

create index idx_sj_retry_04
    on sj_retry (parent_id);

create index idx_sj_retry_05
    on sj_retry (create_dt);


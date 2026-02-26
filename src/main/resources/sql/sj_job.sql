create table sj_job
(
    id               bigserial
        primary key,
    namespace_id     varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name       varchar(64)                                                                not null,
    job_name         varchar(64)                                                                not null,
    args_str         text,
    args_type        smallint     default 1                                                     not null,
    next_trigger_at  bigint                                                                     not null,
    job_status       smallint     default 1                                                     not null,
    task_type        smallint     default 1                                                     not null,
    route_key        smallint     default 4                                                     not null,
    executor_type    smallint     default 1                                                     not null,
    executor_info    varchar(255) default NULL::character varying,
    trigger_type     smallint                                                                   not null,
    trigger_interval varchar(255)                                                               not null,
    block_strategy   smallint     default 1                                                     not null,
    executor_timeout integer      default 0                                                     not null,
    max_retry_times  integer      default 0                                                     not null,
    parallel_num     integer      default 1                                                     not null,
    retry_interval   integer      default 0                                                     not null,
    bucket_index     integer      default 0                                                     not null,
    resident         smallint     default 0                                                     not null,
    notify_ids       varchar(128) default ''::character varying                                 not null,
    owner_id         bigint,
    labels           varchar(512) default ''::character varying,
    description      varchar(256) default ''::character varying                                 not null,
    ext_attrs        varchar(256) default ''::character varying,
    deleted          smallint     default 0                                                     not null,
    create_dt        timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt        timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_job is '任务信息';

comment on column sj_job.id is '主键';

comment on column sj_job.namespace_id is '命名空间id';

comment on column sj_job.group_name is '组名称';

comment on column sj_job.job_name is '名称';

comment on column sj_job.args_str is '执行方法参数';

comment on column sj_job.args_type is '参数类型 ';

comment on column sj_job.next_trigger_at is '下次触发时间';

comment on column sj_job.job_status is '任务状态 0、关闭、1、开启';

comment on column sj_job.task_type is '任务类型 1、集群 2、广播 3、切片';

comment on column sj_job.route_key is '路由策略';

comment on column sj_job.executor_type is '执行器类型';

comment on column sj_job.executor_info is '执行器名称';

comment on column sj_job.trigger_type is '触发类型 1.CRON 表达式 2. 固定时间';

comment on column sj_job.trigger_interval is '间隔时长';

comment on column sj_job.block_strategy is '阻塞策略 1、丢弃 2、覆盖 3、并行 4、恢复';

comment on column sj_job.executor_timeout is '任务执行超时时间，单位秒';

comment on column sj_job.max_retry_times is '最大重试次数';

comment on column sj_job.parallel_num is '并行数';

comment on column sj_job.retry_interval is '重试间隔 ( s)';

comment on column sj_job.bucket_index is 'bucket';

comment on column sj_job.resident is '是否是常驻任务';

comment on column sj_job.notify_ids is '通知告警场景配置id列表';

comment on column sj_job.owner_id is '负责人id';

comment on column sj_job.labels is '标签';

comment on column sj_job.description is '描述';

comment on column sj_job.ext_attrs is '扩展字段';

comment on column sj_job.deleted is '逻辑删除 1、删除';

comment on column sj_job.create_dt is '创建时间';

comment on column sj_job.update_dt is '修改时间';

alter table sj_job
    owner to astra;

create index idx_sj_job_01
    on sj_job (namespace_id, group_name);

create index idx_sj_job_02
    on sj_job (job_status, bucket_index);

create index idx_sj_job_03
    on sj_job (create_dt);

INSERT INTO public.sj_job (id, namespace_id, group_name, job_name, args_str, args_type, next_trigger_at, job_status, task_type, route_key, executor_type, executor_info, trigger_type, trigger_interval, block_strategy, executor_timeout, max_retry_times, parallel_num, retry_interval, bucket_index, resident, notify_ids, owner_id, labels, description, ext_attrs, deleted, create_dt, update_dt) VALUES (1, 'dev', 'ruoyi_group', 'demo-job', null, 1, 1768732999757, 1, 1, 4, 1, 'testJobExecutor', 2, '60', 1, 60, 3, 1, 1, 116, 0, '', 1, '', '', '', 0, '2025-11-25 12:12:43.496571', '2026-01-18 18:42:19.746877');

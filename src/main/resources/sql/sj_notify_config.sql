create table sj_notify_config
(
    id                     bigserial
        primary key,
    namespace_id           varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name             varchar(64)                                                                not null,
    notify_name            varchar(64)  default ''::character varying                                 not null,
    system_task_type       smallint     default 3                                                     not null,
    notify_status          smallint     default 0                                                     not null,
    recipient_ids          varchar(128)                                                               not null,
    notify_threshold       integer      default 0                                                     not null,
    notify_scene           smallint     default 0                                                     not null,
    rate_limiter_status    smallint     default 0                                                     not null,
    rate_limiter_threshold integer      default 0                                                     not null,
    description            varchar(256) default ''::character varying                                 not null,
    create_dt              timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt              timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_notify_config is '通知配置';

comment on column sj_notify_config.id is '主键';

comment on column sj_notify_config.namespace_id is '命名空间id';

comment on column sj_notify_config.group_name is '组名称';

comment on column sj_notify_config.notify_name is '通知名称';

comment on column sj_notify_config.system_task_type is '任务类型 1. 重试任务 2. 重试回调 3、JOB任务 4、WORKFLOW任务';

comment on column sj_notify_config.notify_status is '通知状态 0、未启用 1、启用';

comment on column sj_notify_config.recipient_ids is '接收人id列表';

comment on column sj_notify_config.notify_threshold is '通知阈值';

comment on column sj_notify_config.notify_scene is '通知场景';

comment on column sj_notify_config.rate_limiter_status is '限流状态 0、未启用 1、启用';

comment on column sj_notify_config.rate_limiter_threshold is '每秒限流阈值';

comment on column sj_notify_config.description is '描述';

comment on column sj_notify_config.create_dt is '创建时间';

comment on column sj_notify_config.update_dt is '修改时间';

alter table sj_notify_config
    owner to astra;

create index idx_sj_notify_config_01
    on sj_notify_config (namespace_id, group_name);


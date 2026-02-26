create table sj_retry_scene_config
(
    id                  bigserial
        primary key,
    namespace_id        varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    scene_name          varchar(64)                                                                not null,
    group_name          varchar(64)                                                                not null,
    scene_status        smallint     default 0                                                     not null,
    max_retry_count     integer      default 5                                                     not null,
    back_off            smallint     default 1                                                     not null,
    trigger_interval    varchar(16)  default ''::character varying                                 not null,
    notify_ids          varchar(128) default ''::character varying                                 not null,
    deadline_request    bigint       default 60000                                                 not null,
    executor_timeout    integer      default 5                                                     not null,
    route_key           smallint     default 4                                                     not null,
    block_strategy      smallint     default 1                                                     not null,
    cb_status           smallint     default 0                                                     not null,
    cb_trigger_type     smallint     default 1                                                     not null,
    cb_max_count        integer      default 16                                                    not null,
    cb_trigger_interval varchar(16)  default ''::character varying                                 not null,
    owner_id            bigint,
    labels              varchar(512) default ''::character varying,
    description         varchar(256) default ''::character varying                                 not null,
    create_dt           timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt           timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_retry_scene_config is '场景配置';

comment on column sj_retry_scene_config.id is '主键';

comment on column sj_retry_scene_config.namespace_id is '命名空间id';

comment on column sj_retry_scene_config.scene_name is '场景名称';

comment on column sj_retry_scene_config.group_name is '组名称';

comment on column sj_retry_scene_config.scene_status is '组状态 0、未启用 1、启用';

comment on column sj_retry_scene_config.max_retry_count is '最大重试次数';

comment on column sj_retry_scene_config.back_off is '1、默认等级 2、固定间隔时间 3、CRON 表达式';

comment on column sj_retry_scene_config.trigger_interval is '间隔时长';

comment on column sj_retry_scene_config.notify_ids is '通知告警场景配置id列表';

comment on column sj_retry_scene_config.deadline_request is 'Deadline Request 调用链超时 单位毫秒';

comment on column sj_retry_scene_config.executor_timeout is '任务执行超时时间，单位秒';

comment on column sj_retry_scene_config.route_key is '路由策略';

comment on column sj_retry_scene_config.block_strategy is '阻塞策略 1、丢弃 2、覆盖 3、并行';

comment on column sj_retry_scene_config.cb_status is '回调状态 0、不开启 1、开启';

comment on column sj_retry_scene_config.cb_trigger_type is '1、默认等级 2、固定间隔时间 3、CRON 表达式';

comment on column sj_retry_scene_config.cb_max_count is '回调的最大执行次数';

comment on column sj_retry_scene_config.cb_trigger_interval is '回调的最大执行次数';

comment on column sj_retry_scene_config.owner_id is '负责人id';

comment on column sj_retry_scene_config.labels is '标签';

comment on column sj_retry_scene_config.description is '描述';

comment on column sj_retry_scene_config.create_dt is '创建时间';

comment on column sj_retry_scene_config.update_dt is '修改时间';

alter table sj_retry_scene_config
    owner to astra;

create unique index uk_sj_retry_scene_config_01
    on sj_retry_scene_config (namespace_id, group_name, scene_name);


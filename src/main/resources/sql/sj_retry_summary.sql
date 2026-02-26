create table sj_retry_summary
(
    id            bigserial
        primary key,
    namespace_id  varchar(64) default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name    varchar(64) default ''::character varying                                 not null,
    scene_name    varchar(50) default ''::character varying                                 not null,
    trigger_at    timestamp   default CURRENT_TIMESTAMP                                     not null,
    running_num   integer     default 0                                                     not null,
    finish_num    integer     default 0                                                     not null,
    max_count_num integer     default 0                                                     not null,
    suspend_num   integer     default 0                                                     not null,
    create_dt     timestamp   default CURRENT_TIMESTAMP                                     not null,
    update_dt     timestamp   default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_retry_summary is 'DashBoard_Retry';

comment on column sj_retry_summary.id is '主键';

comment on column sj_retry_summary.namespace_id is '命名空间id';

comment on column sj_retry_summary.group_name is '组名称';

comment on column sj_retry_summary.scene_name is '场景名称';

comment on column sj_retry_summary.trigger_at is '统计时间';

comment on column sj_retry_summary.running_num is '重试中-日志数量';

comment on column sj_retry_summary.finish_num is '重试完成-日志数量';

comment on column sj_retry_summary.max_count_num is '重试到达最大次数-日志数量';

comment on column sj_retry_summary.suspend_num is '暂停重试-日志数量';

comment on column sj_retry_summary.create_dt is '创建时间';

comment on column sj_retry_summary.update_dt is '修改时间';

alter table sj_retry_summary
    owner to astra;

create unique index uk_sj_retry_summary_01
    on sj_retry_summary (namespace_id, group_name, scene_name, trigger_at);

create index idx_sj_retry_summary_01
    on sj_retry_summary (trigger_at);


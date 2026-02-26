create table sj_retry_task_log_message
(
    id            bigserial
        primary key,
    namespace_id  varchar(64) default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name    varchar(64)                                                               not null,
    retry_id      bigint                                                                    not null,
    retry_task_id bigint                                                                    not null,
    message       text                                                                      not null,
    log_num       integer     default 1                                                     not null,
    real_time     bigint      default 0                                                     not null,
    create_dt     timestamp   default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_retry_task_log_message is '任务调度日志信息记录表';

comment on column sj_retry_task_log_message.id is '主键';

comment on column sj_retry_task_log_message.namespace_id is '命名空间id';

comment on column sj_retry_task_log_message.group_name is '组名称';

comment on column sj_retry_task_log_message.retry_id is '重试信息Id';

comment on column sj_retry_task_log_message.retry_task_id is '重试任务Id';

comment on column sj_retry_task_log_message.message is '异常信息';

comment on column sj_retry_task_log_message.log_num is '日志数量';

comment on column sj_retry_task_log_message.real_time is '上报时间';

comment on column sj_retry_task_log_message.create_dt is '创建时间';

alter table sj_retry_task_log_message
    owner to astra;

create index idx_sj_retry_task_log_message_01
    on sj_retry_task_log_message (namespace_id, group_name, retry_task_id);

create index idx_sj_retry_task_log_message_02
    on sj_retry_task_log_message (create_dt);


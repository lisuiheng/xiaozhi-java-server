create table sj_retry_dead_letter
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
    serializer_name varchar(32)  default 'jackson'::character varying                          not null,
    args_str        text                                                                       not null,
    ext_attrs       text                                                                       not null,
    create_dt       timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_retry_dead_letter is '死信队列表';

comment on column sj_retry_dead_letter.id is '主键';

comment on column sj_retry_dead_letter.namespace_id is '命名空间id';

comment on column sj_retry_dead_letter.group_name is '组名称';

comment on column sj_retry_dead_letter.group_id is '组Id';

comment on column sj_retry_dead_letter.scene_name is '场景名称';

comment on column sj_retry_dead_letter.scene_id is '场景ID';

comment on column sj_retry_dead_letter.idempotent_id is '幂等id';

comment on column sj_retry_dead_letter.biz_no is '业务编号';

comment on column sj_retry_dead_letter.executor_name is '执行器名称';

comment on column sj_retry_dead_letter.serializer_name is '执行方法参数序列化器名称';

comment on column sj_retry_dead_letter.args_str is '执行方法参数';

comment on column sj_retry_dead_letter.ext_attrs is '扩展字段';

comment on column sj_retry_dead_letter.create_dt is '创建时间';

alter table sj_retry_dead_letter
    owner to astra;

create index idx_sj_retry_dead_letter_01
    on sj_retry_dead_letter (namespace_id, group_name, scene_name);

create index idx_sj_retry_dead_letter_02
    on sj_retry_dead_letter (idempotent_id);

create index idx_sj_retry_dead_letter_03
    on sj_retry_dead_letter (biz_no);

create index idx_sj_retry_dead_letter_04
    on sj_retry_dead_letter (create_dt);


create table sj_group_config
(
    id                bigserial
        primary key,
    namespace_id      varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying    not null,
    group_name        varchar(64)  default ''::character varying                                    not null,
    description       varchar(256) default ''::character varying                                    not null,
    token             varchar(64)  default 'SJ_cKqBTPzCsWA3VyuCfFoccmuIEGXjr5KT'::character varying not null,
    group_status      smallint     default 0                                                        not null,
    version           integer                                                                       not null,
    group_partition   integer                                                                       not null,
    id_generator_mode smallint     default 1                                                        not null,
    init_scene        smallint     default 0                                                        not null,
    create_dt         timestamp    default CURRENT_TIMESTAMP                                        not null,
    update_dt         timestamp    default CURRENT_TIMESTAMP                                        not null
);

comment on table sj_group_config is '组配置';

comment on column sj_group_config.id is '主键';

comment on column sj_group_config.namespace_id is '命名空间id';

comment on column sj_group_config.group_name is '组名称';

comment on column sj_group_config.description is '组描述';

comment on column sj_group_config.token is 'token';

comment on column sj_group_config.group_status is '组状态 0、未启用 1、启用';

comment on column sj_group_config.version is '版本号';

comment on column sj_group_config.group_partition is '分区';

comment on column sj_group_config.id_generator_mode is '唯一id生成模式 默认号段模式';

comment on column sj_group_config.init_scene is '是否初始化场景 0:否 1:是';

comment on column sj_group_config.create_dt is '创建时间';

comment on column sj_group_config.update_dt is '修改时间';

alter table sj_group_config
    owner to astra;

create unique index uk_sj_group_config_01
    on sj_group_config (namespace_id, group_name);

INSERT INTO public.sj_group_config (id, namespace_id, group_name, description, token, group_status, version, group_partition, id_generator_mode, init_scene, create_dt, update_dt) VALUES (1, 'dev', 'ruoyi_group', '', 'SJ_cKqBTPzCsWA3VyuCfFoccmuIEGXjr5KT', 1, 1, 0, 1, 1, '2025-11-25 12:12:39.208480', '2025-11-25 12:12:39.208480');
INSERT INTO public.sj_group_config (id, namespace_id, group_name, description, token, group_status, version, group_partition, id_generator_mode, init_scene, create_dt, update_dt) VALUES (2, 'prod', 'ruoyi_group', '', 'SJ_cKqBTPzCsWA3VyuCfFoccmuIEGXjr5KT', 1, 1, 0, 1, 1, '2025-11-25 12:12:39.208480', '2025-11-25 12:12:39.208480');

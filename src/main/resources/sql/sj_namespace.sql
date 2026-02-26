create table sj_namespace
(
    id          bigserial
        primary key,
    name        varchar(64)                                not null,
    unique_id   varchar(64)                                not null,
    description varchar(256) default ''::character varying not null,
    deleted     smallint     default 0                     not null,
    create_dt   timestamp    default CURRENT_TIMESTAMP     not null,
    update_dt   timestamp    default CURRENT_TIMESTAMP     not null
);

comment on table sj_namespace is '命名空间';

comment on column sj_namespace.id is '主键';

comment on column sj_namespace.name is '名称';

comment on column sj_namespace.unique_id is '唯一id';

comment on column sj_namespace.description is '描述';

comment on column sj_namespace.deleted is '逻辑删除 1、删除';

comment on column sj_namespace.create_dt is '创建时间';

comment on column sj_namespace.update_dt is '修改时间';

alter table sj_namespace
    owner to astra;

create index idx_sj_namespace_01
    on sj_namespace (name);

INSERT INTO public.sj_namespace (id, name, unique_id, description, deleted, create_dt, update_dt) VALUES (1, 'Development', 'dev', '', 0, '2025-11-25 12:12:38.772678', '2025-11-25 12:12:38.772678');
INSERT INTO public.sj_namespace (id, name, unique_id, description, deleted, create_dt, update_dt) VALUES (2, 'Production', 'prod', '', 0, '2025-11-25 12:12:38.772678', '2025-11-25 12:12:38.772678');

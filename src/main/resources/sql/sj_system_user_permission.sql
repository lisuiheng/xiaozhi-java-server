create table sj_system_user_permission
(
    id             bigserial
        primary key,
    group_name     varchar(64)                                                               not null,
    namespace_id   varchar(64) default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    system_user_id bigint                                                                    not null,
    create_dt      timestamp   default CURRENT_TIMESTAMP                                     not null,
    update_dt      timestamp   default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_system_user_permission is '系统用户权限表';

comment on column sj_system_user_permission.id is '主键';

comment on column sj_system_user_permission.group_name is '组名称';

comment on column sj_system_user_permission.namespace_id is '命名空间id';

comment on column sj_system_user_permission.system_user_id is '系统用户id';

comment on column sj_system_user_permission.create_dt is '创建时间';

comment on column sj_system_user_permission.update_dt is '修改时间';

alter table sj_system_user_permission
    owner to astra;

create unique index uk_sj_system_user_permission_01
    on sj_system_user_permission (namespace_id, group_name, system_user_id);


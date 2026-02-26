create table sj_system_user
(
    id        bigserial
        primary key,
    username  varchar(64)                         not null,
    password  varchar(128)                        not null,
    role      smallint  default 0                 not null,
    create_dt timestamp default CURRENT_TIMESTAMP not null,
    update_dt timestamp default CURRENT_TIMESTAMP not null
);

comment on table sj_system_user is '系统用户表';

comment on column sj_system_user.id is '主键';

comment on column sj_system_user.username is '账号';

comment on column sj_system_user.password is '密码';

comment on column sj_system_user.role is '角色：1-普通用户、2-管理员';

comment on column sj_system_user.create_dt is '创建时间';

comment on column sj_system_user.update_dt is '修改时间';

alter table sj_system_user
    owner to astra;

INSERT INTO public.sj_system_user (id, username, password, role, create_dt, update_dt) VALUES (1, 'admin', '465c194afb65670f38322df087f0a9bb225cc257e43eb4ac5a0c98ef5b3173ac', 2, '2025-11-25 12:12:42.473591', '2025-11-25 12:12:42.473591');

create table sys_user_role
(
    user_id bigint not null,
    role_id bigint not null,
    constraint sys_user_role_pk
        primary key (user_id, role_id)
);

comment on table sys_user_role is '用户和角色关联表';

comment on column sys_user_role.user_id is '用户ID';

comment on column sys_user_role.role_id is '角色ID';

alter table sys_user_role
    owner to astra;

INSERT INTO public.sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO public.sys_user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO public.sys_user_role (user_id, role_id) VALUES (4, 4);

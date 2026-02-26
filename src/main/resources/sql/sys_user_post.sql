create table sys_user_post
(
    user_id bigint not null,
    post_id bigint not null,
    constraint sys_user_post_pk
        primary key (user_id, post_id)
);

comment on table sys_user_post is '用户与岗位关联表';

comment on column sys_user_post.user_id is '用户ID';

comment on column sys_user_post.post_id is '岗位ID';

alter table sys_user_post
    owner to astra;

INSERT INTO public.sys_user_post (user_id, post_id) VALUES (1, 1);

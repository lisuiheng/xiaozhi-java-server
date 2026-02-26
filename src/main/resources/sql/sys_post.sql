create table sys_post
(
    post_id       bigint      not null
        constraint sys_post_pk
            primary key,
    tenant_id     varchar(20)  default '000000'::character varying,
    dept_id       bigint,
    post_code     varchar(64) not null,
    post_category varchar(100) default NULL::character varying,
    post_name     varchar(50) not null,
    post_sort     integer     not null,
    status        char        not null,
    create_dept   bigint,
    create_by     bigint,
    create_time   timestamp,
    update_by     bigint,
    update_time   timestamp,
    remark        varchar(500) default NULL::character varying
);

comment on table sys_post is '岗位信息表';

comment on column sys_post.post_id is '岗位ID';

comment on column sys_post.tenant_id is '租户编号';

comment on column sys_post.dept_id is '部门id';

comment on column sys_post.post_code is '岗位编码';

comment on column sys_post.post_category is '岗位类别编码';

comment on column sys_post.post_name is '岗位名称';

comment on column sys_post.post_sort is '显示顺序';

comment on column sys_post.status is '状态（0正常 1停用）';

comment on column sys_post.create_dept is '创建部门';

comment on column sys_post.create_by is '创建者';

comment on column sys_post.create_time is '创建时间';

comment on column sys_post.update_by is '更新者';

comment on column sys_post.update_time is '更新时间';

comment on column sys_post.remark is '备注';

alter table sys_post
    owner to astra;

INSERT INTO public.sys_post (post_id, tenant_id, dept_id, post_code, post_category, post_name, post_sort, status, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', 103, 'ceo', null, '董事长', 1, '0', 103, 1, '2025-11-25 12:11:21.818088', null, null, '');
INSERT INTO public.sys_post (post_id, tenant_id, dept_id, post_code, post_category, post_name, post_sort, status, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2, '000000', 100, 'se', null, '项目经理', 2, '0', 103, 1, '2025-11-25 12:11:21.818088', null, null, '');
INSERT INTO public.sys_post (post_id, tenant_id, dept_id, post_code, post_category, post_name, post_sort, status, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (3, '000000', 100, 'hr', null, '人力资源', 3, '0', 103, 1, '2025-11-25 12:11:21.818088', null, null, '');
INSERT INTO public.sys_post (post_id, tenant_id, dept_id, post_code, post_category, post_name, post_sort, status, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (4, '000000', 100, 'user', null, '普通员工', 4, '0', 103, 1, '2025-11-25 12:11:21.818088', null, null, '');

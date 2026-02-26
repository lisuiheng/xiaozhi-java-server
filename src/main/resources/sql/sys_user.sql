create table sys_user
(
    user_id     bigint      not null
        constraint sys_user_pk
            primary key,
    tenant_id   varchar(20)  default '000000'::character varying,
    dept_id     bigint,
    user_name   varchar(30) not null,
    nick_name   varchar(30) not null,
    user_type   varchar(10)  default 'sys_user'::character varying,
    email       varchar(50)  default ''::character varying,
    phonenumber varchar(11)  default ''::character varying,
    sex         char         default '0'::bpchar,
    avatar      bigint,
    password    varchar(100) default ''::character varying,
    status      char         default '0'::bpchar,
    del_flag    char         default '0'::bpchar,
    login_ip    varchar(128) default ''::character varying,
    login_date  timestamp,
    create_dept bigint,
    create_by   bigint,
    create_time timestamp,
    update_by   bigint,
    update_time timestamp,
    remark      varchar(500) default NULL::character varying
);

comment on table sys_user is '用户信息表';

comment on column sys_user.user_id is '用户ID';

comment on column sys_user.tenant_id is '租户编号';

comment on column sys_user.dept_id is '部门ID';

comment on column sys_user.user_name is '用户账号';

comment on column sys_user.nick_name is '用户昵称';

comment on column sys_user.user_type is '用户类型（sys_user系统用户）';

comment on column sys_user.email is '用户邮箱';

comment on column sys_user.phonenumber is '手机号码';

comment on column sys_user.sex is '用户性别（0男 1女 2未知）';

comment on column sys_user.avatar is '头像地址';

comment on column sys_user.password is '密码';

comment on column sys_user.status is '帐号状态（0正常 1停用）';

comment on column sys_user.del_flag is '删除标志（0代表存在 1代表删除）';

comment on column sys_user.login_ip is '最后登陆IP';

comment on column sys_user.login_date is '最后登陆时间';

comment on column sys_user.create_dept is '创建部门';

comment on column sys_user.create_by is '创建者';

comment on column sys_user.create_time is '创建时间';

comment on column sys_user.update_by is '更新者';

comment on column sys_user.update_time is '更新时间';

comment on column sys_user.remark is '备注';

alter table sys_user
    owner to astra;

INSERT INTO public.sys_user (user_id, tenant_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (3, '000000', 108, 'test', '本部门及以下 密码666666', 'sys_user', '', '', '0', null, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '127.0.0.1', '2025-11-25 12:11:21.403843', 103, 1, '2025-11-25 12:11:21.403843', 3, '2025-11-25 12:11:21.403843', '1');
INSERT INTO public.sys_user (user_id, tenant_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', 103, 'admin', 'lee', 'sys_user', 'crazyLionLi@163.com', '15888888888', '1', null, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-01-18 16:47:39.053000', 103, 1, '2025-11-25 12:11:21.403843', -1, '2026-01-18 16:47:39.054000', '管理员');

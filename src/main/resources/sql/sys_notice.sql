create table sys_notice
(
    notice_id      bigint      not null
        constraint sys_notice_pk
            primary key,
    tenant_id      varchar(20)  default '000000'::character varying,
    notice_title   varchar(50) not null,
    notice_type    char        not null,
    notice_content text,
    status         char         default '0'::bpchar,
    create_dept    bigint,
    create_by      bigint,
    create_time    timestamp,
    update_by      bigint,
    update_time    timestamp,
    remark         varchar(255) default NULL::character varying
);

comment on table sys_notice is '通知公告表';

comment on column sys_notice.notice_id is '公告ID';

comment on column sys_notice.tenant_id is '租户编号';

comment on column sys_notice.notice_title is '公告标题';

comment on column sys_notice.notice_type is '公告类型（1通知 2公告）';

comment on column sys_notice.notice_content is '公告内容';

comment on column sys_notice.status is '公告状态（0正常 1关闭）';

comment on column sys_notice.create_dept is '创建部门';

comment on column sys_notice.create_by is '创建者';

comment on column sys_notice.create_time is '创建时间';

comment on column sys_notice.update_by is '更新者';

comment on column sys_notice.update_time is '更新时间';

comment on column sys_notice.remark is '备注';

alter table sys_notice
    owner to astra;

INSERT INTO public.sys_notice (notice_id, tenant_id, notice_title, notice_type, notice_content, status, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2, '000000', '维护通知：2018-07-01 系统凌晨维护', '1', '维护内容', '0', 103, 1, '2025-11-25 12:11:25.831797', null, null, '管理员');
INSERT INTO public.sys_notice (notice_id, tenant_id, notice_title, notice_type, notice_content, status, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', '温馨提醒：2018-07-01 新版本发布啦', '2', '<p>新版本内容1</p>', '0', 103, 1, '2025-11-26 04:11:25.000000', null, '2026-01-19 11:36:59.432933', '管理员');

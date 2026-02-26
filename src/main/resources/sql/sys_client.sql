create table sys_client
(
    id             bigint not null
        constraint sys_client_pk
            primary key,
    client_id      varchar(64)  default ''::character varying,
    client_key     varchar(32)  default ''::character varying,
    client_secret  varchar(255) default ''::character varying,
    grant_type     varchar(255) default ''::character varying,
    device_type    varchar(32)  default ''::character varying,
    active_timeout integer      default 1800,
    timeout        integer      default 604800,
    status         char         default '0'::bpchar,
    del_flag       char         default '0'::bpchar,
    create_dept    bigint,
    create_by      bigint,
    create_time    timestamp,
    update_by      bigint,
    update_time    timestamp
);

comment on table sys_client is '系统授权表';

comment on column sys_client.id is '主键';

comment on column sys_client.client_id is '客户端id';

comment on column sys_client.client_key is '客户端key';

comment on column sys_client.client_secret is '客户端秘钥';

comment on column sys_client.grant_type is '授权类型';

comment on column sys_client.device_type is '设备类型';

comment on column sys_client.active_timeout is 'token活跃超时时间';

comment on column sys_client.timeout is 'token固定超时';

comment on column sys_client.status is '状态（0正常 1停用）';

comment on column sys_client.del_flag is '删除标志（0代表存在 1代表删除）';

comment on column sys_client.create_dept is '创建部门';

comment on column sys_client.create_by is '创建者';

comment on column sys_client.create_time is '创建时间';

comment on column sys_client.update_by is '更新者';

comment on column sys_client.update_time is '更新时间';

alter table sys_client
    owner to astra;

INSERT INTO public.sys_client (id, client_id, client_key, client_secret, grant_type, device_type, active_timeout, timeout, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (1, 'e5cd7e4891bf95d1d19206ce24a7b32e', 'pc', 'pc123', 'password,social', 'pc', 1800, 604800, '0', '0', 103, 1, '2025-11-25 12:11:28.062057', 1, '2025-11-25 12:11:28.062057');
INSERT INTO public.sys_client (id, client_id, client_key, client_secret, grant_type, device_type, active_timeout, timeout, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (2, '428a8310cd442757ae699df5d894f051', 'app', 'app123', 'password,sms', 'android', 1800, 604800, '0', '0', 103, 1, '2025-11-25 12:11:28.062057', null, '2026-01-14 17:44:26.316020');

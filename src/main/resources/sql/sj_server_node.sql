create table sj_server_node
(
    id           bigserial
        primary key,
    namespace_id varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name   varchar(64)                                                                not null,
    host_id      varchar(64)                                                                not null,
    host_ip      varchar(64)                                                                not null,
    host_port    integer                                                                    not null,
    expire_at    timestamp                                                                  not null,
    node_type    smallint                                                                   not null,
    ext_attrs    varchar(256) default ''::character varying,
    labels       varchar(512) default ''::character varying,
    create_dt    timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt    timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_server_node is '服务器节点';

comment on column sj_server_node.id is '主键';

comment on column sj_server_node.namespace_id is '命名空间id';

comment on column sj_server_node.group_name is '组名称';

comment on column sj_server_node.host_id is '主机id';

comment on column sj_server_node.host_ip is '机器ip';

comment on column sj_server_node.host_port is '机器端口';

comment on column sj_server_node.expire_at is '过期时间';

comment on column sj_server_node.node_type is '节点类型 1、客户端 2、是服务端';

comment on column sj_server_node.ext_attrs is '扩展字段';

comment on column sj_server_node.labels is '标签';

comment on column sj_server_node.create_dt is '创建时间';

comment on column sj_server_node.update_dt is '修改时间';

alter table sj_server_node
    owner to astra;

create unique index uk_sj_server_node_01
    on sj_server_node (host_id, host_ip);

create index idx_sj_server_node_01
    on sj_server_node (namespace_id, group_name);

create index idx_sj_server_node_02
    on sj_server_node (expire_at, node_type);

INSERT INTO public.sj_server_node (id, namespace_id, group_name, host_id, host_ip, host_port, expire_at, node_type, ext_attrs, labels, create_dt, update_dt) VALUES (50, 'DEFAULT_SERVER_NAMESPACE_ID', 'DEFAULT_SERVER', '2012736400858079232', '172.19.0.1', 17888, '2026-01-18 18:43:19.746120', 2, '{"webPort":8800,"systemVersion":"1.8.0"}', null, '2026-01-18 11:58:49.786101', '2026-01-18 18:42:49.747005');
INSERT INTO public.sj_server_node (id, namespace_id, group_name, host_id, host_ip, host_port, expire_at, node_type, ext_attrs, labels, create_dt, update_dt) VALUES (52, 'dev', 'ruoyi_group', '2012737147347726336', '192.168.3.15', 28080, '2026-01-18 18:43:24.909010', 1, '{"systemVersion":"1.8.0","executorType":"1"}', '{"state":"up"}', '2026-01-18 12:01:51.547134', '2026-01-18 18:42:54.909805');

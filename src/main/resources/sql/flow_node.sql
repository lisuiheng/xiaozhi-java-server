create table flow_node
(
    id              bigint       not null
        primary key,
    node_type       smallint     not null,
    definition_id   bigint       not null,
    node_code       varchar(100) not null,
    node_name       varchar(100),
    permission_flag varchar(200),
    node_ratio      numeric(6, 3),
    coordinate      varchar(100),
    any_node_skip   varchar(100),
    listener_type   varchar(100),
    listener_path   varchar(400),
    handler_type    varchar(100),
    handler_path    varchar(400),
    form_custom     char        default 'N'::character varying,
    form_path       varchar(100),
    version         varchar(20)  not null,
    create_time     timestamp,
    create_by       varchar(64) default ''::character varying,
    update_time     timestamp,
    update_by       varchar(64) default ''::character varying,
    ext             text,
    del_flag        char        default '0'::character varying,
    tenant_id       varchar(40)
);

comment on table flow_node is '流程节点表';

comment on column flow_node.id is '主键id';

comment on column flow_node.node_type is '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';

comment on column flow_node.definition_id is '流程定义id';

comment on column flow_node.node_code is '流程节点编码';

comment on column flow_node.node_name is '流程节点名称';

comment on column flow_node.permission_flag is '权限标识（权限类型:权限标识，可以多个，用@@隔开)';

comment on column flow_node.node_ratio is '流程签署比例值';

comment on column flow_node.coordinate is '坐标';

comment on column flow_node.any_node_skip is '任意结点跳转';

comment on column flow_node.listener_type is '监听器类型';

comment on column flow_node.listener_path is '监听器路径';

comment on column flow_node.handler_type is '处理器类型';

comment on column flow_node.handler_path is '处理器路径';

comment on column flow_node.form_custom is '审批表单是否自定义（Y是 N否）';

comment on column flow_node.form_path is '审批表单路径';

comment on column flow_node.version is '版本';

comment on column flow_node.create_time is '创建时间';

comment on column flow_node.create_by is '创建人';

comment on column flow_node.update_time is '更新时间';

comment on column flow_node.update_by is '更新人';

comment on column flow_node.ext is '节点扩展属性';

comment on column flow_node.del_flag is '删除标志';

comment on column flow_node.tenant_id is '租户id';

alter table flow_node
    owner to astra;


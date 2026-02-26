create table flow_skip
(
    id             bigint       not null
        primary key,
    definition_id  bigint       not null,
    now_node_code  varchar(100) not null,
    now_node_type  smallint,
    next_node_code varchar(100) not null,
    next_node_type smallint,
    skip_name      varchar(100),
    skip_type      varchar(40),
    skip_condition varchar(200),
    coordinate     varchar(100),
    create_time    timestamp,
    create_by      varchar(64) default ''::character varying,
    update_time    timestamp,
    update_by      varchar(64) default ''::character varying,
    del_flag       char        default '0'::character varying,
    tenant_id      varchar(40)
);

comment on table flow_skip is '节点跳转关联表';

comment on column flow_skip.id is '主键id';

comment on column flow_skip.definition_id is '流程定义id';

comment on column flow_skip.now_node_code is '当前流程节点的编码';

comment on column flow_skip.now_node_type is '当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';

comment on column flow_skip.next_node_code is '下一个流程节点的编码';

comment on column flow_skip.next_node_type is '下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';

comment on column flow_skip.skip_name is '跳转名称';

comment on column flow_skip.skip_type is '跳转类型（PASS审批通过 REJECT退回）';

comment on column flow_skip.skip_condition is '跳转条件';

comment on column flow_skip.coordinate is '坐标';

comment on column flow_skip.create_time is '创建时间';

comment on column flow_skip.create_by is '创建人';

comment on column flow_skip.update_time is '更新时间';

comment on column flow_skip.update_by is '更新人';

comment on column flow_skip.del_flag is '删除标志';

comment on column flow_skip.tenant_id is '租户id';

alter table flow_skip
    owner to astra;


create table flow_definition
(
    id              bigint                                            not null
        primary key,
    flow_code       varchar(40)                                       not null,
    flow_name       varchar(100)                                      not null,
    model_value     varchar(40) default 'CLASSICS'::character varying not null,
    category        varchar(100),
    version         varchar(20)                                       not null,
    is_publish      smallint    default 0                             not null,
    form_custom     char        default 'N'::character varying,
    form_path       varchar(100),
    activity_status smallint    default 1                             not null,
    listener_type   varchar(100),
    listener_path   varchar(400),
    ext             varchar(500),
    create_time     timestamp,
    create_by       varchar(64) default ''::character varying,
    update_time     timestamp,
    update_by       varchar(64) default ''::character varying,
    del_flag        char        default '0'::character varying,
    tenant_id       varchar(40)
);

comment on table flow_definition is '流程定义表';

comment on column flow_definition.id is '主键id';

comment on column flow_definition.flow_code is '流程编码';

comment on column flow_definition.flow_name is '流程名称';

comment on column flow_definition.model_value is '设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）';

comment on column flow_definition.category is '流程类别';

comment on column flow_definition.version is '流程版本';

comment on column flow_definition.is_publish is '是否发布（0未发布 1已发布 9失效）';

comment on column flow_definition.form_custom is '审批表单是否自定义（Y是 N否）';

comment on column flow_definition.form_path is '审批表单路径';

comment on column flow_definition.activity_status is '流程激活状态（0挂起 1激活）';

comment on column flow_definition.listener_type is '监听器类型';

comment on column flow_definition.listener_path is '监听器路径';

comment on column flow_definition.ext is '扩展字段，预留给业务系统使用';

comment on column flow_definition.create_time is '创建时间';

comment on column flow_definition.create_by is '创建人';

comment on column flow_definition.update_time is '更新时间';

comment on column flow_definition.update_by is '更新人';

comment on column flow_definition.del_flag is '删除标志';

comment on column flow_definition.tenant_id is '租户id';

alter table flow_definition
    owner to astra;


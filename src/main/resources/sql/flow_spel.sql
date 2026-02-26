create table flow_spel
(
    id             bigint not null
        primary key,
    component_name varchar(255),
    method_name    varchar(255),
    method_params  varchar(255),
    view_spel      varchar(255),
    remark         varchar(255),
    status         char default '0'::bpchar,
    del_flag       char default '0'::bpchar,
    create_dept    bigint,
    create_by      bigint,
    create_time    timestamp,
    update_by      bigint,
    update_time    timestamp
);

comment on table flow_spel is '流程spel表达式定义表';

comment on column flow_spel.id is '主键id';

comment on column flow_spel.component_name is '组件名称';

comment on column flow_spel.method_name is '方法名';

comment on column flow_spel.method_params is '参数';

comment on column flow_spel.view_spel is '预览spel表达式';

comment on column flow_spel.remark is '备注';

comment on column flow_spel.status is '状态（0正常 1停用）';

comment on column flow_spel.del_flag is '删除标志';

comment on column flow_spel.create_dept is '创建部门';

comment on column flow_spel.create_by is '创建者';

comment on column flow_spel.create_time is '创建时间';

comment on column flow_spel.update_by is '更新者';

comment on column flow_spel.update_time is '更新时间';

alter table flow_spel
    owner to astra;

INSERT INTO public.flow_spel (id, component_name, method_name, method_params, view_spel, remark, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (1, 'spelRuleComponent', 'selectDeptLeaderById', 'initiatorDeptId', '#{@spelRuleComponent.selectDeptLeaderById(#initiatorDeptId)}', '根据部门id获取部门负责人', '0', '0', 103, 1, '2025-11-25 12:12:28.906670', 1, '2025-11-25 12:12:28.906670');
INSERT INTO public.flow_spel (id, component_name, method_name, method_params, view_spel, remark, status, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (2, null, null, 'initiator', '${initiator}', '流程发起人', '0', '0', 103, 1, '2025-11-25 12:12:28.906670', 1, '2025-11-25 12:12:28.906670');

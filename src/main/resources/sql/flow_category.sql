create table flow_category
(
    category_id   bigint      not null
        primary key,
    tenant_id     varchar(20)  default '000000'::character varying,
    parent_id     bigint       default 0,
    ancestors     varchar(500) default ''::character varying,
    category_name varchar(30) not null,
    order_num     integer      default 0,
    del_flag      char         default '0'::bpchar,
    create_dept   bigint,
    create_by     bigint,
    create_time   timestamp,
    update_by     bigint,
    update_time   timestamp
);

comment on table flow_category is '流程分类';

comment on column flow_category.category_id is '流程分类ID';

comment on column flow_category.tenant_id is '租户编号';

comment on column flow_category.parent_id is '父流程分类id';

comment on column flow_category.ancestors is '祖级列表';

comment on column flow_category.category_name is '流程分类名称';

comment on column flow_category.order_num is '显示顺序';

comment on column flow_category.del_flag is '删除标志（0代表存在 1代表删除）';

comment on column flow_category.create_dept is '创建部门';

comment on column flow_category.create_by is '创建者';

comment on column flow_category.create_time is '创建时间';

comment on column flow_category.update_by is '更新者';

comment on column flow_category.update_time is '更新时间';

alter table flow_category
    owner to astra;

INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (100, '000000', 0, '0', 'OA审批', 0, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (101, '000000', 100, '0,100', '假勤管理', 0, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (102, '000000', 100, '0,100', '人事管理', 1, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (104, '000000', 101, '0,100,101', '出差', 1, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (105, '000000', 101, '0,100,101', '加班', 2, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (106, '000000', 101, '0,100,101', '换班', 3, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (107, '000000', 101, '0,100,101', '外出', 4, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (108, '000000', 102, '0,100,102', '转正', 1, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (109, '000000', 102, '0,100,102', '离职', 2, '0', 103, 1, '2025-11-25 12:12:28.629951', null, null);
INSERT INTO public.flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (103, '000000', 101, '0,100,101', '请假', 1, '0', 103, 1, '2025-11-25 12:12:28.629000', null, null);

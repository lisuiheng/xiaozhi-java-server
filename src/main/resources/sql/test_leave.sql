create table test_leave
(
    id          bigint       not null
        primary key,
    tenant_id   varchar(20) default '000000'::character varying,
    apply_code  varchar(50)  not null,
    leave_type  varchar(255) not null,
    start_date  timestamp    not null,
    end_date    timestamp    not null,
    leave_days  smallint     not null,
    remark      varchar(255),
    status      varchar(255),
    create_dept bigint,
    create_by   bigint,
    create_time timestamp,
    update_by   bigint,
    update_time timestamp
);

comment on table test_leave is '请假申请表';

comment on column test_leave.id is 'id';

comment on column test_leave.tenant_id is '租户编号';

comment on column test_leave.apply_code is '申请编号';

comment on column test_leave.leave_type is '请假类型';

comment on column test_leave.start_date is '开始时间';

comment on column test_leave.end_date is '结束时间';

comment on column test_leave.leave_days is '请假天数';

comment on column test_leave.remark is '请假原因';

comment on column test_leave.status is '状态';

comment on column test_leave.create_dept is '创建部门';

comment on column test_leave.create_by is '创建者';

comment on column test_leave.create_time is '创建时间';

comment on column test_leave.update_by is '更新者';

comment on column test_leave.update_time is '更新时间';

alter table test_leave
    owner to astra;


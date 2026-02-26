create table sj_job_summary
(
    id               bigserial
        primary key,
    namespace_id     varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name       varchar(64)  default ''::character varying                                 not null,
    business_id      bigint                                                                     not null,
    system_task_type smallint     default 3                                                     not null,
    trigger_at       timestamp    default CURRENT_TIMESTAMP                                     not null,
    success_num      integer      default 0                                                     not null,
    fail_num         integer      default 0                                                     not null,
    fail_reason      varchar(512) default ''::character varying                                 not null,
    stop_num         integer      default 0                                                     not null,
    stop_reason      varchar(512) default ''::character varying                                 not null,
    cancel_num       integer      default 0                                                     not null,
    cancel_reason    varchar(512) default ''::character varying                                 not null,
    create_dt        timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt        timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_job_summary is 'DashBoard_Job';

comment on column sj_job_summary.id is '主键';

comment on column sj_job_summary.namespace_id is '命名空间id';

comment on column sj_job_summary.group_name is '组名称';

comment on column sj_job_summary.business_id is '业务id  ( job_id或workflow_id)';

comment on column sj_job_summary.system_task_type is '任务类型 3、JOB任务 4、WORKFLOW任务';

comment on column sj_job_summary.trigger_at is '统计时间';

comment on column sj_job_summary.success_num is '执行成功-日志数量';

comment on column sj_job_summary.fail_num is '执行失败-日志数量';

comment on column sj_job_summary.fail_reason is '失败原因';

comment on column sj_job_summary.stop_num is '执行失败-日志数量';

comment on column sj_job_summary.stop_reason is '失败原因';

comment on column sj_job_summary.cancel_num is '执行失败-日志数量';

comment on column sj_job_summary.cancel_reason is '失败原因';

comment on column sj_job_summary.create_dt is '创建时间';

comment on column sj_job_summary.update_dt is '修改时间';

alter table sj_job_summary
    owner to astra;

create unique index uk_sj_job_summary_01
    on sj_job_summary (trigger_at, system_task_type, business_id);

create index idx_sj_job_summary_01
    on sj_job_summary (namespace_id, group_name, business_id);

INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (2, 'dev', 'ruoyi_group', 1, 3, '2025-12-29 00:00:00.000000', 355, 3, '[{"reason":0,"total":3}]', 0, '[]', 0, '[]', '2025-12-29 10:35:47.453989', '2026-01-04 19:24:49.932389');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (1, 'dev', 'ruoyi_group', 1, 3, '2025-11-26 00:00:00.000000', 486, 1, '[{"reason":0,"total":1}]', 0, '[]', 15, '[{"reason":2,"total":15}]', '2025-11-26 07:15:10.368172', '2025-11-26 15:36:11.183652');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (9, 'dev', 'ruoyi_group', 1, 3, '2026-01-07 00:00:00.000000', 32, 0, '[]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2026-01-07 10:26:09.682809', '2026-01-13 19:18:42.058266');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (11, 'dev', 'ruoyi_group', 1, 3, '2026-01-10 00:00:00.000000', 228, 0, '[]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2026-01-10 15:10:49.221426', '2026-01-16 18:43:38.045357');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (10, 'dev', 'ruoyi_group', 1, 3, '2026-01-08 00:00:00.000000', 60, 2, '[{"reason":0,"total":2}]', 0, '[]', 2, '[{"reason":2,"total":2}]', '2026-01-08 18:03:24.558193', '2026-01-14 19:10:35.483810');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (4, 'dev', 'ruoyi_group', 1, 3, '2025-12-31 00:00:00.000000', 544, 0, '[]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2025-12-31 09:56:12.664799', '2026-01-06 19:13:50.894870');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (3, 'dev', 'ruoyi_group', 1, 3, '2025-12-30 00:00:00.000000', 374, 1, '[{"reason":0,"total":1}]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2025-12-30 10:10:36.053998', '2026-01-05 18:48:41.752748');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (16, 'dev', 'ruoyi_group', 1, 3, '2026-01-18 00:00:00.000000', 401, 1, '[{"reason":0,"total":1}]', 0, '[]', 2, '[{"reason":2,"total":2}]', '2026-01-18 11:59:49.756341', '2026-01-18 18:42:49.755366');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (15, 'dev', 'ruoyi_group', 1, 3, '2026-01-16 00:00:00.000000', 414, 0, '[]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2026-01-16 11:49:38.031072', '2026-01-18 18:42:49.759462');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (7, 'dev', 'ruoyi_group', 1, 3, '2026-01-05 00:00:00.000000', 412, 1, '[{"reason":0,"total":1}]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2026-01-05 11:28:48.748810', '2026-01-10 18:58:49.235834');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (6, 'dev', 'ruoyi_group', 1, 3, '2026-01-04 00:00:00.000000', 49, 1, '[{"reason":0,"total":1}]', 1, '[{"reason":1,"total":1}]', 12, '[{"reason":2,"total":10},{"reason":4,"total":2}]', '2026-01-04 17:38:50.795218', '2026-01-10 18:58:49.239072');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (14, 'dev', 'ruoyi_group', 1, 3, '2026-01-14 00:00:00.000000', 447, 64, '[{"reason":0,"total":64}]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2026-01-14 10:39:35.473037', '2026-01-18 18:42:49.763827');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (13, 'dev', 'ruoyi_group', 1, 3, '2026-01-13 00:00:00.000000', 506, 0, '[]', 0, '[]', 2, '[{"reason":2,"total":2}]', '2026-01-13 10:51:42.040308', '2026-01-18 18:42:49.767386');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (12, 'dev', 'ruoyi_group', 1, 3, '2026-01-12 00:00:00.000000', 127, 0, '[]', 0, '[]', 2, '[{"reason":2,"total":2}]', '2026-01-12 17:10:31.903274', '2026-01-18 18:42:49.770326');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (5, 'dev', 'ruoyi_group', 1, 3, '2026-01-01 00:00:00.000000', 248, 1, '[{"reason":0,"total":1}]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2026-01-01 13:53:02.140518', '2026-01-07 10:57:09.698783');
INSERT INTO public.sj_job_summary (id, namespace_id, group_name, business_id, system_task_type, trigger_at, success_num, fail_num, fail_reason, stop_num, stop_reason, cancel_num, cancel_reason, create_dt, update_dt) VALUES (8, 'dev', 'ruoyi_group', 1, 3, '2026-01-06 00:00:00.000000', 101, 0, '[]', 0, '[]', 1, '[{"reason":2,"total":1}]', '2026-01-06 17:32:50.871537', '2026-01-12 19:17:31.921810');

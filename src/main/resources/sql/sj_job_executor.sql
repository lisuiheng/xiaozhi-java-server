create table sj_job_executor
(
    id            bigserial
        primary key,
    namespace_id  varchar(64) default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    group_name    varchar(64)                                                               not null,
    executor_info varchar(256)                                                              not null,
    executor_type varchar(3)                                                                not null,
    create_dt     timestamp   default CURRENT_TIMESTAMP                                     not null,
    update_dt     timestamp   default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_job_executor is '任务执行器信息';

comment on column sj_job_executor.id is '主键';

comment on column sj_job_executor.namespace_id is '命名空间id';

comment on column sj_job_executor.group_name is '组名称';

comment on column sj_job_executor.executor_info is '任务执行器名称';

comment on column sj_job_executor.executor_type is '1:java 2:python 3:go';

comment on column sj_job_executor.create_dt is '创建时间';

comment on column sj_job_executor.update_dt is '修改时间';

alter table sj_job_executor
    owner to astra;

create index idx_sj_job_executor_01
    on sj_job_executor (namespace_id, group_name);

create index idx_sj_job_executor_02
    on sj_job_executor (create_dt);

INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (1, 'dev', 'ruoyi_group', 'alipayBillTask', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');
INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (2, 'dev', 'ruoyi_group', 'summaryBillTask', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');
INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (3, 'dev', 'ruoyi_group', 'testJobExecutor', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');
INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (4, 'dev', 'ruoyi_group', 'testBroadcastJob', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');
INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (5, 'dev', 'ruoyi_group', 'org.dromara.job.snailjob.TestClassJobExecutor', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');
INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (6, 'dev', 'ruoyi_group', 'testMapJobAnnotation', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');
INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (7, 'dev', 'ruoyi_group', 'testMapReduceAnnotation1', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');
INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (8, 'dev', 'ruoyi_group', 'testStaticShardingJob', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');
INSERT INTO public.sj_job_executor (id, namespace_id, group_name, executor_info, executor_type, create_dt, update_dt) VALUES (9, 'dev', 'ruoyi_group', 'wechatBillTask', '1', '2025-11-26 07:29:31.350093', '2025-11-26 07:29:29.282863');

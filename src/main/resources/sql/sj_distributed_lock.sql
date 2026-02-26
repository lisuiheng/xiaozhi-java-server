create table sj_distributed_lock
(
    name       varchar(64)                               not null
        primary key,
    lock_until timestamp(3) default CURRENT_TIMESTAMP(3) not null,
    locked_at  timestamp(3) default CURRENT_TIMESTAMP(3) not null,
    locked_by  varchar(255)                              not null,
    create_dt  timestamp    default CURRENT_TIMESTAMP    not null,
    update_dt  timestamp    default CURRENT_TIMESTAMP    not null
);

comment on table sj_distributed_lock is '锁定表';

comment on column sj_distributed_lock.name is '锁名称';

comment on column sj_distributed_lock.lock_until is '锁定时长';

comment on column sj_distributed_lock.locked_at is '锁定时间';

comment on column sj_distributed_lock.locked_by is '锁定者';

comment on column sj_distributed_lock.create_dt is '创建时间';

comment on column sj_distributed_lock.update_dt is '修改时间';

alter table sj_distributed_lock
    owner to astra;

INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('retryLogMerge', '2026-01-18 17:59:49.753', '2026-01-18 17:58:49.753', '2012736400858079232', '2026-01-04 17:48:29.901849', '2026-01-18 17:58:49.760287');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('clearLog', '2026-01-18 15:59:49.757', '2026-01-18 15:58:49.757', '2012736400858079232', '2026-01-04 17:48:29.878877', '2026-01-18 15:58:49.761465');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('jobClearLog', '2026-01-18 15:59:49.750', '2026-01-18 15:58:49.750', '2012736400858079232', '2026-01-04 17:48:29.581127', '2026-01-18 15:58:49.755120');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('jobLogMerge', '2026-01-18 18:43:49.750', '2026-01-18 18:42:49.750', '2012736400858079232', '2026-01-04 17:48:29.581127', '2026-01-18 18:42:49.794944');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('clearOfflineNode', '2026-01-18 18:43:04.272', '2026-01-18 18:42:59.272', '2012736400858079232', '2026-01-04 17:48:29.581145', '2026-01-18 18:42:59.276590');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('workflowJobSummarySchedule', '2026-01-18 18:43:09.751', '2026-01-18 18:42:49.751', '2012736400858079232', '2026-01-04 17:48:29.873374', '2026-01-18 18:42:49.757647');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('retryTaskMoreThreshold', '2026-01-18 18:39:50.684', '2026-01-18 18:38:50.684', '2012736400858079232', '2026-01-04 17:48:29.916885', '2026-01-18 18:38:50.689833');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('retrySummaryDashboard', '2026-01-18 18:43:09.753', '2026-01-18 18:42:49.753', '2012736400858079232', '2026-01-04 17:48:29.914005', '2026-01-18 18:42:49.762122');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('retryErrorMoreThreshold', '2026-01-18 18:39:50.683', '2026-01-18 18:38:50.683', '2012736400858079232', '2026-01-04 17:48:29.899018', '2026-01-18 18:38:50.688854');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('jobSummaryDashboard', '2026-01-18 18:43:09.750', '2026-01-18 18:42:49.750', '2012736400858079232', '2026-01-04 17:48:29.581318', '2026-01-18 18:42:49.772341');
INSERT INTO public.sj_distributed_lock (name, lock_until, locked_at, locked_by, create_dt, update_dt) VALUES ('registerNode', '2026-01-18 18:43:04.913', '2026-01-18 18:42:59.913', '2012736400858079232', '2026-01-04 17:48:29.931297', '2026-01-18 18:42:59.916153');

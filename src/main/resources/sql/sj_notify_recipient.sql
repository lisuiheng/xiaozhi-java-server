create table sj_notify_recipient
(
    id               bigserial
        primary key,
    namespace_id     varchar(64)  default '764d604ec6fc45f68cd92514c40e9e1a'::character varying not null,
    recipient_name   varchar(64)                                                                not null,
    notify_type      smallint     default 0                                                     not null,
    notify_attribute varchar(512)                                                               not null,
    description      varchar(256) default ''::character varying                                 not null,
    create_dt        timestamp    default CURRENT_TIMESTAMP                                     not null,
    update_dt        timestamp    default CURRENT_TIMESTAMP                                     not null
);

comment on table sj_notify_recipient is '告警通知接收人';

comment on column sj_notify_recipient.id is '主键';

comment on column sj_notify_recipient.namespace_id is '命名空间id';

comment on column sj_notify_recipient.recipient_name is '接收人名称';

comment on column sj_notify_recipient.notify_type is '通知类型 1、钉钉 2、邮件 3、企业微信 4 飞书 5 webhook';

comment on column sj_notify_recipient.notify_attribute is '配置属性';

comment on column sj_notify_recipient.description is '描述';

comment on column sj_notify_recipient.create_dt is '创建时间';

comment on column sj_notify_recipient.update_dt is '修改时间';

alter table sj_notify_recipient
    owner to astra;

create index idx_sj_notify_recipient_01
    on sj_notify_recipient (namespace_id);


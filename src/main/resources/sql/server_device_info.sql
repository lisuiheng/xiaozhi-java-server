create table server_device_info
(
    id                  text not null
        primary key,
    name                varchar(255),
    serial_number       varchar(255),
    verify_code         varchar(255),
    device_kind         varchar(255),
    device_state        varchar(50),
    program_kind        varchar(255),
    program_ver         varchar(255),
    volume              integer,
    brightness          integer,
    is_updatable        varchar(10),
    version_type        text,
    program_update_time timestamp,
    detail_info         jsonb,
    ota_update_url      varchar(512),
    agent_id            text,
    aes_key             varchar(255),
    aes_nonce           varchar(255),
    remark              varchar(512),
    create_dept         bigint,
    create_by           bigint,
    create_time         timestamp,
    update_by           bigint,
    update_time         timestamp,
    uuid                varchar(50),
    activated_time      timestamp,
    last_seen_time      timestamp,
    secret_key          varchar(256),
    hmac_key_index      integer default 0,
    production_batch    varchar(100),
    production_date     timestamp
);

comment on table server_device_info is '服务端设备信息表';

comment on column server_device_info.id is '设备ID';

comment on column server_device_info.name is '设备名称';

comment on column server_device_info.serial_number is '设备串号，如MAC';

comment on column server_device_info.verify_code is '设备验证码';

comment on column server_device_info.device_kind is '设备类型，如：esp32-S1';

comment on column server_device_info.device_state is '设备状态';

comment on column server_device_info.program_kind is '程序名称';

comment on column server_device_info.program_ver is '程序版本';

comment on column server_device_info.volume is '音量';

comment on column server_device_info.brightness is '亮度';

comment on column server_device_info.is_updatable is '版本是否可更新';

comment on column server_device_info.version_type is '版本类型';

comment on column server_device_info.program_update_time is '程序更新时间';

comment on column server_device_info.detail_info is '设备拓展信息,json格式的mapList';

comment on column server_device_info.ota_update_url is 'OTA更新的URL';

comment on column server_device_info.agent_id is '智能体ID';

comment on column server_device_info.aes_key is 'AES密钥';

comment on column server_device_info.aes_nonce is 'AES随机数';

comment on column server_device_info.remark is '备注';

comment on column server_device_info.create_dept is '创建部门';

comment on column server_device_info.create_by is '创建者';

comment on column server_device_info.create_time is '创建时间';

comment on column server_device_info.update_by is '更新者';

comment on column server_device_info.update_time is '更新时间';

comment on column server_device_info.uuid is '设备UUID';

comment on column server_device_info.activated_time is '激活时间';

comment on column server_device_info.last_seen_time is '最后连接时间';

comment on column server_device_info.secret_key is '设备密钥（Base64编码）';

comment on column server_device_info.hmac_key_index is 'HMAC密钥索引（0-7）';

comment on column server_device_info.production_batch is '生产批次';

comment on column server_device_info.production_date is '生产日期';

alter table server_device_info
    owner to astra;

create index idx_device_uuid
    on server_device_info (uuid);

create index idx_device_activated
    on server_device_info (activated_time);

create index idx_device_last_seen
    on server_device_info (last_seen_time);

create index idx_device_production_batch
    on server_device_info (production_batch);

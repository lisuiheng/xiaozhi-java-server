create table server_chat_detail
(
    id                   varchar(255) not null
        primary key,
    call_id              varchar(255),
    chat_time            timestamp,
    question_kind        varchar(50),
    question_name        varchar(255),
    content              text,
    voice_remark         text,
    user_id              varchar(255),
    agent_id             varchar(255),
    device_id            varchar(255),
    chat_kind            varchar(50),
    chat_id              varchar(255),
    is_interrupted       boolean,
    conversation_content text,
    longitude            numeric(9, 6),
    latitude             numeric(9, 6),
    create_dept          bigint,
    create_by            bigint,
    create_time          timestamp default CURRENT_TIMESTAMP,
    update_by            bigint,
    update_time          timestamp default CURRENT_TIMESTAMP
);

comment on table server_chat_detail is '聊天详情表';

comment on column server_chat_detail.id is '主键ID';

comment on column server_chat_detail.call_id is '会话ID';

comment on column server_chat_detail.chat_time is '对话时间';

comment on column server_chat_detail.question_kind is '说话者类型(使用者/智能体)';

comment on column server_chat_detail.question_name is '说话者名称(姓名(昵称)/智能体名称(角色))';

comment on column server_chat_detail.content is '对话内容';

comment on column server_chat_detail.voice_remark is '声纹数据';

comment on column server_chat_detail.user_id is '用户ID';

comment on column server_chat_detail.agent_id is '智能体ID';

comment on column server_chat_detail.device_id is '设备ID';

comment on column server_chat_detail.chat_kind is '对话类型 chat/pic/file/video';

comment on column server_chat_detail.chat_id is '对话文件ID';

comment on column server_chat_detail.is_interrupted is '是否被打断';

comment on column server_chat_detail.conversation_content is '实际对话内容';

comment on column server_chat_detail.longitude is '经度，精度为小数点后6位';

comment on column server_chat_detail.latitude is '纬度，精度为小数点后6位';

comment on column server_chat_detail.create_dept is '创建部门';

comment on column server_chat_detail.create_by is '创建者';

comment on column server_chat_detail.create_time is '创建时间';

comment on column server_chat_detail.update_by is '更新者';

comment on column server_chat_detail.update_time is '更新时间';

alter table server_chat_detail
    owner to astra;

create index idx_server_chat_detail_call_id
    on server_chat_detail (call_id);

create index idx_server_chat_detail_user_id
    on server_chat_detail (user_id);

create index idx_server_chat_detail_agent_id
    on server_chat_detail (agent_id);

create index idx_server_chat_detail_chat_time
    on server_chat_detail (chat_time);

create index idx_server_chat_detail_device_id
    on server_chat_detail (device_id);


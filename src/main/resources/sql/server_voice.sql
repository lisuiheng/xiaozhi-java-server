create table server_voice
(
    id           text not null
        primary key,
    name         varchar(255),
    speaker      varchar(255),
    tts          varchar(50),
    oss_id       bigint,
    prompt_text  text,
    voice_remark text,
    description  text,
    create_dept  bigint,
    create_by    bigint,
    create_time  timestamp(3) default CURRENT_TIMESTAMP(3),
    update_by    bigint,
    update_time  timestamp(3) default CURRENT_TIMESTAMP(3)
);

comment on table server_voice is '语音音色配置表';

comment on column server_voice.id is '主键，唯一标识符';

comment on column server_voice.name is '声音名称';

comment on column server_voice.speaker is '音色';

comment on column server_voice.tts is 'TTS 类型（对应 Tts 枚举）';

comment on column server_voice.oss_id is '样例录音文件ID（OSS存储ID）';

comment on column server_voice.prompt_text is '样例录音文本';

comment on column server_voice.voice_remark is '声纹数据';

comment on column server_voice.description is '声音描述';

comment on column server_voice.create_dept is '创建部门';

comment on column server_voice.create_by is '创建者';

comment on column server_voice.create_time is '创建时间';

comment on column server_voice.update_by is '更新者';

comment on column server_voice.update_time is '更新时间';

alter table server_voice
    owner to astra;


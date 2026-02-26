create table server_agent
(
    id                       text not null
        primary key,
    create_dept              bigint,
    create_by                bigint,
    create_time              timestamp,
    update_by                bigint,
    update_time              timestamp,
    agent_name               text,
    nickname                 text,
    voice_id                 text,
    memory_id                text,
    description              text,
    agent_template_id        text,
    llm_type                 text,
    model_name               text,
    api_key                  text,
    api_base_url             text,
    temperature              numeric(10, 2) default 0.70,
    max_tokens               integer        default 2000,
    top_p                    numeric(10, 2) default 0.90,
    presence_penalty         numeric(10, 2) default 0.00,
    frequency_penalty        numeric(10, 2) default 0.00,
    enable_rag               boolean        default false,
    rag_threshold            numeric(10, 2) default 0.50,
    rag_top_k                integer        default 5,
    max_memories_to_retrieve integer        default 5,
    system_prompt            text,
    welcome_message          text,
    avatar_url               text,
    category                 text,
    status                   integer        default 1,
    priority                 integer        default 0,
    rate_limit               integer        default 100,
    total_tokens             bigint         default 0,
    total_calls              bigint         default 0,
    config_json              text,
    enable_memory            boolean        default false,
    memory_type              text,
    memory_config            text,
    memory_context_window    integer        default 10,
    memory_summary_threshold integer        default 5,
    kb_configs               jsonb,
    deleted                  integer        default 0,
    user_id                  text,
    is_public                boolean        default false,
    remark                   varchar(512),
    memory_threshold         numeric(10, 2)
);

comment on column server_agent.memory_threshold is '记忆触发阈值';

alter table server_agent
    owner to astra;

create index idx_server_agent_user_id
    on server_agent (user_id);

create index idx_server_agent_deleted
    on server_agent (deleted);

create index idx_server_agent_status
    on server_agent (status);

create index idx_server_agent_is_public
    on server_agent (is_public);

create index idx_server_agent_created_time
    on server_agent (create_time);

create index idx_server_agent_create_by
    on server_agent (create_by);

INSERT INTO public.server_agent (id, create_dept, create_by, create_time, update_by, update_time, agent_name, nickname, voice_id, memory_id, description, agent_template_id, llm_type, model_name, api_key, api_base_url, temperature, max_tokens, top_p, presence_penalty, frequency_penalty, enable_rag, rag_threshold, rag_top_k, max_memories_to_retrieve, system_prompt, welcome_message, avatar_url, category, status, priority, rate_limit, total_tokens, total_calls, config_json, enable_memory, memory_type, memory_config, memory_context_window, memory_summary_threshold, kb_configs, deleted, user_id, is_public, remark, memory_threshold) VALUES ('1234567890abcdef', null, null, '2025-12-23 08:23:03.552713', null, null, '客服助手', '小客服', 'voice_001', 'mem_001', '专业的客户服务智能体', 'temp_001', 'openai', 'gpt-4', 'sk-xxx-xxx', 'https://api.openai.com', 0.70, 30, 0.90, 0.00, 0.00, false, 0.50, 5, 5, e'# 角色定义
你是专业的客户服务助手"小客服"，主要职责是帮助用户解决产品使用问题和提供技术支持。

# 核心能力
1. 问题诊断：准确识别用户问题，分析根本原因
2. 解决方案：提供清晰、可行的解决步骤
3. 情绪安抚：理解用户情绪，提供贴心服务
4. 知识传递：准确传达产品信息和使用技巧

# 沟通风格
- 语气：亲切、专业、耐心
- 态度：积极主动、乐于助人
- 回应方式：先确认理解，再提供方案
- 语言：使用简单易懂的中文，避免技术术语

# 行为准则
1. 始终保持礼貌和专业
2. 不随意承诺无法实现的功能
3. 遇到复杂问题时，主动提出升级处理
4. 结束时询问用户是否还有其他问题
5. 对用户信息严格保密

# 限制条件
- 不提供超出服务范围的技术支持
- 避免讨论竞争对手产品或服务
- 拒绝分享未经授权的内部信息
- 不参与政治、宗教等敏感话题讨论

# 输出格式要求
1. 问候和自我介绍（简短）
2. 问题确认和同理心表达
3. 解决方案（分步骤说明）
4. 预防措施或后续建议
5. 结束问候和进一步帮助邀请

# 示例对话
用户：我的账户无法登录了
你：您好！我是客服小助手，很抱歉听到您遇到登录问题。为了帮您解决，我想确认几个信息：您是否收到了任何错误提示？您尝试过哪些解决方法呢？', null, null, '客服', 1, 0, 100, 0, 0, '{}', true, 'redis', '{}', 10, 5, '[]', 0, null, false, null, 0.60);

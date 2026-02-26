create table server_knowledge_document
(
    id               varchar(50)  not null
        primary key,
    kb_id            varchar(50)  not null
        constraint fk_kb_id
            references server_knowledge_base
            on delete cascade,
    doc_name         varchar(255) not null,
    file_name        varchar(255),
    file_size        bigint,
    file_type        varchar(100),
    content          text,
    content_summary  text,
    status           integer default 1,
    processed_at     timestamp,
    vector_id        varchar(255),
    embedding_status integer default 0,
    create_dept      bigint,
    create_by        bigint,
    create_time      timestamp,
    update_by        bigint,
    update_time      timestamp
);

alter table server_knowledge_document
    owner to astra;

create index idx_doc_kb_id
    on server_knowledge_document (kb_id);

create index idx_doc_status
    on server_knowledge_document (status);

create index idx_doc_embedding_status
    on server_knowledge_document (embedding_status);

INSERT INTO public.server_knowledge_document (id, kb_id, doc_name, file_name, file_size, file_type, content, content_summary, status, processed_at, vector_id, embedding_status, create_dept, create_by, create_time, update_by, update_time) VALUES ('c4414898-370c-40da-b724-f66d0057af32', '118c3879-757d-47e6-b123-df6f2c880205', '搜索测试文档', null, null, 'text', 'Spring AI 是一个强大的AI应用开发框架，支持多种AI模型和向量数据库。', 'Spring AI 是一个强大的AI应用开发框架，支持多种AI模型和向量数据库。', 1, '2025-12-03 16:41:22.587000', 'd5b511da-fd60-487d-befc-2e3a733f6932', 1, null, null, null, null, null);
INSERT INTO public.server_knowledge_document (id, kb_id, doc_name, file_name, file_size, file_type, content, content_summary, status, processed_at, vector_id, embedding_status, create_dept, create_by, create_time, update_by, update_time) VALUES ('3196af2c-774f-4487-9344-b8f735ead41e', '118c3879-757d-47e6-b123-df6f2c880205', '机器人几条腿', null, null, 'text', '机器人有3条腿', '机器人有3条腿', 1, '2026-01-06 19:00:15.158000', '420c7870-eb09-4f79-bed4-e277d98c53e9', 1, null, null, null, null, null);

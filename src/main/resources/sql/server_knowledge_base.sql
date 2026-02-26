create table server_knowledge_base
(
    id                varchar(50)  not null
        primary key,
    kb_name           varchar(255) not null,
    description       text,
    vector_store_type varchar(50) default 'milvus'::character varying,
    embedding_model   varchar(255),
    doc_count         integer     default 0,
    status            integer     default 1,
    is_public         boolean     default false,
    create_dept       bigint,
    create_by         bigint,
    create_time       timestamp,
    update_by         bigint,
    update_time       timestamp
);

alter table server_knowledge_base
    owner to astra;

create index idx_kb_status
    on server_knowledge_base (status);

INSERT INTO public.server_knowledge_base (id, kb_name, description, vector_store_type, embedding_model, doc_count, status, is_public, create_dept, create_by, create_time, update_by, update_time) VALUES ('118c3879-757d-47e6-b123-df6f2c880205', '搜索测试知识库', null, 'qdrant', 'text-embedding-v4', 2, 1, false, null, null, null, null, null);

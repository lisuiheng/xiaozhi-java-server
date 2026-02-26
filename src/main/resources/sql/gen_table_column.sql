create table gen_table_column
(
    column_id      bigint not null
        constraint gen_table_column_pk
            primary key,
    table_id       bigint,
    column_name    varchar(200) default NULL::character varying,
    column_comment varchar(500) default NULL::character varying,
    column_type    varchar(100) default NULL::character varying,
    java_type      varchar(500) default NULL::character varying,
    java_field     varchar(200) default NULL::character varying,
    is_pk          char         default NULL::bpchar,
    is_increment   char         default NULL::bpchar,
    is_required    char         default NULL::bpchar,
    is_insert      char         default NULL::bpchar,
    is_edit        char         default NULL::bpchar,
    is_list        char         default NULL::bpchar,
    is_query       char         default NULL::bpchar,
    query_type     varchar(200) default 'EQ'::character varying,
    html_type      varchar(200) default NULL::character varying,
    dict_type      varchar(200) default ''::character varying,
    sort           integer,
    create_dept    bigint,
    create_by      bigint,
    create_time    timestamp,
    update_by      bigint,
    update_time    timestamp
);

comment on table gen_table_column is '代码生成业务表字段';

comment on column gen_table_column.column_id is '编号';

comment on column gen_table_column.table_id is '归属表编号';

comment on column gen_table_column.column_name is '列名称';

comment on column gen_table_column.column_comment is '列描述';

comment on column gen_table_column.column_type is '列类型';

comment on column gen_table_column.java_type is 'JAVA类型';

comment on column gen_table_column.java_field is 'JAVA字段名';

comment on column gen_table_column.is_pk is '是否主键（1是）';

comment on column gen_table_column.is_increment is '是否自增（1是）';

comment on column gen_table_column.is_required is '是否必填（1是）';

comment on column gen_table_column.is_insert is '是否为插入字段（1是）';

comment on column gen_table_column.is_edit is '是否编辑字段（1是）';

comment on column gen_table_column.is_list is '是否列表字段（1是）';

comment on column gen_table_column.is_query is '是否查询字段（1是）';

comment on column gen_table_column.query_type is '查询方式（等于、不等于、大于、小于、范围）';

comment on column gen_table_column.html_type is '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）';

comment on column gen_table_column.dict_type is '字典类型';

comment on column gen_table_column.sort is '排序';

comment on column gen_table_column.create_dept is '创建部门';

comment on column gen_table_column.create_by is '创建者';

comment on column gen_table_column.create_time is '创建时间';

comment on column gen_table_column.update_by is '更新者';

comment on column gen_table_column.update_time is '更新时间';

alter table gen_table_column
    owner to astra;


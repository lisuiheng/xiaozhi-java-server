create table gen_table
(
    table_id          bigint                            not null
        constraint gen_table_pk
            primary key,
    data_name         varchar(200)  default ''::character varying,
    table_name        varchar(200)  default ''::character varying,
    table_comment     varchar(500)  default ''::character varying,
    sub_table_name    varchar(64)   default ''::character varying,
    sub_table_fk_name varchar(64)   default ''::character varying,
    class_name        varchar(100)  default ''::character varying,
    tpl_category      varchar(200)  default 'crud'::character varying,
    package_name      varchar(100)  default NULL::character varying,
    module_name       varchar(30)   default NULL::character varying,
    business_name     varchar(30)   default NULL::character varying,
    function_name     varchar(50)   default NULL::character varying,
    function_author   varchar(50)   default NULL::character varying,
    gen_type          char          default '0'::bpchar not null,
    gen_path          varchar(200)  default '/'::character varying,
    options           varchar(1000) default NULL::character varying,
    create_dept       bigint,
    create_by         bigint,
    create_time       timestamp,
    update_by         bigint,
    update_time       timestamp,
    remark            varchar(500)  default NULL::character varying
);

comment on table gen_table is '代码生成业务表';

comment on column gen_table.table_id is '编号';

comment on column gen_table.data_name is '数据源名称';

comment on column gen_table.table_name is '表名称';

comment on column gen_table.table_comment is '表描述';

comment on column gen_table.sub_table_name is '关联子表的表名';

comment on column gen_table.sub_table_fk_name is '子表关联的外键名';

comment on column gen_table.class_name is '实体类名称';

comment on column gen_table.tpl_category is '使用的模板（CRUD单表操作 TREE树表操作）';

comment on column gen_table.package_name is '生成包路径';

comment on column gen_table.module_name is '生成模块名';

comment on column gen_table.business_name is '生成业务名';

comment on column gen_table.function_name is '生成功能名';

comment on column gen_table.function_author is '生成功能作者';

comment on column gen_table.gen_type is '生成代码方式（0zip压缩包 1自定义路径）';

comment on column gen_table.gen_path is '生成路径（不填默认项目路径）';

comment on column gen_table.options is '其它生成选项';

comment on column gen_table.create_dept is '创建部门';

comment on column gen_table.create_by is '创建者';

comment on column gen_table.create_time is '创建时间';

comment on column gen_table.update_by is '更新者';

comment on column gen_table.update_time is '更新时间';

comment on column gen_table.remark is '备注';

alter table gen_table
    owner to astra;


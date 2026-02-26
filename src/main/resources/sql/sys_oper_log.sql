create table sys_oper_log
(
    oper_id        bigint not null
        constraint sys_oper_log_pk
            primary key,
    tenant_id      varchar(20)   default '000000'::character varying,
    title          varchar(50)   default ''::character varying,
    business_type  integer       default 0,
    method         varchar(100)  default ''::character varying,
    request_method varchar(10)   default ''::character varying,
    operator_type  integer       default 0,
    oper_name      varchar(50)   default ''::character varying,
    dept_name      varchar(50)   default ''::character varying,
    oper_url       varchar(255)  default ''::character varying,
    oper_ip        varchar(128)  default ''::character varying,
    oper_location  varchar(255)  default ''::character varying,
    oper_param     varchar(4000) default ''::character varying,
    json_result    varchar(4000) default ''::character varying,
    status         integer       default 0,
    error_msg      varchar(4000) default ''::character varying,
    oper_time      timestamp,
    cost_time      bigint        default 0
);

comment on table sys_oper_log is '操作日志记录';

comment on column sys_oper_log.oper_id is '日志主键';

comment on column sys_oper_log.tenant_id is '租户编号';

comment on column sys_oper_log.title is '模块标题';

comment on column sys_oper_log.business_type is '业务类型（0其它 1新增 2修改 3删除）';

comment on column sys_oper_log.method is '方法名称';

comment on column sys_oper_log.request_method is '请求方式';

comment on column sys_oper_log.operator_type is '操作类别（0其它 1后台用户 2手机端用户）';

comment on column sys_oper_log.oper_name is '操作人员';

comment on column sys_oper_log.dept_name is '部门名称';

comment on column sys_oper_log.oper_url is '请求URL';

comment on column sys_oper_log.oper_ip is '主机地址';

comment on column sys_oper_log.oper_location is '操作地点';

comment on column sys_oper_log.oper_param is '请求参数';

comment on column sys_oper_log.json_result is '返回参数';

comment on column sys_oper_log.status is '操作状态（0正常 1异常）';

comment on column sys_oper_log.error_msg is '错误消息';

comment on column sys_oper_log.oper_time is '操作时间';

comment on column sys_oper_log.cost_time is '消耗时间';

alter table sys_oper_log
    owner to astra;

create index idx_sys_oper_log_bt
    on sys_oper_log (business_type);

create index idx_sys_oper_log_s
    on sys_oper_log (status);

create index idx_sys_oper_log_ot
    on sys_oper_log (oper_time);

INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2004845776516337666, '000000', '菜单管理', 2, 'org.dromara.system.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":103,"createBy":null,"createTime":"2025-11-25 12:11:22","updateBy":null,"updateTime":null,"menuId":5,"parentId":0,"menuName":"会话管理","orderNum":5,"path":"chat","component":null,"queryParam":"","isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","icon":"wechat","remark":"测试菜单"}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-27 17:24:16.009000', 32);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2004847628796481537, '000000', '菜单管理', 1, 'org.dromara.system.controller.system.SysMenuController.add()', 'POST', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"menuId":null,"parentId":5,"menuName":"实时会话","orderNum":1,"path":"ChatRealtime","component":"chat/realtime/index","queryParam":null,"isFrame":"1","isCache":"0","menuType":"C","visible":"0","status":"0","perms":"chat:realtime:list","icon":"","remark":null}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-27 17:31:37.628000', 18);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2004851063683993601, '000000', '菜单管理', 1, 'org.dromara.system.controller.system.SysMenuController.add()', 'POST', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"menuId":null,"parentId":0,"menuName":"智能体管理","orderNum":1,"path":"agent","component":null,"queryParam":null,"isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","icon":"language","remark":null}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-27 17:45:16.569000', 15);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2004851096944824321, '000000', '菜单管理', 2, 'org.dromara.system.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":103,"createBy":null,"createTime":"2025-12-27 17:45:16","updateBy":null,"updateTime":null,"menuId":"2004851063637856258","parentId":0,"menuName":"智能体管理","orderNum":7,"path":"agent","component":null,"queryParam":null,"isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","icon":"language","remark":""}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-27 17:45:24.499000', 18);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2004851161973313537, '000000', '菜单管理', 2, 'org.dromara.system.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":103,"createBy":null,"createTime":"2025-11-25 12:11:22","updateBy":null,"updateTime":null,"menuId":5,"parentId":0,"menuName":"会话管理","orderNum":10,"path":"chat","component":null,"queryParam":"","isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","icon":"wechat","remark":"测试菜单"}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-27 17:45:40.003000', 13);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2004851187680202754, '000000', '菜单管理', 2, 'org.dromara.system.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":103,"createBy":null,"createTime":"2025-12-27 17:45:16","updateBy":null,"updateTime":null,"menuId":"2004851063637856258","parentId":0,"menuName":"智能体管理","orderNum":11,"path":"agent","component":null,"queryParam":null,"isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","icon":"language","remark":""}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-27 17:45:46.131000', 20);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2004852107369435138, '000000', '菜单管理', 2, 'org.dromara.system.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":103,"createBy":null,"createTime":"2025-12-27 17:45:16","updateBy":null,"updateTime":null,"menuId":"2004851063637856258","parentId":0,"menuName":"AI助手管理","orderNum":11,"path":"ai","component":null,"queryParam":null,"isFrame":"1","isCache":"0","menuType":"M","visible":"0","status":"0","icon":"language","remark":""}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-27 17:49:25.403000', 16);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2004852401763438594, '000000', '菜单管理', 1, 'org.dromara.system.controller.system.SysMenuController.add()', 'POST', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"menuId":null,"parentId":"2004851063637856258","menuName":"智能体管理","orderNum":1,"path":"Agent","component":"ai/agent/index","queryParam":null,"isFrame":"1","isCache":"0","menuType":"C","visible":"0","status":"0","perms":"ai:agent:list","icon":"","remark":null}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-27 17:50:35.592000', 12);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2005844917744590849, '000000', '菜单管理', 1, 'org.dromara.system.controller.system.SysMenuController.add()', 'POST', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"menuId":null,"parentId":"2004851063637856258","menuName":"设备管理","orderNum":1,"path":"Device","component":"ai/device/index","queryParam":null,"isFrame":"1","isCache":"0","menuType":"C","visible":"0","status":"0","perms":"ai:device:list","icon":"","remark":null}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-30 11:34:29.841000', 30);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2005852989250625537, '000000', '菜单管理', 1, 'org.dromara.system.controller.system.SysMenuController.add()', 'POST', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"menuId":null,"parentId":"2004851063637856258","menuName":"实时会话","orderNum":3,"path":"ChatDetail","component":"ai/chatDetail/index","queryParam":null,"isFrame":"1","isCache":"0","menuType":"C","visible":"0","status":"0","perms":"ai:chatDetail:list","icon":"","remark":null}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-30 12:06:34.240000', 15);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2006202253885509633, '000000', '菜单管理', 1, 'org.dromara.system.controller.system.SysMenuController.add()', 'POST', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"menuId":null,"parentId":"2004851063637856258","menuName":"长期记忆管理","orderNum":1,"path":"Memory","component":"ai/memory/index","queryParam":null,"isFrame":"1","isCache":"0","menuType":"C","visible":"0","status":"0","perms":"ai:memory:list","icon":"","remark":null}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2025-12-31 11:14:25.417000', 29);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2008475347288645633, '000000', '菜单管理', 1, 'org.dromara.system.controller.system.SysMenuController.add()', 'POST', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":null,"createBy":null,"createTime":null,"updateBy":null,"updateTime":null,"menuId":null,"parentId":0,"menuName":"知识库","orderNum":1,"path":"Knowledge","component":"ai/knowledge/index","queryParam":null,"isFrame":"1","isCache":"0","menuType":"C","visible":"0","status":"0","perms":"ai:knowledge:list","icon":"","remark":null}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2026-01-06 17:46:53.116000', 35);
INSERT INTO public.sys_oper_log (oper_id, tenant_id, title, business_type, method, request_method, operator_type, oper_name, dept_name, oper_url, oper_ip, oper_location, oper_param, json_result, status, error_msg, oper_time, cost_time) VALUES (2008475808318152705, '000000', '菜单管理', 2, 'org.dromara.system.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{"createDept":103,"createBy":null,"createTime":"2026-01-06 17:46:53","updateBy":null,"updateTime":null,"menuId":"2008475347158622209","parentId":"2004851063637856258","menuName":"知识库","orderNum":1,"path":"Knowledge","component":"ai/knowledge/index","queryParam":null,"isFrame":"1","isCache":"0","menuType":"C","visible":"0","status":"0","perms":"ai:knowledge:list","icon":"","remark":""}', '{"code":200,"msg":"操作成功","data":null}', 0, '', '2026-01-06 17:48:43.036000', 22);

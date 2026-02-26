create table sys_logininfor
(
    info_id        bigint not null
        constraint sys_logininfor_pk
            primary key,
    tenant_id      varchar(20)  default '000000'::character varying,
    user_name      varchar(50)  default ''::character varying,
    client_key     varchar(32)  default ''::character varying,
    device_type    varchar(32)  default ''::character varying,
    ipaddr         varchar(128) default ''::character varying,
    login_location varchar(255) default ''::character varying,
    browser        varchar(50)  default ''::character varying,
    os             varchar(50)  default ''::character varying,
    status         char         default '0'::bpchar,
    msg            varchar(255) default ''::character varying,
    login_time     timestamp
);

comment on table sys_logininfor is '系统访问记录';

comment on column sys_logininfor.info_id is '访问ID';

comment on column sys_logininfor.tenant_id is '租户编号';

comment on column sys_logininfor.user_name is '用户账号';

comment on column sys_logininfor.client_key is '客户端';

comment on column sys_logininfor.device_type is '设备类型';

comment on column sys_logininfor.ipaddr is '登录IP地址';

comment on column sys_logininfor.login_location is '登录地点';

comment on column sys_logininfor.browser is '浏览器类型';

comment on column sys_logininfor.os is '操作系统';

comment on column sys_logininfor.status is '登录状态（0成功 1失败）';

comment on column sys_logininfor.msg is '提示消息';

comment on column sys_logininfor.login_time is '访问时间';

alter table sys_logininfor
    owner to astra;

create index idx_sys_logininfor_s
    on sys_logininfor (status);

create index idx_sys_logininfor_lt
    on sys_logininfor (login_time);

INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (1993466736255541249, '000000', 'admin', 'pc', 'pc', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome', 'Windows 10 or Windows Server 2016', '1', '密码输入错误1次', '2025-11-26 07:48:01.391000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (1993467928306098178, '000000', 'admin', 'pc', 'pc', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome', 'Windows 10 or Windows Server 2016', '1', '密码输入错误2次', '2025-11-26 07:52:45.618000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (1993468281026093058, '000000', 'admin', 'pc', 'pc', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome', 'Windows 10 or Windows Server 2016', '1', '验证码错误', '2025-11-26 07:54:09.704000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (1993468308448452610, '000000', 'admin', 'pc', 'pc', '0:0:0:0:0:0:0:1', '内网IP', 'Chrome', 'Windows 10 or Windows Server 2016', '0', '登录成功', '2025-11-26 07:54:16.248000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2004843484673134593, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-27 17:15:09.588000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005468303722897410, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-29 10:37:58.055000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005482121781927938, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '1', '验证码已失效', '2025-12-29 11:32:52.545000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005482149825044482, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-29 11:32:59.232000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005560806069329921, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-29 16:45:32.342000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005594947028606977, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-29 19:01:12.180000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005599929622683649, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-29 19:21:00.122000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005823596532883458, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-30 10:09:46.462000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005844395620851714, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-30 11:32:25.359000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005895509783371778, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-30 14:55:31.925000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005915739653955585, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-30 16:15:55.103000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2005946710033981441, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-30 18:18:59.016000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006182931255463938, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-31 09:57:38.536000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006200592903376898, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-31 11:07:49.409000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006252066849099778, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-31 14:32:21.754000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006293495092809729, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-31 17:16:59.018000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006315707493453826, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2025-12-31 18:45:14.866000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006564036989259778, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-01 11:12:01.224000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006574063565246465, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '退出成功', '2026-01-01 11:51:51.754000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006574087514722305, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-01 11:51:57.463000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006603268281700354, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-01 13:47:54.700000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2006659316233375746, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-01 17:30:37.574000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2007359832537939969, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-03 15:54:13.669000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2007750517856264194, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-04 17:46:40.306000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2008471977555451906, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-06 17:33:29.702000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2008488203891564545, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-06 18:37:58.371000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2008727198209486850, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-07 10:27:39.048000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2009885969048821761, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-10 15:12:11.545000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2009900794378346497, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-10 16:11:06.187000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2009901703636344834, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-10 16:14:42.971000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2010640917540544514, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-12 17:12:05.292000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2010913525443543042, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-13 11:15:20.081000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2010983097899106305, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-13 15:51:47.456000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2010998866812813314, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-13 16:54:27.058000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2011267047030317058, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-14 10:40:06.196000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2011374020736376834, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-14 17:45:10.722000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012739811410243586, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-18 12:12:20.587000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012742734177431553, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-18 12:23:57.436000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012773964520419330, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-18 14:28:03.330000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012774650062630914, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-18 14:30:46.776000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012782965987536898, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-18 15:03:49.447000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012783266287120386, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '退出成功', '2026-01-18 15:05:01.042000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012783333001719810, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-18 15:05:16.950000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012783988697264129, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-18 15:07:53.280000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2012809094853095425, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-18 16:47:39.054000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013067399234961409, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '退出成功', '2026-01-19 09:54:03.614000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013067414846160897, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-19 09:54:07.340000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013067533154893826, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '退出成功', '2026-01-19 09:54:35.547000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013071906035556353, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-19 10:11:58.119000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013072568228106242, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-19 10:14:35.998000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013072597206552578, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '退出成功', '2026-01-19 10:14:42.910000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013072622141689858, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-19 10:14:48.856000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013100862990266369, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '退出成功', '2026-01-19 12:07:01.995000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2013100890135801857, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-01-19 12:07:08.471000');
INSERT INTO public.sys_logininfor (info_id, tenant_id, user_name, client_key, device_type, ipaddr, login_location, browser, os, status, msg, login_time) VALUES (2018158199304200194, '000000', 'admin', 'pc', 'pc', '127.0.0.1', '内网IP', 'Chrome', 'Linux', '0', '登录成功', '2026-02-02 11:03:04.933000');

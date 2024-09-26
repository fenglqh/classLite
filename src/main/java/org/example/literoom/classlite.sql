create database if not exists classlite;
use classlite;
--     创建用户表
drop table if exists user;
create table user (
                      user_id int primary key auto_increment,
                      user_name varchar(20) unique,
                      password varchar(20)
);

insert into user values (1,"zhangsan",123),(2,"lisi",123),(3,"wangwu",123),(4,"zhaoliu",123),(5,"gufa",123);
-- 创建好友表
drop table if exists friend;
create table friend(
                       user_id int,
                       friend_id int
);
insert into friend values(1,2),(1,3),(1,4),(1,5),(2,1),(3,1),(4,1),(5,1),(2,3),(3,2);

-- 创建会话表
drop table if exists message_session;
create table message_session (session_id int primary key auto_increment, last_time datetime );
insert into message_session values (1,"2024-9-1 20:23:45"),(2,"2024-9-1 21:00:00");

-- 创建会话用户关联表
drop table if exists message_session_user;
create table message_session_user(session_id int, user_id int);
insert into message_session_user values (1,1),(1,2),(2,2),(2,3);
-- 会话1 张三和李四  会话2 李四和王五

-- 创建消息表
drop table  if exists message;
create table message (
    message_id int primary key auto_increment,
    from_id int, -- 发送消息的用户id
    session_id int,
    content varchar(2048),
    post_time datetime

);
--     会话1 张三和李四聊天
insert into message values (1,1,1,"你在干嘛","2024-9-2 20:30:12");
insert into message values (2,2,1,"我在看月亮","2024-9-2 21:00:20");
insert into message values (3,1,1,"今天是阴天","2024-9-2 21:10:03");
insert into message values (4,2,1,"你是我的月亮","2024-9-2 21:11:03");
insert into message values (5,1,1,"你这是在和我告白吗","2024-9-2 21:12:03");
insert into message values (6,2,1,"我觉得我说的很明显了","2024-9-2 21:13:03");
insert into message values (7,1,1,"确实","2024-9-2 21:14:03");
insert into message values (8,2,1,"那你同意了吗","2024-9-2 21:15:03");
insert into message values (9,1,1,"你好，男朋友","2024-9-2 21:16:03");
insert into message values (11,2,1,"亲亲抱抱举高高，mua! (*╯3╰)，（づ￣3￣）づ╭❤～，
老婆，(* ￣3)(ε￣ *)，哇卡卡卡哇卡卡卡哇卡卡卡，喜欢你，hhhhh，亲亲ლ(°◕‵ƹ′◕ლ)老婆么么哒，我是老婆的小狗狗，
亲亲抱抱举高高，mua! (*╯3╰)，（づ￣3￣）づ╭❤～，
老婆，(* ￣3)(ε￣ *)，哇卡卡卡哇卡卡卡哇卡卡卡，喜欢你，hhhhh，亲亲ლ(°◕‵ƹ′◕ლ)老婆么么哒，我是老婆的小狗狗","2024-9-2 21:17:03");
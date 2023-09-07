insert into member
(email,is_deleted,member_name,password,role)
values
    ('admin@naver.com',0,'admin123','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER'),
    ('test1@naver.com',0,'test1','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER'),
    ('test2@naver.com',0,'test2','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER'),
    ('test3@naver.com',0,'test4','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER'),
    ('test4@naver.com',0,'test4','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER'),
    ('test5@naver.com',0,'test5','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER'),
    ('test6@naver.com',0,'test6','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER'),
    ('test7@naver.com',0,'test7','$2a$10$jWpTgBFPm4f77Jklchp7iu/oe0uaB8VeaeBiEueRp3/xOlFLvkPqC', 'USER');

insert into member_game_info
(is_observer, exp, member_game_info_id, member_id, in_game_role, level, nickname, profile_img)
values
    (0,'0','1','1','NONE','BEGINNER','admin','PROFILE01'),
    (0,'0','2','2','NONE','BEGINNER','test1','PROFILE01'),
    (0,'0','3','3','NONE','BEGINNER','test3','PROFILE01'),
    (0,'0','4','4','NONE','BEGINNER','test4','PROFILE01'),
    (0,'0','5','5','NONE','BEGINNER','test5','PROFILE01'),
    (0,'0','6','6','NONE','BEGINNER','test6','PROFILE01'),
    (0,'0','7','7','NONE','BEGINNER','test7','PROFILE01');


insert into chat_room
(close, capacity, chatroom_id, head, owner_id, password, state, title)
values
    (0, '7', '1', '0', '1', '', 'WAITING', 'mafia');

insert into game_record
(title, owner_id, state, capacity, head, open, winner, duration)
values
    ('testTitle1', 1, '진행중', 10, '9', true, true, 60),
    ('testTitle1', 1, '진행중', 10, '9', true, true, 60),
    ('testTitle1', 1, '진행중', 10, '9', true, true, 60),
    ('testTitle1', 1, '진행중', 10, '9', true, true, 60);

insert into springles.member_record(
    mafia_cnt, citizen_cnt, doctor_cnt, police_cnt,
    save_cnt, kill_cnt,
    mafia_win_cnt, citizen_win_cnt, police_win_cnt, doctor_win_cnt,
    total_cnt, total_time, member_id
)
values
    (5, 10, 20, 10, 5, 5, 2, 5, 4, 10, 100, 1000, 1),
    (5, 10, 20, 10, 5, 5, 2, 5, 4, 10, 100, 1000, 2),
    (5, 10, 20, 10, 5, 5, 2, 5, 4, 10, 100, 1000, 3),
    (5, 10, 20, 10, 5, 5, 2, 5, 4, 10, 100, 1000, 4);


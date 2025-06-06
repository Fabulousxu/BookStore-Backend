DROP DATABASE IF EXISTS bookstore;
CREATE DATABASE bookstore;
USE bookstore;

# 用户信息表
CREATE TABLE user
(
    user_id  BIGINT PRIMARY KEY AUTO_INCREMENT, # 用户id
    username VARCHAR(16) NOT NULL,              # 用户名
    nickname VARCHAR(16) NOT NULL,              # 昵称
    email    VARCHAR(32) NOT NULL,              # 邮箱
    balance  BIGINT  DEFAULT 0,                 # 余额
    admin    BOOLEAN DEFAULT FALSE,             # 是否为管理员
    silence  BOOLEAN DEFAULT FALSE              # 是否被禁用
);

# 账户密码表
CREATE TABLE user_auth
(
    user_id  BIGINT PRIMARY KEY,   # 用户id
    password VARCHAR(16) NOT NULL, # 密码
    FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# 书籍信息表
CREATE TABLE book
(
    book_id     BIGINT PRIMARY KEY AUTO_INCREMENT, # 书籍id
    title       TEXT        NOT NULL,              # 书名
    author      TEXT        NOT NULL,              # 作者
    isbn        VARCHAR(17) NOT NULL,              # ISBN
    description TEXT        NOT NULL,              # 描述
    price       INT         NOT NULL,              # 价格
    sales       INT DEFAULT 0,                     # 销量
    repertory   INT DEFAULT 0                      # 库存
);

# 订单表
CREATE TABLE `order`
(
    order_id   BIGINT PRIMARY KEY AUTO_INCREMENT,   # 订单id
    user_id    BIGINT      NOT NULL,                # 用户id
    receiver   VARCHAR(32) NOT NULL,                # 收件人
    address    TEXT        NOT NULL,                # 地址
    tel        VARCHAR(16) NOT NULL,                # 电话
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, # 创建时间
    FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# 订单项目表
CREATE TABLE order_item
(
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT, # 订单项目id
    order_id      BIGINT NOT NULL,                   # 订单id
    book_id       BIGINT NOT NULL,                   # 书籍id
    number        INT DEFAULT 1,                     # 数量
    FOREIGN KEY (order_id) REFERENCES `order` (order_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book (book_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# 购物车表
CREATE TABLE cart_item
(
    cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT, # 购物车项目id
    user_id      BIGINT NOT NULL,                   # 用户id
    book_id      BIGINT NOT NULL,                   # 书籍id
    number       INT DEFAULT 1,                     # 数量
    FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book (book_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# 评论表
CREATE TABLE comment
(
    comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,   # 评论id
    user_id    BIGINT NOT NULL,                     # 用户id
    book_id    BIGINT NOT NULL,                     # 书籍id
    content    TEXT   NOT NULL,                     # 内容
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, # 创建时间
    `like`     INT       DEFAULT 0,                 # 点赞数
    FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book (book_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# 评论点赞表
CREATE TABLE comment_like
(
    comment_id BIGINT NOT NULL, # 评论id
    user_id    BIGINT NOT NULL, # 用户id
    PRIMARY KEY (comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES comment (comment_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

# 书籍分类表
CREATE TABLE book_category
(
    book_id       BIGINT     NOT NULL, # 书籍id
    category_code VARCHAR(8) NOT NULL, # 分类代码
    PRIMARY KEY (book_id, category_code),
    FOREIGN KEY (book_id) REFERENCES book (book_id) ON DELETE CASCADE ON UPDATE CASCADE
);

####################################################################################################
# 以下为测试数据

# 创建测试用户信息
INSERT INTO user (username, nickname, email, admin)
VALUES ('u1', 'user1', 'u1@bookstore.com', true),
       ('u2', 'user2', 'u2@bookstore.com', false),
       ('u3', 'user3', 'u3@bookstore.com', false),
       ('u4', 'user4', 'u4@bookstore.com', false),
       ('u5', 'user5', 'u5@bookstore.com', false),
       ('u6', 'user6', 'u6@bookstore.com', false),
       ('u7', 'user7', 'u7@bookstore.com', false),
       ('u8', 'user8', 'u8@bookstore.com', false),
       ('u9', 'user9', 'u9@bookstore.com', false),
       ('u10', 'user10', 'u10@bookstore.com', false),
       ('u11', 'user11', 'u11@bookstore.com', false),
       ('u12', 'user12', 'u12@bookstore.com', false),
       ('u13', 'user13', 'u13@bookstore.com', false),
       ('u14', 'user14', 'u14@bookstore.com', false),
       ('u15', 'user15', 'u15@bookstore.com', false),
       ('u16', 'user16', 'u16@bookstore.com', false),
       ('u17', 'user17', 'u17@bookstore.com', false),
       ('u18', 'user18', 'u18@bookstore.com', false),
       ('u19', 'user19', 'u19@bookstore.com', false),
       ('u20', 'user20', 'u20@bookstore.com', false);


# 创建测试用户账号密码信息
INSERT INTO user_auth (user_id, password)
VALUES (1, '123'),
       (2, '123'),
       (3, '123'),
       (4, '123'),
       (5, '123'),
       (6, '123'),
       (7, '123'),
       (8, '123'),
       (9, '123'),
       (10, '123'),
       (11, '123'),
       (12, '123'),
       (13, '123'),
       (14, '123'),
       (15, '123'),
       (16, '123'),
       (17, '123'),
       (18, '123'),
       (19, '123'),
       (20, '123');

# 创建测试书籍信息
INSERT INTO book (title, author, description, isbn, price, sales, repertory)
VALUES ('1984', '[英] 乔治·奥威尔',
        '《1984》是一部杰出的政治寓言小说，也是一部幻想小说。作品刻画了人类在极权主义社会的生存状态，有若一个永不褪色的警示标签，警醒世人提防这种预想中的黑暗成为现实。历经几十年，其生命力益显强大，被誉为20世纪影响最为深远的文学经典之一。',
        '0000000000001', 2800, 0, 100),
       ('C++ Primer 中文版（第 5 版）', '[美] Stanley B. Lippman / [美] Josée Lajoie / [美]',
        '这本久负盛名的 C++ 经典教程，时隔八年之久，终迎来史无前例的重大升级。除令全球无数程序员从中受益，甚至为之迷醉的——C++ 大师 Stanley B. Lippman 的丰富实践经验，C++标准委员会原负责人 Josée Lajoie 对C++标准的深入理解，以及C+ + 先驱 Barbara E. Moo 在 C++教学方面的真知灼见外，更是基于全新的 C++11标准进行了全面而彻底的内容更新。非常难能可贵的是，本书所有示例均全部采用 C++11 标准改写，这在经典升级版中极其罕见——充分体现了 C++ 语言的重大进展及其全面实践。书中丰富的教学辅助内容、醒目的知识点提示，以及精心组织的编程示范，让这本书在 C++ 领域的权威地位更加不可动摇。无论是初学者入门，或是中、高级程序员提升，本书均为不容置疑的首选。',
        '0000000000002', 12800, 0, 100),
       ('Java核心技术·卷I（原书第12版）', '[美] 凯·S.霍斯特曼（Cay S.Horstmann）',
        '伴随着Java的成长，《Java核心技术》从第1版到第11版一路走来，得到了广大Java程序设计人员的青睐，成为一本畅销不衰的Java经典图书。',
        '0000000000003', 14900, 0, 100),
       ('Java语言程序设计（基础篇 原书第10版）', '[美]粱勇（Y.Daniel Liang）',
        '《Java语言程序设计（基础篇 原书第10版）》是Java语言的经典教材，中文版分为基础篇和进阶篇，主要介绍程序设计基础、面向对象编程、GUI程序设计、数据结构和算法、高级Java程序设计等内容。本书以示例讲解解决问题的技巧，提供大量的程序清单，每章配有大量复习题和编程练习题，帮助读者掌握编程技术，并应用所学技术解决实际应用开发中遇到的问题。您手中的这本是其中的基础篇，主要介绍了基本程序设计、语法结构、面向对象程序设计、继承和多态、异常处理和文本I/O、抽象类和接口等内容。本书可作为高等院校程序设计相关专业的基础教材，也可作为Java语言及编程开发爱好者的参考资料。',
        '0000000000004', 8500, 0, 100),
       ('Go专家编程', '任洪彩',
        '《Go专家编程》深入地讲解了Go语言常见特性的内部机制和实现方式，大部分内容源自对Go语言源码的分析，并从中提炼出实现原理。通过阅读本书，读者可以快速、轻松地了解Go语言的内部运作机制。',
        '0000000000005', 10800, 0, 100),
       ('Go语言精进之路', '白明',
        'Go入门容易，精进难，如何才能像Go开发团队那样写出符合Go思维和语言惯例的高质量代码呢？本书将从编程思维和实践技巧2个维度给出答案，帮助你在Go进阶的路上事半功倍。',
        '0000000000006', 9900, 0, 100),
       ('吉伊卡哇 這又小又可愛的傢伙 2', 'nagano',
        '又小又可愛的傢伙=通稱「吉伊卡哇」。 吉伊卡哇的好朋友小八貓， 在吉伊卡哇家發現了《拔草撿定5級》的應考書籍。 「考取資格酬勞增加的話，就能買禮物送給大家了！」 看著不擅長念書的吉伊卡哇努力用功的模樣， 小八貓想到了…… 又小又可愛，無時無刻都很努力。 當然，不可能每件事都一帆風順， 吉伊卡哇的每一天，也伴隨著辛苦、悲傷和危險。 但是，和*喜歡的朋友一起，明天也會帶著笑容「這樣過生活」。 本書同樣收錄了在這裡才看得到的特別繪製故事哦！',
        '0000000000007', 6600, 0, 100),
       ('吉伊卡哇 這又小又可愛的傢伙 1', 'nagano',
        '《MOGUMOGU邊走邊吃熊》作者nagano繪製的人氣系列終於登場！ 由又小又可愛的傢伙=通稱「吉伊卡哇」們所編織出的， 快樂的、苦悶的、也有點艱辛的日日物語。 好想過著受大家喜愛、被溫柔對待的生活…… 身邊卻有許多來路不明的人事物！？ 不過，和*喜歡的小八貓和兔兔一起，努力過生活的「吉伊卡哇」身邊， 不論何時都洋溢歡笑。 本書當然也收錄了在這裡才看得到的故事！ 又一個「nagano WORLD」，在此拉開序幕！',
        '0000000000008', 6600, 0, 100),
       ('吉伊卡哇 這又小又可愛的傢伙 3', 'nagano',
        '又小又可爱的家伙，就是“吉伊卡哇”！ 吉伊卡哇、小八猫、兔兔，友好三人组发现了神秘开关。 按下开关后，门敞开了，另一头出现了比自己“更小更可爱的家伙”！ 有了想守护的同伴，辛苦的讨伐和准备考拔草检定，也比往常更有干劲！ 还以为从今以后能永远在一起… 努力过生活的吉伊卡哇的身边，每天都会发生许多事， 无论发生任何事，只要和喜欢的朋友一起，一定就能所向无敌。 本书同样收录了只有在这里才看得到的特别绘制故事哦！',
        '0000000000009', 6600, 0, 100),
       ('吉伊卡哇 這又小又可愛的傢伙 4', 'nagano',
        '又小又可爱的傢伙＝通称“吉伊卡哇”。 不由得就会随口哼唱出“?唔、唔、哇、哇、唔哇”的歌词， 出自相当受吉伊卡哇们喜爱的团体“睡衣派队”！ “睡衣派队”要在“超好狂欢节”表演，成员却被大鸟抓走了…！ 吉伊卡哇们深信成员们一定会回来，想好好守护著舞台…… 既然如此！他们决定代替“睡衣派队”上台！ 无论是快乐的时候，还是遇到危机的时候。 吉伊卡哇和zui喜欢的朋友们，努力过好每一天。 本书也收录了在这裡才看得到的特别绘制故事！',
        '0000000000010', 6600, 0, 100),
       ('吉伊卡哇 這又小又可愛的傢伙 5', 'nagano',
        '电视动画也大受好评！人气系列“Chikawa”，期待已久的第5卷登场！！ 千鹤他们在森林里遇见的大个子“奥德”。在奥德的推荐下一起吃蘑菇的时候，被地精们抓住了！ 和奥德一起被关在监狱里的千鹤们。这样下去的话会被地精们吃掉的！但是……大家都能不放弃就逃出来吧 除了“奥德”篇，还收录了强烈反响的“老虎机”篇和“黑色流星”篇！还有只用单行本才能读懂的画！ 虽然有痛苦和悲伤，但明天也想和蕞喜欢的大家一起笑着生活。找到小小的幸福，小小的日子还在继续！',
        '0000000000011', 6600, 0, 100),
       ('Java编程思想（第4版）', '[美] Bruce Eckel',
        'Java学习经典,殿堂级著作！赢得了全球程序员的广泛赞誉。',
        '0000000000012', 9900, 0, 100),
       ('测试书籍1', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000013', 10001, 0, 100),
       ('测试书籍2', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000014', 10002, 0, 100),
       ('测试书籍3', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000015', 10003, 0, 100),
       ('测试书籍4', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000016', 10004, 0, 100),
       ('测试书籍5', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000017', 10005, 0, 100),
       ('测试书籍6', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000018', 10006, 0, 100),
       ('测试书籍7', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000019', 10007, 0, 100),
       ('测试书籍8', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000020', 10008, 0, 100),
       ('测试书籍9', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000021', 10009, 0, 100),
       ('测试书籍10', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000022', 10010, 0, 100),
       ('测试书籍11', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000023', 10011, 0, 100),
       ('测试书籍12', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000024', 10012, 0, 100),
       ('测试书籍13', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000025', 10013, 0, 100),
       ('测试书籍14', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000026', 10014, 0, 100),
       ('测试书籍15', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000027', 10015, 0, 100),
       ('测试书籍16', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000028', 10016, 0, 100),
       ('测试书籍17', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000029', 10017, 0, 100),
       ('测试书籍18', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000030', 10018, 0, 100),
       ('测试书籍19', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000031', 10019, 0, 100),
       ('测试书籍20', '徐培公', '该书为测试书籍，仅供测试使用。',
        '0000000000032', 10020, 0, 100);

# 创建测试购物车信息
INSERT INTO cart_item (user_id, book_id, number)
VALUES (1, 1, 1),
       (1, 2, 2),
       (1, 3, 3),
       (1, 4, 4),
       (1, 5, 5),
       (1, 6, 6),
       (1, 7, 7),
       (1, 8, 8),
       (1, 9, 9),
       (1, 10, 10),
       (1, 11, 11),
       (1, 12, 12),
       (1, 13, 13),
       (1, 14, 14),
       (1, 15, 15),
       (1, 16, 16),
       (1, 17, 17),
       (1, 18, 18),
       (1, 19, 19),
       (1, 20, 20),
       (1, 21, 21),
       (1, 22, 22),
       (1, 23, 23),
       (1, 24, 24),
       (1, 25, 25),
       (1, 26, 26),
       (1, 27, 27),
       (1, 28, 28),
       (1, 29, 29),
       (1, 30, 30),
       (2, 1, 1),
       (2, 2, 2),
       (2, 3, 3),
       (2, 4, 4),
       (2, 5, 5),
       (2, 6, 6),
       (2, 7, 7),
       (2, 8, 8),
       (2, 9, 9),
       (2, 10, 10),
       (2, 11, 11),
       (2, 12, 12),
       (2, 13, 13),
       (2, 14, 14),
       (2, 15, 15),
       (2, 16, 16),
       (2, 17, 17),
       (2, 18, 18),
       (2, 19, 19),
       (2, 20, 20),
       (2, 21, 21),
       (2, 22, 22),
       (2, 23, 23),
       (2, 24, 24),
       (2, 25, 25),
       (2, 26, 26),
       (2, 27, 27),
       (2, 28, 28),
       (2, 29, 29),
       (2, 30, 30);

# 创建测试书籍分类信息
INSERT INTO book_category (book_id, category_code)
VALUES (1, 'A'),
       (2, 'A1'),
       (3, 'A11'),
       (4, 'A12'),
       (5, 'A13'),
       (6, 'A2'),
       (7, 'A21'),
       (8, 'A22'),
       (9, 'A23'),
       (10, 'A3'),
       (11, 'A31'),
       (12, 'A32'),
       (13, 'A33'),
       (14, 'B'),
       (15, 'C'),
       (16, 'D'),
       (17, 'A'),
       (18, 'A1'),
       (19, 'A11'),
       (20, 'A12'),
       (21, 'A13'),
       (22, 'A2'),
       (23, 'A21'),
       (24, 'A22'),
       (25, 'A23'),
       (26, 'A3'),
       (27, 'A31'),
       (28, 'A32'),
       (29, 'A33'),
       (30, 'B'),
       (31, 'C'),
       (32, 'D');

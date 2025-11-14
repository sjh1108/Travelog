use ssafybook;

-- 1. User (회원) 데이터
INSERT INTO `User` (`user_id`, `email`, `password`, `nickname`, `borrow_limit`, `status`, `created_at`)
VALUES
(1, 'user1@example.com', 'hashed_password_1', '책읽는사피', 5, 'ACTIVE', NOW()),
(2, 'user2@example.com', 'hashed_password_2', '알고리즘왕', 5, 'ACTIVE', NOW()),
(3, 'user3@example.com', 'hashed_password_3', '도서관지기', 10, 'ACTIVE', NOW());

-- 2. Category (카테고리) 데이터
INSERT INTO `Category` (`category_id`, `name`)
VALUES
(1, '소설'),
(2, 'IT/컴퓨터'),
(3, '자기계발'),
(4, '인문'),
(5, '경제/경영');

-- 3. Author (저자) 데이터
INSERT INTO `Author` (`author_id`, `name`, `bio`)
VALUES
(1, '조앤 K. 롤링', '영국의 소설가, \'해리 포터\' 시리즈의 저자'),
(2, '유발 하라리', '이스라엘의 역사학자, \'사피엔스\'의 저자'),
(3, '로버트 C. 마틴', '미국의 소프트웨어 엔지니어, \'클린 코드\'의 저자'),
(4, '김영하', '대한민국의 소설가, \'여행의 이유\'의 저자');

-- 4. Publisher (출판사) 데이터
INSERT INTO `Publisher` (`publisher_id`, `name`)
VALUES
(1, '문학동네'),
(2, '김영사'),
(3, '인사이트'),
(4, '복복서가'),
(5, '한빛미디어');

-- 5. Book (도서) 데이터
INSERT INTO `Book` (`book_id`, `category_id2`, `publisher_id2`, `title`, `description`, `isbn`, `cover_image_url`, `file_path`, `page_count`, `published_date`, `created_at`)
VALUES
(1, 1, 1, '해리 포터와 마법사의 돌', '해리 포터 시리즈의 첫 번째 책', '9788983920625', '/covers/book1.jpg', '/ebooks/book1.epub', 350, '2019-11-19', NOW()),
(2, 4, 2, '사피엔스', '유인원에서 사이보그까지, 인간 역사의 대담하고 거침없는 탐험', '9788934972464', '/covers/book2.jpg', '/ebooks/book2.epub', 600, '2015-11-24', NOW()),
(3, 2, 3, '클린 코드 (Clean Code)', '애자일 소프트웨어 장인 정신', '9788966260959', '/covers/book3.jpg', '/ebooks/book3.epub', 450, '2013-12-24', NOW()),
(4, 4, 4, '여행의 이유', '김영하 작가의 여행 에세이', '9788997782983', '/covers/book4.jpg', '/ebooks/book4.epub', 216, '2019-04-17', NOW()),
(5, 2, 5, '이펙티브 자바 (Effective Java)', '자바 개발자의 필독서', '9788968480333', '/covers/book5.jpg', '/ebooks/book5.epub', 512, '2018-11-01', NOW());

-- 6. Book_Author (도서-저자 N:M) 데이터
INSERT INTO `Book_Author` (`book_id2`, `author_id`)
VALUES
(1, 1), -- 해리 포터 -> 조앤 K. 롤링
(2, 2), -- 사피엔스 -> 유발 하라리
(3, 3), -- 클린 코드 -> 로버트 C. 마틴
(4, 4), -- 여행의 이유 -> 김영하
(5, 3); -- 이펙티브 자바 -> 로버트 C. 마틴 (예시: 저자가 같음)

-- 7. Rental (대출 기록) 데이터
INSERT INTO `Rental` (`rental_id`, `user_id2`, `book_id2`, `rental_date`, `due_date`, `status`, `last_updated_at`)
VALUES
-- '책읽는사피'(1)가 '해리 포터'(1)를 대출 중
(1, 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'RENTED', NULL),
-- '책읽는사피'(1)가 '사피엔스'(2)를 빌렸다가 반납함
(2, 1, 2, '2025-10-01 10:00:00', '2025-10-15 10:00:00', 'RETURNED', '2025-10-14 11:30:00'),
-- '알고리즘왕'(2)이 '클린 코드'(3)를 대출 중
(3, 2, 3, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), 'RENTED', NULL),
-- '알고리즘왕'(2)이 '이펙티브 자바'(5)를 대출 중
(4, 2, 5, NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'RENTED', NULL);

-- 8. ReadingLog (독서록) 데이터
INSERT INTO `ReadingLog` (`log_id`, `user_id2`, `book_id2`, `content`, `rating`, `created_at`)
VALUES
-- '책읽는사피'(1)가 반납한 '사피엔스'(2)에 대한 독서록
(1, 1, 2, '정말 감명 깊게 읽었습니다. 인류의 역사에 대해 다시 생각해보게 되네요.', 5, '2025-10-15 12:00:00'),
-- '알고리즘왕'(2)이 읽고 있는 '클린 코드'(3)에 대한 독서록
(2, 2, 3, '내 코드가 얼마나 더러웠는지 깨닫게 해준 책. 챕터 3 읽는 중.', 4, NOW());
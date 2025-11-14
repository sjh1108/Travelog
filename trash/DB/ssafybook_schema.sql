-- 1. 데이터베이스 생성 및 선택
DROP DATABASE IF EXISTS ssafybook;
CREATE DATABASE ssafybook;
USE ssafybook;

-- 2. 테이블 생성 (CREATE TABLE)
CREATE TABLE `User` (
	`user_id`	INT	NOT NULL,
	`email`	VARCHAR(100)	NOT NULL,
	`password`	VARCHAR(255)	NOT NULL,
	`nickname`	VARCHAR(50)	NOT NULL,
	`borrow_limit`	INT	NOT NULL,
	`status`	VARCHAR(20)	NOT NULL	DEFAULT 'ACTIVE',
	`created_at`	TIMESTAMP	NOT NULL
);

CREATE TABLE `Category` (
	`category_id`	INT	NOT NULL,
	`name`	VARCHAR(50)	NOT NULL
);

CREATE TABLE `Author` (
	`author_id`	INT	NOT NULL,
	`name`	VARCHAR(100)	NOT NULL,
	`bio`	TEXT	NULL
);

CREATE TABLE `Publisher` (
	`publisher_id`	INT	NOT NULL,
	`name`	VARCHAR(100)	NOT NULL
);

CREATE TABLE `Book` (
	`book_id`	INT	NOT NULL,
	`category_id2`	INT	NOT NULL,
	`publisher_id2`	INT	NOT NULL,
	`title`	VARCHAR(255)	NOT NULL,
	`description`	TEXT	NULL,
	`isbn`	VARCHAR(13)	NULL,
	`cover_image_url`	VARCHAR(255)	NULL,
	`file_path`	VARCHAR(255)	NOT NULL,
	`page_count`	INT	NULL,
	`published_date`	DATE	NULL,
	`created_at`	TIMESTAMP	NOT NULL
);

CREATE TABLE `Book_Author` (
	`book_id2`	INT	NOT NULL,
	`author_id`	INT	NOT NULL
);

CREATE TABLE `Rental` (
	`rental_id`	INT	NOT NULL,
	`user_id2`	INT	NOT NULL,
	`book_id2`	INT	NOT NULL,
	`rental_date`	TIMESTAMP	NOT NULL,
	`due_date`	TIMESTAMP	NOT NULL,
	`status`	VARCHAR(20)	NOT NULL	DEFAULT 'RENTED',
	`last_updated_at`	TIMESTAMP	NULL
);

CREATE TABLE `ReadingLog` (
	`log_id`	INT	NOT NULL,
	`user_id2`	INT	NOT NULL,
	`book_id2`	INT	NOT NULL,
	`content`	TEXT	NULL,
	`rating`	TINYINT	NULL,
	`created_at`	TIMESTAMP	NOT NULL
);

-- 3. 기본 키 (PK) 설정
ALTER TABLE `User` ADD CONSTRAINT `PK_USER` PRIMARY KEY (
	`user_id`
);

ALTER TABLE `Category` ADD CONSTRAINT `PK_CATEGORY` PRIMARY KEY (
	`category_id`
);

ALTER TABLE `Author` ADD CONSTRAINT `PK_AUTHOR` PRIMARY KEY (
	`author_id`
);

ALTER TABLE `Publisher` ADD CONSTRAINT `PK_PUBLISHER` PRIMARY KEY (
	`publisher_id`
);

ALTER TABLE `Book` ADD CONSTRAINT `PK_BOOK` PRIMARY KEY (
	`book_id`
);

ALTER TABLE `Book_Author` ADD CONSTRAINT `PK_BOOK_AUTHOR` PRIMARY KEY (
	`book_id2`,
	`author_id`
);

ALTER TABLE `Rental` ADD CONSTRAINT `PK_RENTAL` PRIMARY KEY (
	`rental_id`
);

ALTER TABLE `ReadingLog` ADD CONSTRAINT `PK_READINGLOG` PRIMARY KEY (
	`log_id`
);

-- 4. 외래 키 (FK) 설정 (빠진 부분 모두 포함)

-- Book -> Category, Publisher
ALTER TABLE `Book` ADD CONSTRAINT `FK_Category_TO_Book` FOREIGN KEY (
	`category_id2`
)
REFERENCES `Category` (
	`category_id`
);

ALTER TABLE `Book` ADD CONSTRAINT `FK_Publisher_TO_Book` FOREIGN KEY (
	`publisher_id2`
)
REFERENCES `Publisher` (
	`publisher_id`
);

-- Book_Author -> Book, Author
ALTER TABLE `Book_Author` ADD CONSTRAINT `FK_Book_TO_Book_Author_1` FOREIGN KEY (
	`book_id2`
)
REFERENCES `Book` (
	`book_id`
);

ALTER TABLE `Book_Author` ADD CONSTRAINT `FK_Author_TO_Book_Author_1` FOREIGN KEY (
	`author_id`
)
REFERENCES `Author` (
	`author_id`
);

-- Rental -> User, Book
ALTER TABLE `Rental` ADD CONSTRAINT `FK_User_TO_Rental` FOREIGN KEY (
	`user_id2`
)
REFERENCES `User` (
	`user_id`
);

ALTER TABLE `Rental` ADD CONSTRAINT `FK_Book_TO_Rental` FOREIGN KEY (
	`book_id2`
)
REFERENCES `Book` (
	`book_id`
);

-- ReadingLog -> User, Book
ALTER TABLE `ReadingLog` ADD CONSTRAINT `FK_User_TO_ReadingLog` FOREIGN KEY (
	`user_id2`
)
REFERENCES `User` (
	`user_id`
);

ALTER TABLE `ReadingLog` ADD CONSTRAINT `FK_Book_TO_ReadingLog` FOREIGN KEY (
	`book_id2`
)
REFERENCES `Book` (
	`book_id`
);
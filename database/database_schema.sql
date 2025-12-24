-- =====================================================
-- Travelog Database Schema (Integrated)
-- =====================================================

DROP DATABASE IF EXISTS travelog;

CREATE DATABASE travelog
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE travelog;

-- =====================================================
-- 1. Users Table (사용자)
-- =====================================================
CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) UNIQUE NOT NULL,
    profile_image VARCHAR(500),
    bio TEXT,
    role VARCHAR(20) DEFAULT 'ROLE_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_nickname (nickname)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. Posts Table (게시물)
-- =====================================================
CREATE TABLE Posts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    travel_location VARCHAR(255) NOT NULL,
    caption TEXT,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    INDEX idx_posts_user_id (user_id),
    INDEX idx_posts_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. Travel_Records Table (여행 기록)
-- =====================================================
CREATE TABLE Travel_Records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_cost DECIMAL(12,2) DEFAULT 0,
    theme VARCHAR(100),
    is_public BOOLEAN DEFAULT true,

    -- 추가된 컬럼
    image_urls TEXT NULL COMMENT '여행 사진 URL 목록 (JSON 배열)',
    address VARCHAR(500) NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    INDEX idx_travel_records_user_id (user_id),
    INDEX idx_travel_records_dates (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. Tourist_Spots Table (관광지 정보)
-- =====================================================
CREATE TABLE Tourist_Spots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    country VARCHAR(100),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    category VARCHAR(50),
    main_photo VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tourist_spots_location (latitude, longitude),
    INDEX idx_tourist_spots_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. Travel_Details Table (여행 상세)
-- =====================================================
CREATE TABLE Travel_Details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    travel_record_id INT NOT NULL,
    tourist_spot_id INT,

    sequence_order INT NOT NULL,
    visit_date DATE,
    cost DECIMAL(10,2) DEFAULT 0,

    -- 기존
    review TEXT,
    photos JSON,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),

    -- 추가된 컬럼
    location_name VARCHAR(255) NULL COMMENT '장소 이름',
    description TEXT NULL COMMENT '메모/설명',
    image_urls TEXT NULL COMMENT '사진 URL 목록 (JSON 배열)',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (travel_record_id) REFERENCES Travel_Records(id) ON DELETE CASCADE,
    FOREIGN KEY (tourist_spot_id) REFERENCES Tourist_Spots(id) ON DELETE SET NULL,
    INDEX idx_travel_details_record_id (travel_record_id),
    INDEX idx_travel_details_spot_id (tourist_spot_id),
    INDEX idx_travel_details_order (travel_record_id, sequence_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. Comments Table (댓글)
-- =====================================================
CREATE TABLE Comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES Posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    INDEX idx_comments_post_id (post_id),
    INDEX idx_comments_user_id (user_id),
    INDEX idx_comments_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 7. Likes Table (좋아요)
-- =====================================================
CREATE TABLE Likes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES Posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_post_user (post_id, user_id),
    INDEX idx_likes_post_id (post_id),
    INDEX idx_likes_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 8. Follows Table (팔로우)
-- =====================================================
CREATE TABLE Follows (
    id INT AUTO_INCREMENT PRIMARY KEY,
    follower_id INT NOT NULL COMMENT '팔로우 하는 사용자',
    following_id INT NOT NULL COMMENT '팔로우 받는 사용자',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (follower_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES Users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_follow (follower_id, following_id),
    CHECK (follower_id != following_id),
    INDEX idx_follows_follower (follower_id),
    INDEX idx_follows_following (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 9. Notifications Table (알림)
-- =====================================================
CREATE TABLE Notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL COMMENT '알림 수신자',
    actor_id INT NOT NULL COMMENT '행동 사용자',
    type ENUM('like', 'comment', 'follow') NOT NULL,
    post_id INT,
    comment_id INT,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (actor_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Posts(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES Comments(id) ON DELETE CASCADE,
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_created_at (created_at DESC),
    INDEX idx_notifications_is_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Triggers
-- =====================================================
DELIMITER //

CREATE TRIGGER after_like_insert
AFTER INSERT ON Likes
FOR EACH ROW
BEGIN
    UPDATE Posts
    SET like_count = like_count + 1
    WHERE id = NEW.post_id;
END//

CREATE TRIGGER after_like_delete
AFTER DELETE ON Likes
FOR EACH ROW
BEGIN
    UPDATE Posts
    SET like_count = like_count - 1
    WHERE id = OLD.post_id;
END//

CREATE TRIGGER after_comment_insert
AFTER INSERT ON Comments
FOR EACH ROW
BEGIN
    UPDATE Posts
    SET comment_count = comment_count + 1
    WHERE id = NEW.post_id;
END//

CREATE TRIGGER after_comment_delete
AFTER DELETE ON Comments
FOR EACH ROW
BEGIN
    UPDATE Posts
    SET comment_count = comment_count - 1
    WHERE id = OLD.post_id;
END//

DELIMITER ;

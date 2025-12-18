# Travelog Database Design

## ERD (Entity Relationship Diagram)

### 테이블 설계

#### 1. Users (사용자)
```
- id (PK): BIGINT AUTO_INCREMENT
- email: VARCHAR(255) UNIQUE NOT NULL
- password: VARCHAR(255) NOT NULL
- nickname: VARCHAR(50) UNIQUE NOT NULL
- profile_image: VARCHAR(500)
- bio: TEXT
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

#### 2. Posts (게시물)
```
- id (PK): BIGINT AUTO_INCREMENT
- user_id (FK): BIGINT NOT NULL
- image_url: VARCHAR(500) NOT NULL
- travel_location: VARCHAR(255) NOT NULL
- caption: TEXT
- like_count: INT DEFAULT 0
- comment_count: INT DEFAULT 0
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

#### 3. Travel_Records (여행 기록)
```
- id (PK): BIGINT AUTO_INCREMENT
- user_id (FK): BIGINT NOT NULL
- title: VARCHAR(255) NOT NULL
- start_date: DATE NOT NULL
- end_date: DATE NOT NULL
- total_cost: DECIMAL(12,2) DEFAULT 0
- theme: VARCHAR(100)
- is_public: BOOLEAN DEFAULT true
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

#### 4. Travel_Details (여행 상세)
```
- id (PK): BIGINT AUTO_INCREMENT
- travel_record_id (FK): BIGINT NOT NULL
- tourist_spot_id (FK): BIGINT
- sequence_order: INT NOT NULL
- review: TEXT
- cost: DECIMAL(10,2) DEFAULT 0
- visit_date: DATE
- photos: JSON
- latitude: DECIMAL(10,8)
- longitude: DECIMAL(11,8)
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

#### 5. Tourist_Spots (관광지 정보)
```
- id (PK): BIGINT AUTO_INCREMENT
- name: VARCHAR(255) NOT NULL
- address: VARCHAR(500)
- city: VARCHAR(100)
- country: VARCHAR(100)
- latitude: DECIMAL(10,8)
- longitude: DECIMAL(11,8)
- category: VARCHAR(50)
- main_photo: VARCHAR(500)
- description: TEXT
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

#### 6. Comments (댓글)
```
- id (PK): BIGINT AUTO_INCREMENT
- post_id (FK): BIGINT NOT NULL
- user_id (FK): BIGINT NOT NULL
- content: TEXT NOT NULL
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
```

#### 7. Likes (좋아요)
```
- id (PK): BIGINT AUTO_INCREMENT
- post_id (FK): BIGINT NOT NULL
- user_id (FK): BIGINT NOT NULL
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- UNIQUE(post_id, user_id)
```

#### 8. Follows (팔로우)
```
- id (PK): BIGINT AUTO_INCREMENT
- follower_id (FK): BIGINT NOT NULL
- following_id (FK): BIGINT NOT NULL
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- UNIQUE(follower_id, following_id)
```

#### 9. Notifications (알림)
```
- id (PK): BIGINT AUTO_INCREMENT
- user_id (FK): BIGINT NOT NULL (알림을 받는 사용자)
- actor_id (FK): BIGINT NOT NULL (알림을 발생시킨 사용자)
- type: ENUM('like', 'comment', 'follow') NOT NULL
- post_id (FK): BIGINT (좋아요/댓글인 경우)
- comment_id (FK): BIGINT (댓글인 경우)
- is_read: BOOLEAN DEFAULT false
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### 관계 (Relationships)

1. **Users - Posts**: 1:N (한 사용자는 여러 게시물을 작성)
2. **Users - Travel_Records**: 1:N (한 사용자는 여러 여행 기록을 작성)
3. **Travel_Records - Travel_Details**: 1:N (한 여행 기록은 여러 상세 정보를 포함)
4. **Tourist_Spots - Travel_Details**: 1:N (한 관광지는 여러 여행 상세에서 참조)
5. **Posts - Comments**: 1:N (한 게시물은 여러 댓글을 포함)
6. **Users - Comments**: 1:N (한 사용자는 여러 댓글을 작성)
7. **Posts - Likes**: 1:N (한 게시물은 여러 좋아요를 받음)
8. **Users - Likes**: 1:N (한 사용자는 여러 좋아요를 누름)
9. **Users - Follows (follower)**: 1:N (한 사용자는 여러 사용자를 팔로우)
10. **Users - Follows (following)**: 1:N (한 사용자는 여러 팔로워를 가짐)
11. **Users - Notifications**: 1:N (한 사용자는 여러 알림을 받음)

### 인덱스 설계

```sql
-- Users
CREATE INDEX idx_users_email ON Users(email);
CREATE INDEX idx_users_nickname ON Users(nickname);

-- Posts
CREATE INDEX idx_posts_user_id ON Posts(user_id);
CREATE INDEX idx_posts_created_at ON Posts(created_at DESC);

-- Travel_Records
CREATE INDEX idx_travel_records_user_id ON Travel_Records(user_id);
CREATE INDEX idx_travel_records_dates ON Travel_Records(start_date, end_date);

-- Travel_Details
CREATE INDEX idx_travel_details_record_id ON Travel_Details(travel_record_id);
CREATE INDEX idx_travel_details_spot_id ON Travel_Details(tourist_spot_id);

-- Comments
CREATE INDEX idx_comments_post_id ON Comments(post_id);
CREATE INDEX idx_comments_user_id ON Comments(user_id);

-- Likes
CREATE INDEX idx_likes_post_id ON Likes(post_id);
CREATE INDEX idx_likes_user_id ON Likes(user_id);

-- Follows
CREATE INDEX idx_follows_follower ON Follows(follower_id);
CREATE INDEX idx_follows_following ON Follows(following_id);

-- Notifications
CREATE INDEX idx_notifications_user_id ON Notifications(user_id);
CREATE INDEX idx_notifications_created_at ON Notifications(created_at DESC);
```

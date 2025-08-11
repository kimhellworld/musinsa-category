DROP TABLE IF EXISTS categories;

-- 테이블 생성 (H2 호환)
CREATE TABLE categories (
                            id           BIGINT AUTO_INCREMENT PRIMARY KEY,        -- 카테고리 ID
                            ancestor_id  BIGINT,                                   -- 최상위 카테고리 ID
                            parent_id    BIGINT,                                   -- 상위 카테고리 ID
                            name         VARCHAR(100) NOT NULL,                    -- 화면 노출 이름
                            slug         VARCHAR(150) NOT NULL,                    -- 카테고리 slug
                            sort_order   INT NOT NULL DEFAULT 0,                   -- 정렬순서
                            is_active    BOOLEAN NOT NULL DEFAULT TRUE,            -- 활성여부 (TINYINT(1) -> BOOLEAN)
                            created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 생성일시
                            updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 수정일시 (*자동 갱신 주의)
                            deleted_at   TIMESTAMP                                  -- 삭제일시 (soft delete)
);

-- 인덱스는 H2에서 별도 문으로 생성하는 편이 안전합니다.
CREATE INDEX idx_category_slug      ON categories (slug);
CREATE INDEX idx_category_ancestor  ON categories (ancestor_id);
CREATE INDEX idx_category_parent    ON categories (parent_id);
CREATE INDEX idx_category_active    ON categories (is_active);
CREATE INDEX idx_category_deleted_at ON categories (deleted_at);

-- (선택) 주석: H2는 COMMENT ON 구문을 지원합니다.
COMMENT ON TABLE categories IS '카테고리';
COMMENT ON COLUMN categories.id IS '카테고리 ID';
COMMENT ON COLUMN categories.ancestor_id IS '최상위 카테고리 ID';
COMMENT ON COLUMN categories.parent_id IS '상위 카테고리 ID';
COMMENT ON COLUMN categories.name IS '화면에 표시되는 카테고리 이름';
COMMENT ON COLUMN categories.slug IS '카테고리 slug';
COMMENT ON COLUMN categories.sort_order IS '정렬순서';
COMMENT ON COLUMN categories.is_active IS '활성여부';
COMMENT ON COLUMN categories.created_at IS '생성일시';
COMMENT ON COLUMN categories.updated_at IS '수정일시';
COMMENT ON COLUMN categories.deleted_at IS '삭제일시';


-- 최상위 카테고리 (5개)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('패션', 'fashion', NULL, NULL, 1, 1, NOW(), NOW()),
('가전/디지털', 'electronics', NULL, NULL, 2, 1, NOW(), NOW()),
('가구/인테리어', 'furniture', NULL, NULL, 3, 1, NOW(), NOW()),
('식품/생필품', 'groceries', NULL, NULL, 4, 1, NOW(), NOW()),
('스포츠/레저', 'sports', NULL, NULL, 5, 1, NOW(), NOW());

-- 패션 카테고리 하위 (1번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('여성의류', 'women-clothing', 1, 1, 1, 1, NOW(), NOW()),
('남성의류', 'men-clothing', 1, 1, 2, 1, NOW(), NOW()),
('신발', 'shoes', 1, 1, 3, 1, NOW(), NOW()),
('가방/잡화', 'bags-accessories', 1, 1, 4, 1, NOW(), NOW());

-- 가전/디지털 카테고리 하위 (2번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('컴퓨터/노트북', 'computers', 2, 2, 1, 1, NOW(), NOW()),
('모바일/태블릿', 'mobile-tablets', 2, 2, 2, 1, NOW(), NOW()),
('가전제품', 'appliances', 2, 2, 3, 1, NOW(), NOW());

-- 가구/인테리어 카테고리 하위 (3번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('침실가구', 'bedroom-furniture', 3, 3, 1, 1, NOW(), NOW()),
('거실가구', 'living-room-furniture', 3, 3, 2, 1, NOW(), NOW()),
('주방가구', 'kitchen-furniture', 3, 3, 3, 1, NOW(), NOW()),
('인테리어소품', 'home-decor', 3, 3, 4, 1, NOW(), NOW());

-- 식품/생필품 카테고리 하위 (4번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('신선식품', 'fresh-food', 4, 4, 1, 1, NOW(), NOW()),
('가공식품', 'processed-food', 4, 4, 2, 1, NOW(), NOW());

-- 스포츠/레저 카테고리 하위 (5번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('피트니스', 'fitness', 5, 5, 1, 1, NOW(), NOW()),
('아웃도어', 'outdoor', 5, 5, 2, 1, NOW(), NOW()),
('수영/수상스포츠', 'swimming-water-sports', 5, 5, 3, 1, NOW(), NOW());

-- 여성의류 하위 카테고리 (6번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('티셔츠/탑', 'women-tshirts', 6, 1, 1, 1, NOW(), NOW()),
('블라우스/셔츠', 'women-blouses', 6, 1, 2, 1, NOW(), NOW()),
('원피스', 'women-dresses', 6, 1, 3, 1, NOW(), NOW()),
('스커트', 'women-skirts', 6, 1, 4, 1, NOW(), NOW());

-- 남성의류 하위 카테고리 (7번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('티셔츠', 'men-tshirts', 7, 1, 1, 1, NOW(), NOW()),
('셔츠', 'men-shirts', 7, 1, 2, 1, NOW(), NOW()),
('바지/청바지', 'men-pants', 7, 1, 3, 1, NOW(), NOW());

-- 신발 하위 카테고리 (8번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('여성화', 'women-shoes', 8, 1, 1, 1, NOW(), NOW()),
('남성화', 'men-shoes', 8, 1, 2, 1, NOW(), NOW()),
('스포츠화', 'sports-shoes', 8, 1, 3, 1, NOW(), NOW());

-- 가방/잡화 하위 카테고리 (9번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('여성가방', 'women-bags', 9, 1, 1, 1, NOW(), NOW()),
('남성가방', 'men-bags', 9, 1, 2, 1, NOW(), NOW()),
('지갑', 'wallets', 9, 1, 3, 1, NOW(), NOW());

-- 컴퓨터/노트북 하위 카테고리 (10번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('노트북', 'laptops', 10, 2, 1, 1, NOW(), NOW()),
('데스크탑', 'desktops', 10, 2, 2, 1, NOW(), NOW()),
('모니터', 'monitors', 10, 2, 3, 1, NOW(), NOW());

-- 모바일/태블릿 하위 카테고리 (11번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('스마트폰', 'smartphones', 11, 2, 1, 1, NOW(), NOW()),
('태블릿', 'tablets', 11, 2, 2, 1, NOW(), NOW()),
('웨어러블기기', 'wearables', 11, 2, 3, 1, NOW(), NOW());

-- 가전제품 하위 카테고리 (12번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('TV/오디오', 'tv-audio', 12, 2, 1, 1, NOW(), NOW()),
('냉장고/세탁기', 'major-appliances', 12, 2, 2, 1, NOW(), NOW()),
('주방가전', 'kitchen-appliances', 12, 2, 3, 1, NOW(), NOW());

-- 침실가구 하위 카테고리 (13번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('침대', 'beds', 13, 3, 1, 1, NOW(), NOW()),
('매트리스', 'mattresses', 13, 3, 2, 1, NOW(), NOW()),
('옷장/서랍장', 'wardrobes-dressers', 13, 3, 3, 1, NOW(), NOW());

-- 피트니스 하위 카테고리 (19번 카테고리의 하위)
INSERT INTO categories (name, slug, parent_id, ancestor_id, sort_order, is_active, created_at, updated_at)
VALUES
('헬스용품', 'fitness-equipment', 19, 5, 1, 1, NOW(), NOW()),
('요가/필라테스', 'yoga-pilates', 19, 5, 2, 1, NOW(), NOW()),
('런닝/조깅', 'running', 19, 5, 3, 1, NOW(), NOW());

# 무신사 카테고리 API

무신사 카테고리를 관리하는 REST API 입니다.

## 개발 환경

### 개발 언어
- Java 21

### 프레임워크
- Spring Boot 3.5.4
- Spring Data JPA
- Spring Boot Validation

### RDBMS
- H2 Database (Embedded)

## 애플리케이션 실행 방법

### 필수 요구사항
- Java 17 이상

### 빌드 및 실행
```bash
# 프로젝트 클론
git clone <repository-url>
cd musinsa-category

# 빌드
./gradlew clean build

# 실행
./gradlew bootRun
```
애플리케이션은 기본적으로 8080 포트에서 실행됩니다.

8080 포트가 사용중일 경우 아래 bootRun 명령의 {port}를 원하는 포트로 설정 후 실행합니다.
```bash
./gradlew bootRun --args='--server.port={port}'
```

## Database 명세

### 카테고리 테이블 (Category)
```sql
CREATE TABLE category (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    parent_id   BIGINT NULL,
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    order_num   INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (parent_id) REFERENCES category(id),
    UNIQUE KEY uk_category_slug (slug)
);
```

### 테이블 설명
- **id**: 카테고리 고유 식별자 (Primary Key)
- **parent_id**: 상위 카테고리 ID (NULL인 경우 최상위 카테고리)
- **name**: 카테고리 명
- **slug**: URL에 사용되는 카테고리 식별자 (고유값)
- **order_num**: 카테고리 정렬 순서
- **created_at**: 생성 일시
- **updated_at**: 수정 일시

## API 명세

### Swagger UI
API 문서는 Swagger를 통해 확인할 수 있습니다.
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API Docs JSON**: http://localhost:8080/v3/api-docs

### API 상세 명세

## 카테고리 등록

**Description**: 새로운 카테고리를 등록한다

**Endpoint**: POST /api/categories

**Request**:
```json
{
  "name": "도서",
  "parentId": 1,
  "order": 1
}
```

**Response**:
```json
{
  "id": 5,
  "name": "도서",
  "parentId": 1,
  "order": 1,
  "createdAt": "2025-08-10T07:20:00Z"
}
```

## 카테고리 수정

**Description**: 기존 카테고리 정보를 수정한다

**Endpoint**: PUT /api/categories/{id}

**Request**:
```json
{
  "name": "도서/출판",
  "parentId": 1,
  "order": 2
}
```

**Response**:
```json
{
  "id": 5,
  "name": "도서/출판",
  "parentId": 1,
  "order": 2,
  "createdAt": "2025-08-10T07:20:00Z",
  "updatedAt": "2025-08-10T08:30:00Z"
}
```

**에러 발생한 경우**:
```json
{
  "errorCode": "CATEGORY_NOT_FOUND",
  "errorMessage": "카테고리를 찾을 수 없습니다"
}
```

## 카테고리 삭제

**Description**: 카테고리를 삭제한다

**Endpoint**: DELETE /api/categories/{id}

**Request**: -

**Response**:
```json
{
  "success": true
}
```

**에러 발생한 경우**:
```json
{
  "errorCode": "CATEGORY_NOT_FOUND",
  "errorMessage": "카테고리를 찾을 수 없습니다"
}
```


## 전체 카테고리 조회

**Description**: 전체 카테고리를 트리 구조로 조회한다

**Endpoint**: GET /api/categories

**Request**: -

**Response**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "전자제품",
      "parentId": null,
      "order": 1,
      "children": [
        {
          "id": 3,
          "name": "컴퓨터",
          "parentId": 1,
          "order": 1,
          "children": [
            {
              "id": 4,
              "name": "노트북",
              "parentId": 3,
              "order": 1,
              "children": []
            }
          ]
        }
      ]
    },
    {
      "id": 2,
      "name": "의류",
      "parentId": null,
      "order": 2,
      "children": []
    }
  ]
}
```

## 특정 카테고리 및 하위 카테고리 조회

**Description**: 특정 카테고리와 그 하위 카테고리를 트리 구조로 조회한다

**Endpoint**: GET /api/categories/{id}

**Request**: -

**Response**:
```json
{
  "id": 1,
  "name": "전자제품",
  "parentId": null,
  "order": 1,
  "children": [
    {
      "id": 3,
      "name": "컴퓨터",
      "parentId": 1,
      "order": 1,
      "children": [
        {
          "id": 4,
          "name": "노트북",
          "parentId": 3,
          "order": 1,
          "children": []
        }
      ]
    }
  ]
}
```

**에러 발생한 경우**:
```json
{
  "errorCode": "CATEGORY_NOT_FOUND",
  "errorMessage": "카테고리를 찾을 수 없습니다"
}
```

**에러 발생한 경우**:
```json
{
  "errorCode": "CATEGORY_NOT_FOUND",
  "errorMessage": "카테고리를 찾을 수 없습니다"
}
```

## 주요 구현 기능

### 1. 카테고리 CRUD 기능
- 카테고리 생성, 조회, 수정, 삭제 API 제공
- 계층형 카테고리 구조 지원 (부모-자식 관계)

### 2. 트리 구조 조회
- 전체 카테고리를 트리 형태로 조회
- 특정 카테고리와 하위 카테고리를 트리 형태로 조회

### 3. 데이터베이스 설계
- H2 Embedded Database 사용
- 자기 참조 관계를 통한 계층형 구조 구현
- Unique 제약조건을 통한 데이터 무결성 보장

### 4. API 문서화
- Swagger를 통한 API 문서 자동 생성
- 상세한 요청/응답 스키마 정의
- 에러 케이스별 응답 명세

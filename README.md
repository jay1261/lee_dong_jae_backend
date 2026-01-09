</br>

# 송금 서비스 코딩테스트 과제 (이동재)

계좌 등록/삭제, 입금, 출금(일 한도), 이체(수수료/일 한도), 거래내역 조회 기능을 제공하는 간단한 송금 서비스 API입니다.  

</br>

---

</br>


## 1. Quick Start (Docker Compose)

> 아래 실행 방법은 `docker-compose.yml` 기준입니다.  
> DB/포트/환경변수는 프로젝트 설정에 맞게 조정 가능합니다.

### 1.1 Prerequisites
- Docker
- Docker Compose

### 1.2 Run
```bash
# 1) 프로젝트 클론

git clone https://github.com/jay1261/lee_dong_jae_backend.git
cd lee_dong_jae_backend.git

# 2) 프로젝트 빌드

./gradlew clean build

# 3) Docker 컨테이너 실행

docker compose up -d --build
```

### 1.3 Stop

```bash
docker compose down
```

### 1.4 포트 충돌 주의

```
8080 또는 3306 포트를 이미 사용 중인 경우, 
> docker-compose.yml의 포트 매핑을 수정해 주세요.
```

</br>

---
  
</br>

## 2. Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA (Hibernate)
- MySQL
- Docker / Docker Compose

</br>

---

</br>

## 3. Features

- 계좌 등록
- 계좌 삭제 (잔액 존재 시 409)
- 계좌 단일 조회
- 계좌 목록 조회 (페이지네이션)
- 입금
- 출금 (일 최대 1,000,000원 한도)
- 이체 (수수료 1%, 일 최대 3,000,000원 한도)
- 거래내역 조회 (최신순, 페이지네이션)

</br>

---

</br>

</br>

## 4. API Spec

### 4.1 Endpoint Summary

| 기능 | Method | URL | 설명 |
| --- | --- | --- | --- |
| 계좌 등록 | POST | `/api/accounts` | 새로운 계좌를 등록 |
| 계좌 삭제 | PATCH | `/api/accounts/{accountNumber}` | 계좌 해지 (잔액 존재 시 409) |
| 계좌 단일 조회 | GET | `/api/accounts/{accountNumber}` | 계좌 정보 조회 |
| 계좌 목록 조회 | GET | `/api/accounts?page=1&size=10` | 전체 계좌 목록 조회 |
| 입금 | POST | `/api/accounts/{accountNumber}/transactions/deposit` | 금액 입금 |
| 출금 | POST | `/api/accounts/{accountNumber}/transactions/withdraw` | 금액 출금(한도/잔액 체크) |
| 이체 | POST | `/api/accounts/transfer` | 송금 (수수료 1% + 한도 체크) |
| 거래내역 조회 | GET | `/api/accounts/{accountNumber}/transactions?page=1&size=20` | 최신순 거래내역 조회 |

> 페이지네이션: `page`는 1부터 시작한다고 가정합니다.

</br>

---

</br>

## 5. Common Response Format

### Success

```json
{
  "httpCode": 200,
  "message": "요청에 성공했습니다.",
  "data": {}
}
```

### Error

```json
{
  "httpCode": 409,
  "errorCode": "SOME_ERROR_CODE",
  "message": "에러 메시지"
}
```

</br>

---

</br>

## 6. API Request/Response Examples

<details>
<summary><b>1) 계좌 등록</b></summary>

### POST `/api/accounts`

#### Response (201)

```json
{
  "httpCode": 201,
  "message": "계좌 등록에 성공했습니다.",
  "data": {
    "accountNumber": "20260108-0000001",
    "limits": {
      "withdraw": 1000000,
      "transfer": 3000000
    }
  }
}
```

</details>

---

<details>
<summary><b>2) 계좌 삭제</b></summary>

### PATCH `/api/accounts/{accountNumber}`

#### Response (200)

```json
{
  "httpCode": 200,
  "message": "계좌 삭제에 성공했습니다.",
  "data": {}
}
```

#### Error

| httpCode | errorCode | message |
| --- | --- | --- |
| 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
| 409 | ACCOUNT_CLOSED | 해지된 계좌입니다. |
| 409 | ACCOUNT_BALANCE_REMAIN | 계좌에 잔액이 남아 있습니다. |

</details>

---

<details>
<summary><b>3) 계좌 단일 조회</b></summary>

### GET `/api/accounts/{accountNumber}`

#### Response (200)

```json
{
  "httpCode": 200,
  "message": "계좌 조회에 성공했습니다.",
  "data": {
    "accountNumber": "20260108-0000001",
    "balance": 500000,
    "status": "ACTIVE",
    "limits": {
      "withdraw": 1000000,
      "transfer": 3000000
    }
  }
}
```

#### Error

| httpCode | errorCode | message |
| --- | --- | --- |
| 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |

</details>

---


<details>
<summary><b>4) 계좌 목록 조회</b></summary>

### GET `/api/accounts?page=1`

#### Response (200)

```json
{
    "httpCode": 200,
    "message": "계좌 목록 조회에 성공했습니다.",
    "data": {
        "content": [
            {
                "accountNumber": "20260109-32004524",
                "status": "ACTIVE"
            },
            {
                "accountNumber": "20260109-48600517",
                "status": "CLOSED"
            },
            {
                "accountNumber": "20260109-95479285",
                "status": "ACTIVE"
            },
            {
                "accountNumber": "20260109-17140323",
                "status": "ACTIVE"
            },
            {
                "accountNumber": "20260109-77435790",
                "status": "ACTIVE"
            }
        ],
        "page": 1,
        "size": 20,
        "totalElements": 5,
        "totalPages": 1
    }
}
```

</details>

---


<details>
<summary><b>5) 입금</b></summary>

### POST `/api/accounts/{accountNumber}/deposit`

#### Request

```json
{
  "amount": 10000
}
```

#### Response (201)

```json
{
  "httpCode": 201,
  "message": "입금에 성공하였습니다.",
  "data": {
    "transactionId": 1,
    "accountNumber": "20260108-0000001",
    "amount": 10000,
    "balance": 510000
  }
}
```

#### Error

| httpCode | errorCode | message |
| --- | --- | --- |
| 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
| 400 | DEPOSIT_AMOUNT_REQUIRED | 입금 금액을 입력해주세요. |
| 400 | DEPOSIT_AMOUNT_INVALID | 입금 금액은 0보다 커야 합니다. |

</details>

---

<details>
<summary><b>6) 출금</b></summary>

### POST `/api/accounts/{accountNumber}/withdraw`

#### Request

```json
{
  "amount": 10000
}
```

#### Response (201)

```json
{
  "httpCode": 201,
  "message": "출금에 성공하였습니다.",
  "data": {
    "transactionId": 2,
    "accountNumber": "20260108-0000001",
    "amount": 10000,
    "balance": 500000
  }
}
```

#### Error

| httpCode | errorCode | message |
| --- | --- | --- |
| 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
| 400 | WITHDRAW_AMOUNT_REQUIRED | 출금 금액을 입력해주세요. |
| 400 | WITHDRAW_AMOUNT_INVALID | 출금 금액은 0보다 커야 합니다. |
| 409 | INSUFFICIENT_BALANCE | 잔액이 부족합니다. |
| 409 | WITHDRAW_LIMIT_EXCEEDED | 일일 출금 한도를 초과했습니다. |

</details>

---

<details>
<summary><b>7) 이체</b></summary>

### POST `/api/accounts/transfer`

#### Request

```json
{
  "fromAccountNumber": "20260108-0000001",
  "toAccountNumber": "20260108-0000002",
  "amount": 10000
}
```

#### Response (201)

```json
{
  "httpCode": 201,
  "message": "이체에 성공하였습니다.",
  "data": {
    "transactionId": 3,
    "fromAccountNumber": "20260108-0000001",
    "toAccountNumber": "20260108-0000002",
    "amount": 10000,
    "fee": 100,
    "balance": 500000
  }
}
```

#### Error

| httpCode | errorCode | message |
| --- | --- | --- |
| 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
| 404 | COUNTERPARTY_ACCOUNT_NOT_FOUND | 상대 계좌를 찾을 수 없습니다. |
| 400 | TRANSFER_AMOUNT_REQUIRED | 이체 금액을 입력해주세요. |
| 400 | TRANSFER_AMOUNT_INVALID | 이체 금액은 0보다 커야 합니다. |
| 409 | INSUFFICIENT_BALANCE | 잔액이 부족합니다. |
| 409 | TRANSFER_LIMIT_EXCEEDED | 일일 이체 한도를 초과했습니다. |

</details>

---

<details>
<summary><b>8) 거래내역 조회</b></summary>

### GET `/api/accounts/{accountNumber}/transactions?page=1`

#### Response (200)

```json
{
    "httpCode": 200,
    "message": "거래내역 조회에 성공하였습니다.",
    "data": {
        "content": [
            {
                "transactionType": "TRANSFER_IN",
                "counterpartyAccountNumber": "20260109-17140323",
                "amount": 10000,
                "fee": null,
                "balance": 2989900,
                "createdAt": "2026-01-09T23:38:07.719914"
            },
            {
                "transactionType": "TRANSFER_OUT",
                "counterpartyAccountNumber": "20260109-95479285",
                "amount": 10000,
                "fee": 100,
                "balance": 2979900,
                "createdAt": "2026-01-09T23:37:10.533857"
            },
            {
                "transactionType": "WITHDRAW",
                "counterpartyAccountNumber": null,
                "amount": 10000,
                "fee": null,
                "balance": 2990000,
                "createdAt": "2026-01-09T23:36:27.385732"
            },
            {
                "transactionType": "DEPOSIT",
                "counterpartyAccountNumber": null,
                "amount": 3000000,
                "fee": null,
                "balance": 3000000,
                "createdAt": "2026-01-09T23:34:55.765582"
            }
        ],
        "page": 1,
        "size": 20,
        "totalElements": 4,
        "totalPages": 1
    }
}
```

#### Error

| httpCode | errorCode | message |
| --- | --- | --- |
| 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
| 409 | ACCOUNT_CLOSED | 해지된 계좌번호입니다. |

</details>


</br>

---

</br>

## 7. Database Design

### 7.1 ERD
<img width="3412" height="2294" alt="image" src="https://github.com/user-attachments/assets/e82d7a39-b816-474d-b75d-267d782d79ff" />


### 7.2 Tables

#### `account_policies`

계좌 유형별 수수료 정책을 관리하는 테이블입니다.  
정책을 분리하여 추후 새로운 계좌 유형(BASIC, VIP, PREMIUM 등)을 유연하게 확장할 수 있도록 설계했습니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 정책 ID |
| fee_rate | DECIMAL | 이체 수수료율 |
| policy_name | VARCHAR | 정책 이름 (BASIC, VIP, PREMIUM 등) |
| created_at | DATETIME | 생성 시각 |

</br>

---

</br>

#### `accounts`

사용자의 계좌 정보를 관리하는 테이블입니다.  
각 계좌는 하나의 수수료 정책(`account_policies`)을 참조합니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 계좌 ID |
| account_policy_id | BIGINT (FK) | 수수료 정책 ID |
| account_number | VARCHAR | 계좌 번호 (UNIQUE) |
| balance | BIGINT | 현재 잔액 |
| status | VARCHAR | 계좌 상태 (ACTIVE, CLOSED) |
| created_at | DATETIME | 생성 시각 |

</br>

---

</br>

#### `account_settings`

계좌별 사용자 설정 정보를 관리하는 테이블입니다.  
출금 및 이체 한도를 계좌 단위로 관리하여 확장성을 고려했습니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 설정 ID |
| account_id | BIGINT (FK) | 계좌 ID |
| withdraw_limit | BIGINT | 일일 출금 한도 |
| transfer_limit | BIGINT | 일일 이체 한도 |
| created_at | DATETIME | 생성 시각 |

</br>

---

</br>

#### `account_daily_limits`

계좌의 일일 사용 금액(누적)을 관리하는 테이블입니다.  
매일 최초 거래 발생 시 생성되며, 한도 초과 여부를 판단하는 데 사용됩니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 일일 한도 ID |
| account_id | BIGINT (FK) | 계좌 ID |
| withdraw_amount | BIGINT | 당일 출금 누적 금액 |
| transfer_amount | BIGINT | 당일 이체 누적 금액 |
| date | DATE | 기준 날짜 |
| created_at | DATETIME | 생성 시각 |

</br>

---

</br>

#### `transactions`

입금, 출금, 이체에 대한 모든 거래 내역을 관리하는 테이블입니다.  
이체의 경우 송금/수신을 각각 `TRANSFER_OUT`, `TRANSFER_IN`으로 분리 저장합니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 거래 ID |
| account_id | BIGINT (FK) | 거래 발생 계좌 ID |
| transaction_type | VARCHAR | 거래 타입 (DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN) |
| amount | BIGINT | 거래 금액 |
| fee | BIGINT | 수수료 (해당 없을 경우 null) |
| balance_after | BIGINT | 거래 이후 잔액 |
| counterparty_account_id | BIGINT (FK) | 상대 계좌 ID (이체 시 사용) |
| created_at | DATETIME | 거래 시각 |

</br>

---

</br>

## 8. Design Notes

### **1. 확장성 설계**

- **수수료 정책 분리**
    - 계좌 유형별 수수료 정책을 account_policies 테이블로 분리하여 관리
    - 신규 계좌 유형 추가 또는 수수료 변경 시 비즈니스 로직 변경 없이 데이터 확장 가능
- **계좌 설정 분리**
    - 계좌별 한도 및 정책 정보를 account_settings 테이블로 분리
    - 계좌 단위 정책 확장(한도, 옵션 등)에 유연하게 대응 가능

### **2. 한도 관리 방식**

- **일별 누적 한도 관리**
    - 출금: 일 최대 1,000,000원
    - 이체: 일 최대 3,000,000원
    - 일 누적 금액은 account_daily_limits 테이블에서 관리

### **3. 거래 내역 설계**

- **최신순 정렬**을 기본으로 제공
- **이체 거래 분리 저장**
    - TRANSFER_OUT / TRANSFER_IN 으로 각각 저장
    - 계좌 기준 거래내역 조회 시 조인 없이 단순 조회 가능
    - 실제 금융 서비스에서 자주 사용하는 패턴

### **4. 동시성 제어**

- **비관적 락(Pessimistic Lock) 적용**
    - 계좌 조회 시 PESSIMISTIC_WRITE 락 적용
    - 입금 / 출금 / 이체 요청 동시 처리 시 잔액 정합성 보장

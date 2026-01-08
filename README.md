## API 명세서

| 기능 | Method | URL | 요청 예시 | 설명 |
| --- | --- | --- | --- | --- |
| 계좌 등록 | POST | /api/accounts | - | 새로운 계좌를 등록 |
| 계좌 삭제 | PATCH | /api/accounts/{accountNumber} | - | 계좌 해지, 잔액 존재 시 409 |
| 계좌 단일 조회 | GET | /api/accounts/{accountNumber} | - | 계좌 정보 조회 |
| 계좌 목록 조회 | GET | /api/accounts?page=1 | - | 전체 계좌 목록 조회 |
| 입금 | POST | /api/accounts/{accountNumber}/deposit | `{ "amount": 10000 }` | 금액 입금 |
| 출금 | POST | /api/accounts/{accountNumber}/withdraw | `{ "amount": 10000 }` | 금액 출금, 한도/잔액 체크 |
| 이체 | POST | /api/accounts/transfer | `{ "fromAccountNumber": "...", "toAccountNumber": "...", "amount": 10000 }` | 송금, 수수료 1% 적용 |
| 거래내역 조회 | GET | /api/accounts/{accountNumber}/transactions?page=1 | - | 최신순 거래내역 조회 |


</br>


## API 응답, 요청 예시


<details>
<summary>1. 계좌 등록</summary>

- POST /api/accounts
- Response
    
    ```java
    {
    	"httpCode": 201,
    	"message": "계좌 등록에 성공했습니다."
    	"data" : {
    		"accountNumber": "20260108-0000001",
    		"limits": {
    			"withdraw": 1000000,
    			"transfer": 3000000
    		}
    	}
    }
    ```



</details>


<details>
<summary>2. 계좌 삭제</summary>

- PATCH  /api/accounts/{accountNumber}
- Response
    
    ```java
    {
    	"httpCode": 200,
    	"message" : "계좌 삭제에 성공했습니다."
    	"data" : {
    		"accountNumber": "20260108-0000001",
    	}
    }
    ```
    
- error
    
    
    | httpCode | errorCode | message |
    | --- | --- | --- |
    | 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
    | 409 | ACCOUNT_CLOSED | 해지된 계좌입니다 |
    | 409 | ACCOUNT_BALANCE_REMAIN | 계좌에 잔액이 남아 있습니다. |

</details>


<details>
<summary>3. 계좌 단일 조회</summary>

- GET  /api/accounts/{accountNumber}
- Response
    
    ```java
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
    
- error
    
    | httpCode | errorCode | message |
    | --- | --- | --- |
    | 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |



</details>


<details>
<summary>4. 계좌 목록 조회</summary>

- GET  /api/accounts?page=1
- Response
    
    ```java
    {
      "httpCode": 200,
      "message": "계좌 목록 조회에 성공했습니다.",
      "data": [
        {
          "accountNumber": "20260108-0000001",
          "status": "ACTIVE"
        },
        {
          "accountNumber": "20260108-0000002",
          "status": "CLOSED"
        }
      ]
    }
    ```


</details>


<details>
<summary>5. 입금</summary>

- POST  /api/accounts/{accountNumber}/deposit
- Request
    
    ```java
    {
    	"amount": 10000
    }
    ```
    
- Response
    
    ```java
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
    
- error

    | httpCode | errorCode | message |
    | --- | --- | --- |
    | 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
    | 400 | DEPOSIT_AMOUNT_REQUIRED | 입금 금액을 입력해주세요. |
    | 400 | DEPOSIT_AMOUNT_INVALID | 입금 금액은 0보다 커야 합니다. |


</details>


<details>
<summary>6. 출금</summary>

- POST  /api/accounts/{accountNumber}/withdraw
- Request
    
    ```java
    {
    	"amount": 10000
    }
    ```
    
- Response
    
    ```java
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
    
- error

    | httpCode | errorCode | message |
    | --- | --- | --- |
    | 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
    | 400 | WITHDRAW_AMOUNT_REQUIRED | 출금 금액을 입력해주세요 |
    | 400 | WITHDRAW_AMOUNT_INVALID | 출금 금액은 0보다 커야 합니다. |
    | 409 | INSUFFICIENT_BALANCE | 잔액이 부족합니다 |
    | 409 | WITHDRAW_LIMIT_EXCEEDED | 일일 출금 한도를 초과했습니다. |


</details>


<details>
<summary>7. 이체</summary>

- POST  /api/transactions/transfer
- Request

    ```java
    {
    	"fromAccountNumber": "20260108-0000001"
    	"toAccountNumber": "20260108-0000002"
    	"amount": 10000
    }
    
    ```

- Response

    ```java
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

- error

    | httpCode | errorCode | message |
    | --- | --- | --- |
    | 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
    | 404 | COUNTERPARTY_ACCOUNT_NOT_FOUND | 상대 계좌를 찾을 수 없습니다. |
    | 400 | TRANSFER_AMOUNT_REQUIRED | 이체 금액을 입력해주세요 |
    | 400 | TRANSFER_AMOUNT_INVALID | 이체 금액은 0보다 커야 합니다. |
    | 409 | INSUFFICIENT_BALANCE | 잔액이 부족합니다 |
    | 409 | TRANSFER_LIMIT_EXCEEDED | 일일 이체 한도를 초과했습니다. |


</details>


<details>
<summary>8. 거래내역 조회</summary>

- GET  /api/accounts/{accountNumber}/transactions?page=1
- Response
    
    ```java
    
    {
      "httpCode": 200,
      "message": "거래내역 조회에 성공하였습니다.",
      "data": [
        {
          "transactionType": "TRANSFER_OUT",
          "counterpartyAccountNumber": "20260108-0000002",
          "amount": 10000,
          "fee": 100,
          "balance": 510000,
          "createdAt": "2026-01-08T10:00:00"
        },
        {
          "transactionType": "TRANSFER_IN",
          "counterpartyAccountNumber": "20260108-0000001",
          "amount": 10000,
          "fee": null,
          "balance": 520000,
          "createdAt": "2026-01-08T09:50:00"
        },
        {
          "transactionType": "DEPOSIT",
          "counterpartyAccountNumber": null,
          "amount": 10000,
          "fee": null,
          "balance": 510000,
          "createdAt": "2026-01-08T09:30:00"
        },
        {
          "transactionType": "WITHDRAW",
          "counterpartyAccountNumber": null,
          "amount": 10000,
          "fee": null,
          "balance": 500000,
          "createdAt": "2026-01-08T09:00:00"
        }
      ]
    }
    ```

- error

    | httpCode | errorCode | message |
    | --- | --- | --- |
    | 404 | ACCOUNT_NOT_FOUND | 존재하지 않는 계좌번호입니다. |
    | 409 | ACCOUNT_CLOSED | 해지된 계좌번호입니다 |


</details>


</br>

## Database 설계

<img width="3886" height="2210" alt="image" src="https://github.com/user-attachments/assets/a0f19164-72dc-4c7f-8647-8468ca6f635e" />


### account_policies

계좌 유형별 수수료 정책을 관리하는 테이블입니다.
정책을 분리하여 추후 새로운 계좌 유형(BASIC, VIP, PREMIUM 등)을 유연하게 확장할 수 있도록 설계했습니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 정책 ID |
| fee_rate | DECIMAL | 이체 수수료율 |
| policy_name | VARCHAR | 정책 이름 (BASIC, VIP, PREMIUM 등) |
| created_at | DATETIME | 생성 시각 |

### accounts

사용자의 계좌 정보를 관리하는 테이블입니다.

각 계좌는 하나의 수수료 정책(`account_policies`)을 참조합니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 계좌 ID |
| account_policy_id | BIGINT (FK) | 수수료 정책 ID |
| account_number | VARCHAR | 계좌 번호 (유니크) |
| balance | BIGINT | 현재 잔액 |
| status | VARCHAR | 계좌 상태 (ACTIVE, CLOSED) |
| created_at | DATETIME | 생성 시각 |

### account_settings

계좌별 사용자 설정 정보를 관리하는 테이블입니다.
출금 및 이체 한도를 계좌 단위로 관리하여 확장성을 고려했습니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 설정 ID |
| account_id | BIGINT (FK) | 계좌 ID |
| withdraw_limit | BIGINT | 일일 출금 한도 |
| transfer_limit | BIGINT | 일일 이체 한도 |
| created_at | DATETIME | 생성 시각 |

### account_daily_limits

계좌의 **일일 사용 금액**을 관리하는 테이블입니다.

매일 최초 거래 발생 시 생성되며, 한도 초과 여부를 판단하는 데 사용됩니다.

| 컬럼명 | 타입 | 설명 |
| --- | --- | --- |
| id | BIGINT (PK) | 일일 한도 ID |
| account_id | BIGINT (FK) | 계좌 ID |
| withdraw_amount | BIGINT | 당일 출금 누적 금액 |
| transfer_amount | BIGINT | 당일 이체 누적 금액 |
| date | DATE | 기준 날짜 |
| created_at | DATETIME | 생성 시각 |

### transactions

입금, 출금, 이체에 대한 모든 거래 내역을 관리하는 테이블입니다.

이체의 경우 송금/수신을 각각 `TRANSFER_OUT`, `TRANSFER_IN`으로 분리하여 저장합니다.

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

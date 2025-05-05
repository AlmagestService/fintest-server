# Fintest Server

오픈뱅킹 API를 시뮬레이션하는 핀테크 테스트베드 서버입니다.

## 주요 기능
- 통합 계좌 조회
- 계좌 잔액 조회
- 거래내역 조회
- 수취인 정보 조회
- 계좌 이체

※ 모든 오픈뱅킹 API는 발급된 API 키로 인증하며, 키는 일일 1,000회 호출 제한이 있습니다.

## 오픈뱅킹 API 엔드포인트
```
POST /api/fintest/a1/v1/openbank/accounts    - 통합계좌조회
POST /api/fintest/a1/v1/openbank/account     - 계좌 잔액 조회
POST /api/fintest/a1/v1/openbank/tranlist    - 거래내역 조회
POST /api/fintest/a1/v1/openbank/recipient   - 수취인 정보 조회
POST /api/fintest/a1/v1/openbank/transfer    - 계좌 이체
```



package com.almagest_dev.fintest_server.controller.v1;


import com.almagest_dev.fintest_server.dto.CommonResponseDto;
import com.almagest_dev.fintest_server.dto.account.IntegrateAccountRequestDto;
import com.almagest_dev.fintest_server.dto.account.IntegrateAccountResponseDto;
import com.almagest_dev.fintest_server.dto.account.SingleAccountRequestDto;
import com.almagest_dev.fintest_server.dto.account.SingleAccountResponseDto;
import com.almagest_dev.fintest_server.dto.recipient.RecipientInquiryRequestDto;
import com.almagest_dev.fintest_server.dto.recipient.RecipientInquiryResponseDto;
import com.almagest_dev.fintest_server.dto.transaction.TransactionListRequestDto;
import com.almagest_dev.fintest_server.dto.transaction.TransactionListResponseDto;
import com.almagest_dev.fintest_server.dto.withdrawal.TransactionRequestDto;
import com.almagest_dev.fintest_server.dto.withdrawal.TransactionResponseDto;
import com.almagest_dev.fintest_server.entity.*;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.almagest_dev.fintest_server.util.constants.Formatter.API_FORMATTER;

/**
 * 인터셉터 적용 흐름
 * 필터 -> 컨트롤러 진입 -> 인터셉터 pre 실행 (API 로깅 생성)
 * 컨트롤러 및 서비스 로직 실행 -> 인터셉터 post 실행 (API 로깅 상태 변경)
 */

// 은행(오픈뱅킹) 관련 API를 처리하는 컨트롤러 클래스
@RestController
@RequestMapping("/api/fintest/a1/v1/openbank")
@RequiredArgsConstructor
public class BankControllerV1 {
    private final BankService bankService;
    private final ApiKeyService apiKeyService;
    private final PersonService personService;
    private final PersonDataGenerateService personDataGenerateService;

    /**
     * 모든 계좌 조회 API
     * @param request HTTP 요청 객체
     * @param requestDto 통합계좌조회 요청 DTO
     * @param authorizationHeader 인증 헤더
     * @return 통합계좌조회 응답 DTO
     */
    @PostMapping("/accounts")
    public ResponseEntity<?> getAllAccounts(
            HttpServletRequest request,
            @RequestBody IntegrateAccountRequestDto requestDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // ----------- api 공통
        //apikey 추출 및 검증
        String apiKey = extractApiKey(authorizationHeader);
        apiKeyService.decreaseCallCount(apiKey);

        String apiTranId = (String) request.getAttribute("apiTranId");
        CommonResponseDto<IntegrateAccountResponseDto> responseDto = new CommonResponseDto<>();

        // ----------- 개별 로직
        List<PersonAccounts> allAccounts = new ArrayList<>();
        List<BankAccountProducts> productsByCode = new ArrayList<>();
        List<String> productCodes = new ArrayList<>();
        String userFinanceId = requestDto.getUserFinanceId();
        String rspMessage = "";

        // //userFinanceKey 포함여부 확인
        if (userFinanceId == null || userFinanceId.length() != 36) {
            //----사용자식별코드 생성
            Person person = personService.createPerson(requestDto.getServiceName());
            userFinanceId = String.valueOf(person.getUserFinanceId());

            //----개인 계좌정보 생성
            allAccounts = personDataGenerateService.createPersonAccountData(userFinanceId, requestDto.getUserName());

            //-----계좌기반 거래목록 생성
            personDataGenerateService.generateTransactions(userFinanceId);

            rspMessage = "사용자 데이터 생성 성공";
        } else{
            //값이 있으면 조회
            allAccounts = bankService.getAllAccounts(requestDto);
            rspMessage = "사용자 데이터 조회 성공";
        }

        // 계좌의 상품코드로 상품정보 가져오기
        // 상품코드 추출
        for (PersonAccounts personAccounts : allAccounts) {
            productCodes.add(personAccounts.getProductCode());
        }
        productsByCode = bankService.getProductsByCode(productCodes);


        // Dto 변환
        IntegrateAccountResponseDto integrateAccountResponseDto = bankService.processIntegrateAccountData(
                productsByCode,
                allAccounts,
                userFinanceId,
                request
        );


        // ----------- 완료 로직
        ApiRequestLog attribute = (ApiRequestLog) request.getAttribute("apiRequestLog");
        String apiTranDate = attribute.getCreatedDate().format(API_FORMATTER);


        responseDto.setApiTranId(apiTranId);
        responseDto.setApiTranDtm(apiTranDate);
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage(rspMessage);
        responseDto.setData(integrateAccountResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 계좌 잔액 조회 API
     * @param request HTTP 요청 객체
     * @param requestDto 단일계좌조회 요청 DTO
     * @param authorizationHeader 인증 헤더
     * @return 단일계좌조회 응답 DTO
     */
    @PostMapping("/account")
    public ResponseEntity<?> getAccount(
            HttpServletRequest request,
            @RequestBody SingleAccountRequestDto requestDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // ----------- api 공통
        //apikey 추출 및 검증
        String apiKey = extractApiKey(authorizationHeader);
        apiKeyService.decreaseCallCount(apiKey);

        String apiTranId = (String) request.getAttribute("apiTranId");
        CommonResponseDto<SingleAccountResponseDto> responseDto = new CommonResponseDto<>();

        // ----------- 개별 로직
        // 계좌정보 조회
        PersonAccounts account = bankService.getAccount(requestDto.getFintechUseNum(), requestDto.getUserFinanceId());
        OrgCode bankInfo = bankService.getBankInfo(account.getBankCode());
        BankAccountProducts accountProduct = bankService.getAccountProduct(account.getProductCode());

        // Dto 변환
        SingleAccountResponseDto singleAccountResponseDto = bankService.processSingleAccountData(
                account,
                bankInfo,
                accountProduct
        );

        // ----------- 완료 로직
        ApiRequestLog attribute = (ApiRequestLog) request.getAttribute("apiRequestLog");
        String apiTranDate = attribute.getCreatedDate().format(API_FORMATTER);


        responseDto.setApiTranId(apiTranId);
        responseDto.setApiTranDtm(apiTranDate);
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("잔액조회 성공");
        responseDto.setData(singleAccountResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 거래목록 조회 API
     * @param request HTTP 요청 객체
     * @param requestDto 거래목록조회 요청 DTO
     * @param authorizationHeader 인증 헤더
     * @return 거래목록조회 응답 DTO
     */
    @PostMapping("/tranlist")
    public ResponseEntity<?> getTransactionList(
            HttpServletRequest request,
            @RequestBody TransactionListRequestDto requestDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // ----------- api 공통
        //apikey 추출 및 검증
        String apiKey = extractApiKey(authorizationHeader);
        apiKeyService.decreaseCallCount(apiKey);

        String apiTranId = (String) request.getAttribute("apiTranId");
        CommonResponseDto<TransactionListResponseDto> responseDto = new CommonResponseDto<>();

        // ----------- 개별 로직
        // 거래목록 조회
        List<BankTransaction> transactions = bankService.getTransactions(requestDto);
        PersonAccounts account = bankService.getAccount(requestDto.getFintechUseNum(), null);
        OrgCode bankInfo = bankService.getBankInfo(account.getBankCode());

        // Dto 변환
        TransactionListResponseDto transactionListResponseDto = bankService.processTransferListData(
                transactions,
                account,
                bankInfo,
                requestDto
        );

        // ----------- 완료 로직
        ApiRequestLog attribute = (ApiRequestLog) request.getAttribute("apiRequestLog");
        String apiTranDate = attribute.getCreatedDate().format(API_FORMATTER);


        responseDto.setApiTranId(apiTranId);
        responseDto.setApiTranDtm(apiTranDate);
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("거래목록 조회 성공");
        responseDto.setData(transactionListResponseDto);


        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 수취인 정보 조회 API
     * @param request HTTP 요청 객체
     * @param requestDto 수취인조회 요청 DTO
     * @param authorizationHeader 인증 헤더
     * @return 수취인조회 응답 DTO
     */
    @PostMapping("/recipient")
    public ResponseEntity<?> getRecipient(
            HttpServletRequest request,
            @RequestBody RecipientInquiryRequestDto requestDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        // ----------- api 공통
        //apikey 추출 및 검증
        String apiKey = extractApiKey(authorizationHeader);
        apiKeyService.decreaseCallCount(apiKey);

        String apiTranId = (String) request.getAttribute("apiTranId");
        CommonResponseDto<RecipientInquiryResponseDto> responseDto = new CommonResponseDto<>();

        // ----------- 개별 로직
        // 수취인 조회
        PersonAccounts recipient = bankService.getRecipient(requestDto);
        OrgCode bankInfo = bankService.getBankInfo(recipient.getBankCode());
        OrgCode wBankInfo = bankService.getBankInfo(requestDto.getBankCodeStd());

        // Dto 변환
        RecipientInquiryResponseDto recipientInquiryResponseDto = bankService.processRecipientData(
                recipient, bankInfo, wBankInfo, requestDto);


        // ----------- 완료 로직
        ApiRequestLog attribute = (ApiRequestLog) request.getAttribute("apiRequestLog");
        String apiTranDate = attribute.getCreatedDate().format(API_FORMATTER);


        responseDto.setApiTranId(apiTranId);
        responseDto.setApiTranDtm(apiTranDate);
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("수취인 조회 성공");
        responseDto.setData(recipientInquiryResponseDto);


        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 송금 요청 API
     * @param request HTTP 요청 객체
     * @param requestDto 송금 요청 DTO
     * @param authorizationHeader 인증 헤더
     * @return 송금 응답 DTO
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> requestTransfer(
            HttpServletRequest request,
            @RequestBody TransactionRequestDto requestDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        // ----------- api 공통
        //apikey 추출 및 검증
        String apiKey = extractApiKey(authorizationHeader);
        apiKeyService.decreaseCallCount(apiKey);

        String apiTranId = (String) request.getAttribute("apiTranId");
        CommonResponseDto<TransactionResponseDto> responseDto = new CommonResponseDto<>();

        // ----------- 개별 로직

        /*출금시 상대계좌기준 입금데이터도 생성
         * -사용자 식별자 검증
         * -출금계좌 검증
         * -출금계좌 베타락 필요
         * -수취계좌 검증
         * -현재 잔고와 이체금액 차이가 0보다 작은지 검증
         * -송금 : 출금&입금 데이터생성
         * */

        //송금트랜잭션 진행
        LocalDateTime currentDate = LocalDateTime.now();


        bankService.validateUser(requestDto.getUserFinanceId());
        PersonAccounts senderAccount = bankService.validateSenderAccount(requestDto);
        PersonAccounts receiverAccount = bankService.validateReceiverAccount(requestDto);

        bankService.validateTransferBalance(senderAccount.getCurrentBalance(), requestDto.getTranAmt());

        long senderAfterBalance = bankService.calculateWithdrawBalance(
                senderAccount.getCurrentBalance(),
                requestDto.getTranAmt());
        long receiverAfterBalance = bankService.calculateDepositBalance(
                receiverAccount.getCurrentBalance(),
                requestDto.getTranAmt());

        OrgCode senderBankInfo = bankService.getBankInfo(senderAccount.getBankCode());
        OrgCode receiverBankInfo = bankService.getBankInfo(receiverAccount.getBankCode());

        BankTransaction senderTransactionData = bankService.processSenderTransactionData(
                requestDto,
                senderAccount,
                receiverAccount,
                senderAfterBalance,
                apiTranId,
                currentDate
        );
        BankTransaction receiverTransactionData = bankService.processReceiverTransactionData(
                requestDto,
                senderAccount,
                receiverAccount,
                receiverAfterBalance,
                apiTranId,
                currentDate
        );

        bankService.createTransaction(
                requestDto,
                senderTransactionData,
                receiverTransactionData,
                senderAccount,
                receiverAccount,
                senderAfterBalance,
                receiverAfterBalance
        );

        // Dto 변환
        TransactionResponseDto transactionResponseDto = bankService.processBankTransactionData(
                senderTransactionData,
                receiverTransactionData,
                requestDto,
                senderBankInfo,
                receiverBankInfo,
                senderAccount,
                receiverAccount
        );

        // ----------- 완료 로직
        ApiRequestLog attribute = (ApiRequestLog) request.getAttribute("apiRequestLog");
        String apiTranDate = attribute.getCreatedDate().format(API_FORMATTER);

        responseDto.setApiTranId(apiTranId);
        responseDto.setApiTranDtm(apiTranDate);
        responseDto.setRspCode("A0000");
        responseDto.setRspMessage("송금 거래 성공");
        responseDto.setData(transactionResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * Authorization 헤더에서 API 키 추출
     * @param authorizationHeader 인증 헤더
     * @return 추출된 API 키
     */
    private String extractApiKey(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new BadRequestException("API키 추출 실패");
    }
}
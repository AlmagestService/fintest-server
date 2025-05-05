package com.almagest_dev.fintest_server.service;

import com.almagest_dev.fintest_server.dto.account.AccountInfoDto;
import com.almagest_dev.fintest_server.dto.account.IntegrateAccountRequestDto;
import com.almagest_dev.fintest_server.dto.account.IntegrateAccountResponseDto;
import com.almagest_dev.fintest_server.dto.account.SingleAccountResponseDto;
import com.almagest_dev.fintest_server.dto.recipient.RecipientInquiryRequestDto;
import com.almagest_dev.fintest_server.dto.recipient.RecipientInquiryResponseDto;
import com.almagest_dev.fintest_server.dto.transaction.TransactionDetailDto;
import com.almagest_dev.fintest_server.dto.transaction.TransactionListRequestDto;
import com.almagest_dev.fintest_server.dto.transaction.TransactionListResponseDto;
import com.almagest_dev.fintest_server.dto.withdrawal.TransactionRequestDto;
import com.almagest_dev.fintest_server.dto.withdrawal.TransactionResponseDto;
import com.almagest_dev.fintest_server.entity.*;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.exception.base_exceptions.ValidationException;
import com.almagest_dev.fintest_server.repository.BankTransactionRepository;
import com.almagest_dev.fintest_server.repository.PersonAccountsRepository;
import com.almagest_dev.fintest_server.util.MaskUtil;
import com.almagest_dev.fintest_server.util.TranTypePicker;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.almagest_dev.fintest_server.util.constants.Formatter.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BankService {
    private final BankTransactionRepository bankTransactionRepository;
    private final PersonAccountsRepository personAccountsRepository;
    private final JPAQueryFactory query;



    @Transactional
    public void createTransaction(
            TransactionRequestDto requestDto,
            BankTransaction senderTransactionData,
            BankTransaction receiverTransactionData,
            PersonAccounts senderAccount,
            PersonAccounts receiverAccount,
            long senderAfterBalance,
            long receiverAfterBalance
    ) {
        // 입력값 검증
        if(requestDto.getRecvAccountFintechUseNum() == null || requestDto.getRecvAccountFintechUseNum().equals("") ||
                requestDto.getTranAmt() == null || requestDto.getTranAmt().equals("") ||
                requestDto.getRecvClientAccountNum() == null || requestDto.getRecvClientAccountNum().equals("") ||
                requestDto.getUserFinanceId() == null || requestDto.getUserFinanceId().equals("") ||
                requestDto.getFintechUseNum() == null || requestDto.getFintechUseNum().equals("") ||
                requestDto.getReqClientAccountNum() == null || requestDto.getReqClientAccountNum().equals("") ||
                requestDto.getReqClientBankCode() == null || requestDto.getReqClientBankCode().equals("") ||
                requestDto.getRecvClientBankCode() == null || requestDto.getRecvClientBankCode().equals("")
        ) {
            throw new BadRequestException("거래 필수정보 누락");
        }

        LocalDateTime currentDate = LocalDateTime.now();
        List<BankTransaction> transactions = new ArrayList<>();
        List<PersonAccounts> accounts = new ArrayList<>();

        transactions.add(senderTransactionData);
        transactions.add(receiverTransactionData);

        // 생성된 트랜잭션객체 저장
        bankTransactionRepository.saveAll(transactions);

        // 각 사용자 계좌정보의 현재잔고, 마지막거래일 업데이트

        senderAccount.setCurrentBalance(senderAfterBalance);
        senderAccount.setLastTransactionDate(currentDate);

        receiverAccount.setCurrentBalance(receiverAfterBalance);
        receiverAccount.setLastTransactionDate(currentDate);

        accounts.add(senderAccount);
        accounts.add(receiverAccount);

        // 잔고와 마지막 거래일 변경 후 저장
        personAccountsRepository.saveAll(accounts);
    }


    /*계좌의 거래내역 조회*/
    public List<BankTransaction> getTransactions(TransactionListRequestDto requestDto) {
        if(requestDto == null || requestDto.getFintechUseNum() == null || requestDto.getFintechUseNum().equals("")){
            throw new BadRequestException("요청데이터 누락");
        }
        QBankTransaction qBankTransaction = QBankTransaction.bankTransaction;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String fromTime = "000000";
        if (requestDto.getFromTime() != null && !requestDto.getFromTime().equals("")) {
            fromTime = requestDto.getFromTime();
        }

        LocalDateTime fromDateTime = LocalDateTime.parse(requestDto.getFromDate() + fromTime, formatter);
        LocalDateTime toDateTime = LocalDateTime.parse(requestDto.getToDate() + requestDto.getToTime(), formatter);

        OrderSpecifier<?> sortOrder = new OrderSpecifier<>(
                requestDto.getSortOrder() == null ? Order.DESC : requestDto.getSortOrder().equals("A") ? Order.ASC : Order.DESC,
                qBankTransaction.transactionDate
        );

        long limit = Long.MAX_VALUE; // 기본값: 제한 없음

        if (requestDto.getDataLength() != null && !requestDto.getDataLength().equals("")) {
            try {
                limit = Long.parseLong(requestDto.getDataLength());
            } catch (BadRequestException e) {
                throw new BadRequestException("데이터 요청값 오류");
            }
        }


        List<BankTransaction> bankTransactions = query.selectFrom(qBankTransaction)
                .where(
                        qBankTransaction.fintechUseNum.eq(requestDto.getFintechUseNum())
                                .and(qBankTransaction.transactionDate.between(fromDateTime, toDateTime))
                )
                .orderBy(sortOrder)
                .limit(limit)
                .fetch();


        if (bankTransactions == null) {
            throw new ValidationException("계좌 식별 불가");
        }

        return bankTransactions;
    }

    /*사용자의 모든 계좌 조회*/
    public List<PersonAccounts> getAllAccounts(IntegrateAccountRequestDto requestDto) {
        if(requestDto == null || requestDto.getUserFinanceId() == null || requestDto.getUserFinanceId().length() != 36){
            throw new BadRequestException("요청데이터 누락");
        }

        QPersonAccounts qPersonAccounts = QPersonAccounts.personAccounts;
        List<PersonAccounts> personAccounts = query.selectFrom(qPersonAccounts)
                .where(qPersonAccounts.userFinanceId.eq(requestDto.getUserFinanceId()))
                .fetch();

        if (personAccounts == null) {
            throw new BadRequestException("계좌 조회 실패");
        }

        return personAccounts;
    }

    /*수취인 조회*/
    public PersonAccounts getRecipient(RecipientInquiryRequestDto requestDto) {
        if(requestDto == null || requestDto.getAccountNum() == null || requestDto.getAccountNum().equals("")){
            throw new BadRequestException("요청 데이터 누락");
        }

        QPersonAccounts qPersonAccounts = QPersonAccounts.personAccounts;

        PersonAccounts reqAccount = query.selectFrom(qPersonAccounts)
                .where(qPersonAccounts.fintechUseNum.eq(requestDto.getReqFintechUseNum())
                        .and(qPersonAccounts.userFinanceId.eq(requestDto.getReqUserFinanceId())))
                .fetchOne();
        if(reqAccount == null){
            throw new BadRequestException("송금인 정보 검증 실패");
        }

        PersonAccounts personAccounts = query.selectFrom(qPersonAccounts)
                .where(qPersonAccounts.accountNum.eq(requestDto.getAccountNum()))
                .fetchOne();
        if(personAccounts == null){
            throw new BadRequestException("수취계좌 조회 실패");
        }
        return personAccounts;
    }

    /*계좌상품 조회*/
    public BankAccountProducts getAccountProduct(String productCode) {
        if(productCode == null || productCode.equals("")){
            throw new BadRequestException("계좌상품 조회 실패");
        }
        QBankAccountProducts qBankAccountProducts = QBankAccountProducts.bankAccountProducts;

        BankAccountProducts bankAccountProducts = query.selectFrom(qBankAccountProducts)
                .where(qBankAccountProducts.productCode.eq(productCode))
                .fetchOne();

        if(bankAccountProducts == null){
            throw new BadRequestException("계좌 상품 조회 실패");
        }

        return bankAccountProducts;
    }

    /*상품코드로 계좌상품 조회*/
    public List<BankAccountProducts> getProductsByCode(List<String> productCodes) {
        if(productCodes == null || productCodes.size() == 0 ){
            throw new BadRequestException("상품 코드 누락");
        }
        QBankAccountProducts qBankAccountProducts = QBankAccountProducts.bankAccountProducts;
        List<BankAccountProducts> products = query.selectFrom(qBankAccountProducts)
                .where(qBankAccountProducts.productCode.in(productCodes))
                .fetch();
        if (products == null) {
            throw new BadRequestException("자료 조회 실패");
        }

        return products;
    }

    /*기관코드로 은행/기관 조회*/
    public OrgCode getBankInfo(String bankCode) {
        if(bankCode == null){
            throw new BadRequestException("은행 코드 누락");
        }

        QOrgCode qOrgCode = QOrgCode.orgCode;

        OrgCode orgCode = query.selectFrom(qOrgCode)
                .where(qOrgCode.code.eq(bankCode))
                .fetchOne();
        if(orgCode == null){
            throw new BadRequestException("은행/기관 조회 실패");
        }

        return orgCode;

    }


    /*계좌조회(잔액조회)*/
    public PersonAccounts getAccount(String fintechUseNum, String userFinanceId) {
        if(fintechUseNum == null || fintechUseNum.length() != 36){
            throw new BadRequestException("계좌식별자 누락");
        }
        QPersonAccounts qPersonAccounts = QPersonAccounts.personAccounts;

        PersonAccounts personAccounts = new PersonAccounts();
        try {
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(qPersonAccounts.fintechUseNum.eq(fintechUseNum));
            if (userFinanceId != null) {
                builder.and(qPersonAccounts.userFinanceId.eq(userFinanceId));
            }

             personAccounts = query.selectFrom(qPersonAccounts)
                    .where(qPersonAccounts.fintechUseNum.eq(fintechUseNum)
                            .and(builder))
                    .fetchOne();

        }catch (Exception e){

            e.printStackTrace();
        }

        if(personAccounts == null){
            throw new BadRequestException("계좌 조회 실패");
        }

        return personAccounts;
    }

    /*수취인조회 Dto생성*/
    public RecipientInquiryResponseDto processRecipientData(
            PersonAccounts recipient,
            OrgCode bankInfo,
            OrgCode wBankInfo,
            RecipientInquiryRequestDto recipientInquiryRequestDto
    ) {
        if(recipient == null || bankInfo == null || wBankInfo == null || recipientInquiryRequestDto == null){
            throw new BadRequestException("수취인 계좌 정보 누락");
        }
        RecipientInquiryResponseDto responseDto = new RecipientInquiryResponseDto();

        responseDto.setBankCodeStd(String.valueOf(bankInfo.getCode())); // deposit
        responseDto.setBankName(bankInfo.getName());
        responseDto.setAccountNum(recipient.getAccountNum());
        responseDto.setAccountNumMasked(MaskUtil.maskAccountNumber(recipient.getAccountNum()));
        responseDto.setAccountHolderName(recipient.getAccountHolder());
        responseDto.setRecvAccountFintechUseNum(recipient.getFintechUseNum());
        responseDto.setWdBankCodeStd(recipientInquiryRequestDto.getBankCodeStd());
        responseDto.setWdBankName(wBankInfo.getName());
        responseDto.setWdAccountNum(recipientInquiryRequestDto.getAccountNum());
        responseDto.setTranAmt(recipientInquiryRequestDto.getTranAmt());
        return responseDto;
    }

    /*거래내역조회 Dto생성*/
    public TransactionListResponseDto processTransferListData(
            List<BankTransaction> transactions,
            PersonAccounts account,
            OrgCode bankInfo,
            TransactionListRequestDto requestDto
    ){
        if(transactions == null || transactions.isEmpty() || bankInfo == null || requestDto == null || account == null){
            throw new BadRequestException("거래내역 데이터 누락");
        }
        TransactionListResponseDto transactionListResponseDto = new TransactionListResponseDto();
        List<TransactionDetailDto> resList = new ArrayList<>();


        for (BankTransaction transaction : transactions) {
            TransactionDetailDto dto = new TransactionDetailDto();
            dto.setTranNum(String.valueOf(transaction.getId()));
            dto.setTranDate(transaction.getTransactionDate().format(DATE_FORMATTER));
            dto.setTranTime(transaction.getTransactionDate().format(TIME_FORMATTER));
            dto.setInoutType(TranTypePicker.getTranType(transaction.getTranTypeCode()));
            dto.setTranType("현금");
            dto.setPrintContent(transaction.getDescription());
            dto.setTranAmt(String.valueOf(transaction.getAmount()));
            dto.setAfterBalanceAmt(String.valueOf(transaction.getAfterBalance()));

            resList.add(dto);
        }

        transactionListResponseDto.setBankCodeTran(String.valueOf(bankInfo.getCode()));
        transactionListResponseDto.setBankName(bankInfo.getName());
        transactionListResponseDto.setFintechUseNum(requestDto.getFintechUseNum());
        transactionListResponseDto.setBalanceAmt(String.valueOf(account.getCurrentBalance()));
        transactionListResponseDto.setResList(resList);

        return transactionListResponseDto;
    }

    /*계좌정보조회 Dto생성*/
    public SingleAccountResponseDto processSingleAccountData(
            PersonAccounts account,
            OrgCode bankInfo,
            BankAccountProducts accountProduct
    ){
        if(account == null || bankInfo == null || accountProduct == null){
            throw new BadRequestException("계좌정보 데이터 누락");
        }
        SingleAccountResponseDto singleAccountResponseDto = new SingleAccountResponseDto();
        AccountInfoDto accountInfoDto = new AccountInfoDto();

        accountInfoDto.setBankCodeStd(account.getBankCode());
        accountInfoDto.setFintechUseNum(account.getFintechUseNum());
        accountInfoDto.setAccountHolder(account.getAccountHolder());
        accountInfoDto.setActivityType("A");
        accountInfoDto.setAccountType("1");
        accountInfoDto.setAccountNum(account.getAccountNum());
        accountInfoDto.setAccountSeq("01");
        accountInfoDto.setAccountIssueDate(String.valueOf(account.getAccountIssueDate()));
        accountInfoDto.setMaturityDate(accountInfoDto.getMaturityDate());
        accountInfoDto.setLastTranDate(String.valueOf(account.getLastTransactionDate()));
        accountInfoDto.setProductName(accountInfoDto.getProductName());
        accountInfoDto.setDormancyYn("N");
        accountInfoDto.setBalanceAmt(String.valueOf(account.getCurrentBalance()));

        singleAccountResponseDto.setBankCodeTran(String.valueOf(bankInfo.getCode()));
        singleAccountResponseDto.setBankName(bankInfo.getName());
        singleAccountResponseDto.setFintechUseNum(account.getFintechUseNum());
        singleAccountResponseDto.setBalanceAmt(accountInfoDto.getBalanceAmt());
        singleAccountResponseDto.setAvailableAmt(accountInfoDto.getBalanceAmt());
        singleAccountResponseDto.setAccountType("1");
        singleAccountResponseDto.setProductName(accountProduct.getProductName());
        singleAccountResponseDto.setAccountIssueDate(String.valueOf(account.getAccountIssueDate()));
        singleAccountResponseDto.setAccountInfo(accountInfoDto);

        return singleAccountResponseDto;
    }

    /*통합계좌조회 Dto생성*/
    public IntegrateAccountResponseDto processIntegrateAccountData(
            List<BankAccountProducts> productsByCode,
            List<PersonAccounts> allAccounts,
            String userFinanceId,
            HttpServletRequest request
    ){
        if(productsByCode.isEmpty() || allAccounts.isEmpty() || userFinanceId == null || userFinanceId.length() != 36){
            throw new BadRequestException("통합계좌조회 데이터 누락");
        }
        IntegrateAccountResponseDto integrateAccountResponseDto = new IntegrateAccountResponseDto();
        List<AccountInfoDto> accountInfoDto = new ArrayList<>();

        ApiRequestLog attribute = (ApiRequestLog) request.getAttribute("apiRequestLog");

        for (PersonAccounts personAccounts : allAccounts) {
            AccountInfoDto dto = new AccountInfoDto();
            for (BankAccountProducts bankAccountProducts : productsByCode) {
                if (personAccounts.getProductCode().equals(bankAccountProducts.getProductCode())) {
                    dto.setBankCodeStd(personAccounts.getBankCode());
                    dto.setFintechUseNum(personAccounts.getFintechUseNum());
                    dto.setAccountHolder(personAccounts.getAccountHolder());
                    dto.setActivityType("A");
                    dto.setAccountType("1");
                    dto.setAccountNum(personAccounts.getAccountNum());
                    dto.setAccountSeq("01");
                    dto.setAccountIssueDate(personAccounts.getAccountIssueDate().format(DATE_FORMATTER));
                    dto.setMaturityDate(null);
                    dto.setLastTranDate(personAccounts.getLastTransactionDate().format(DATE_FORMATTER));
                    dto.setProductName(bankAccountProducts.getProductName());
                    dto.setProductSubName(null);
                    dto.setDormancyYn("N");
                    dto.setBalanceAmt(String.valueOf(personAccounts.getCurrentBalance()));
                }
            }
            accountInfoDto.add(dto);
        }

        integrateAccountResponseDto.setUserFinanceId(userFinanceId);
        integrateAccountResponseDto.setAinfoTranDate(attribute.getCreatedDate().format(DATE_FORMATTER));
        integrateAccountResponseDto.setRspType("0");
        integrateAccountResponseDto.setInquiryBankType("1");
        integrateAccountResponseDto.setResList(accountInfoDto);

        return integrateAccountResponseDto;
    }

    /*송금 정보 생성*/
    public BankTransaction processSenderTransactionData(
            TransactionRequestDto requestDto,
            PersonAccounts senderAccount,
            PersonAccounts receiverAccount,
            Long senderAfterBalance,
            String apiTranId,
            LocalDateTime currentDate
    ){
        if(requestDto == null || senderAccount == null
                || receiverAccount == null || senderAfterBalance == null
                || apiTranId == null || currentDate == null){
            throw new BadRequestException("송금 정보 누락");
        }
        BankTransaction senderTransaction = new BankTransaction();

        senderTransaction.setPersonAccounts(senderAccount);

        senderTransaction.setAmount(Long.valueOf(requestDto.getTranAmt()));
        senderTransaction.setPersonAccounts(senderAccount);
        senderTransaction.setTranTypeCode("33");
        senderTransaction.setApiTranId(apiTranId);
        senderTransaction.setTransactionDate(currentDate);
        senderTransaction.setDescription(requestDto.getWdPrintContent());
        senderTransaction.setFintechUseNum(senderAccount.getFintechUseNum());
        senderTransaction.setWithdrawalOrgCode(Integer.valueOf(senderAccount.getBankCode()));
        senderTransaction.setWithdrawalAccountNumber(senderAccount.getAccountNum());
        senderTransaction.setDepositOrgCode(Integer.valueOf(receiverAccount.getBankCode()));
        senderTransaction.setDepositAccountNumber(receiverAccount.getAccountNum());
        senderTransaction.setBeforeBalance(senderAccount.getCurrentBalance());
        senderTransaction.setAfterBalance(senderAfterBalance);
        senderTransaction.setStatus("S");
        senderTransaction.setRspCode("A0000");


        return senderTransaction;
    }

    /*수취 정보 생성*/
    public BankTransaction processReceiverTransactionData(
            TransactionRequestDto requestDto,
            PersonAccounts senderAccount,
            PersonAccounts receiverAccount,
            Long receiverAfterBalance,
            String apiTranId,
            LocalDateTime currentDate
    ){
        if(requestDto == null || senderAccount == null
                || receiverAccount == null || receiverAfterBalance == null
                || apiTranId == null || currentDate == null){
            throw new BadRequestException("수취 정보 누락");
        }
        BankTransaction receiverTransaction = new BankTransaction();

        receiverTransaction.setPersonAccounts(receiverAccount);

        receiverTransaction.setAmount(Long.valueOf(requestDto.getTranAmt()));
        receiverTransaction.setPersonAccounts(senderAccount);
        receiverTransaction.setTranTypeCode("44");
        receiverTransaction.setApiTranId(apiTranId);
        receiverTransaction.setTransactionDate(currentDate);
        receiverTransaction.setDescription(requestDto.getDpsPrintContent());
        receiverTransaction.setFintechUseNum(receiverAccount.getFintechUseNum());
        receiverTransaction.setWithdrawalOrgCode(Integer.valueOf(senderAccount.getBankCode()));
        receiverTransaction.setWithdrawalAccountNumber(senderAccount.getAccountNum());
        receiverTransaction.setDepositOrgCode(Integer.valueOf(receiverAccount.getBankCode()));
        receiverTransaction.setDepositAccountNumber(receiverAccount.getAccountNum());
        receiverTransaction.setBeforeBalance(receiverAccount.getCurrentBalance());
        receiverTransaction.setAfterBalance(receiverAfterBalance);
        receiverTransaction.setStatus("S");
        receiverTransaction.setRspCode("A0000");


        return receiverTransaction;
    }

    /*은행거래 Dto생성*/
    public TransactionResponseDto processBankTransactionData(
            BankTransaction senderTransactionData,
            BankTransaction receiverTransactionData,
            TransactionRequestDto requestDto,
            OrgCode sendBankInfo,
            OrgCode recvBankInfo,
            PersonAccounts senderAccount,
            PersonAccounts receiverAccount
    ){
        if(senderTransactionData == null || receiverTransactionData == null
                ||requestDto == null || senderAccount == null
                || receiverAccount == null || sendBankInfo == null
                || recvBankInfo == null){
            throw new BadRequestException("거래 정보 누락");
        }
        TransactionResponseDto transactionResponseDto = new TransactionResponseDto();

        transactionResponseDto.setTranAmt(requestDto.getTranAmt());
        transactionResponseDto.setWdLimitRemainAmt(String.valueOf(senderTransactionData.getAfterBalance()));
        transactionResponseDto.setTranResult("S");

        transactionResponseDto.setFintechUseNum(senderTransactionData.getFintechUseNum());
        transactionResponseDto.setAccountAlias(null);
        transactionResponseDto.setBankCodeStd(senderAccount.getBankCode());
        transactionResponseDto.setBankName(sendBankInfo.getName());
        transactionResponseDto.setAccountNumMasked(MaskUtil.maskAccountNumber(senderTransactionData.getWithdrawalAccountNumber()));
        transactionResponseDto.setPrintContent(requestDto.getWdPrintContent());
        transactionResponseDto.setAccountHolderName(senderAccount.getAccountHolder());

        transactionResponseDto.setDpsAccountHolderName(receiverAccount.getAccountHolder());
        transactionResponseDto.setDpsBankCodeStd(receiverAccount.getBankCode());
        transactionResponseDto.setDpsBankName(recvBankInfo.getName());
        transactionResponseDto.setDpsAccountNumMasked(receiverTransactionData.getDepositAccountNumber());
        transactionResponseDto.setDpsPrintContent(requestDto.getDpsPrintContent());


        return transactionResponseDto;
    }

    /*사용자 식별자 검증*/
    public Person validateUser(String userFinanceId) {
        if(userFinanceId == null || userFinanceId.length() != 36){
            throw new BadRequestException("사용자 식별자 누락");
        }
        QPerson qPerson = QPerson.person;
        Person person = query.selectFrom(qPerson)
                .where(qPerson.userFinanceId.eq(userFinanceId))
                .fetchOne();
        if (person == null) {
            throw new BadRequestException("사용자 식별 불가");
        }
        return person;
    }

    /*출금 계좌 검증*/
    public PersonAccounts validateSenderAccount(TransactionRequestDto requestDto) {
        if(requestDto == null){
            throw new BadRequestException("출금 계좌 정보 누락");
        }
        QPersonAccounts qPersonAccounts = QPersonAccounts.personAccounts;
        PersonAccounts senderAccount = query.selectFrom(qPersonAccounts)
                .where(qPersonAccounts.userFinanceId.eq(requestDto.getUserFinanceId())
                        .and(qPersonAccounts.fintechUseNum.eq(requestDto.getFintechUseNum()))
                        .and(qPersonAccounts.accountNum.eq(requestDto.getReqClientAccountNum()))
                        .and(qPersonAccounts.bankCode.eq(requestDto.getReqClientBankCode()))
                        .and(qPersonAccounts.verified.eq("Y")))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) // 비관적 락 설정
                .fetchOne();

        if(senderAccount == null){
            throw new BadRequestException("송금계좌 검증 실패");
        }

        // 현재잔고와 이체잔고 검증
        if (senderAccount.getCurrentBalance() < Long.parseLong(requestDto.getTranAmt())) {
            throw new BadRequestException("잔고 부족");
        }
        return senderAccount;
    }

    /*수취 계좌 검증*/
    public PersonAccounts validateReceiverAccount(TransactionRequestDto requestDto) {
        if(
                requestDto.getRecvAccountFintechUseNum() == null || requestDto.getRecvAccountFintechUseNum().length() != 36
                && requestDto.getRecvClientAccountNum() == null || requestDto.getRecvClientAccountNum().equals("")
                && requestDto.getReqClientBankCode() == null || requestDto.getReqClientBankCode().equals("")
        ){
            throw new BadRequestException("수취계좌 정보 누락");
        }
        QPersonAccounts qPersonAccounts = QPersonAccounts.personAccounts;
        PersonAccounts receiverAccount = query.selectFrom(qPersonAccounts)
                .where(qPersonAccounts.fintechUseNum.eq(requestDto.getRecvAccountFintechUseNum())
                        .and(qPersonAccounts.accountNum.eq(requestDto.getRecvClientAccountNum()))
                        .and(qPersonAccounts.bankCode.eq(requestDto.getRecvClientBankCode())))
                .fetchOne();

        if (receiverAccount == null) {
            throw new BadRequestException("수취계좌 식별 불가");
        }
        return receiverAccount;
    }

    /*잔고 검증*/
    public void validateTransferBalance(Long currentBalance, String withdrawAmount){
        if(currentBalance == null || withdrawAmount == null){
            throw new BadRequestException("금액 정보 누락");
        }
        if (currentBalance < Long.parseLong(withdrawAmount)) {
            throw new BadRequestException("잔고 부족");
        }
    }

    /*출금 잔고 계산*/
    public long calculateWithdrawBalance(Long currentBalance, String withdrawAmount){
        if(currentBalance == null || withdrawAmount == null){
            throw new BadRequestException("금액 정보 누락");
        }
        long result = currentBalance - Long.parseLong(withdrawAmount);
        if(result < 0){
            throw new BadRequestException("잔고 부족");
        }
        return result;
    }

    /*입금 잔고 계산*/
    public long calculateDepositBalance(Long currentBalance, String depositAmount){
        if(currentBalance == null || depositAmount == null){
            throw new BadRequestException("금액 정보 누락");
        }
        return currentBalance + Long.parseLong(depositAmount);
    }

    /* UUID 형식 검증 */
    public void validateUUID(String value, String fieldName) {
        if (value == null || value.length() != 36) {
            throw new BadRequestException("식별자 형식이 잘못되었습니다.");
        }
    }

}

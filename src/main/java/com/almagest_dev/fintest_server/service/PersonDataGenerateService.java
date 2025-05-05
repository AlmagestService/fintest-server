package com.almagest_dev.fintest_server.service;

import com.almagest_dev.fintest_server.entity.BankAccountProducts;
import com.almagest_dev.fintest_server.entity.BankTransaction;
import com.almagest_dev.fintest_server.entity.OrgCode;
import com.almagest_dev.fintest_server.entity.PersonAccounts;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.repository.BankAccountProductsRepository;
import com.almagest_dev.fintest_server.repository.OrgCodeRepository;
import com.almagest_dev.fintest_server.repository.PersonAccountsRepository;
import com.almagest_dev.fintest_server.repository.BankTransactionRepository;
import com.almagest_dev.fintest_server.util.generator.BankAccountProductGenerator;
import com.almagest_dev.fintest_server.util.generator.PersonAccountGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// 테스트베드용 사용자/계좌/거래 데이터 랜덤 생성 및 관리 서비스 클래스
@Service
@RequiredArgsConstructor
public class PersonDataGenerateService {

    private final Random random = new Random();
    private final PersonAccountsRepository personAccountsRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final BankAccountProductsRepository bankAccountProductsRepository;
    private final PersonAccountGenerator personAccountGenerator;
    private final OrgCodeRepository orgCodeRepository;

    /**
     * 랜덤 은행계좌 상품 데이터 생성
     */
    public void createBankAccountProductData() {
        List<BankAccountProducts> bankAccountProducts = BankAccountProductGenerator.generateRandomBankProducts();
        bankAccountProductsRepository.saveAll(bankAccountProducts);
    }

    /**
     * 랜덤 사용자 소유 계좌 생성
     * @param userFinanceId 사용자 금융식별자
     * @param userName 사용자 이름
     * @return 생성된 계좌 리스트
     */
    public List<PersonAccounts> createPersonAccountData(String userFinanceId, String userName) {
        if(userFinanceId == null || userFinanceId.equals("")){
            throw new BadRequestException("계좌식별번호 누락");
        }
        if(userName == null || userName.equals("")){
            throw new BadRequestException("사용자 이름 누락");
        }

        //모든상품추출
        List<BankAccountProducts> allProducts = bankAccountProductsRepository.findAll();
        List<OrgCode> orgInfo = orgCodeRepository.findAll();

        //사용자계좌 3~10개 랜덤생성
        List<PersonAccounts> personAccounts = personAccountGenerator.createRandomAccountsForUser(userFinanceId, allProducts, orgInfo, userName);

        //생성된 데이터 저장
        return personAccountsRepository.saveAll(personAccounts);
    }

    /**
     * 랜덤 거래목록 생성
     * @param userFinanceId 사용자 금융식별자
     */
    public void generateTransactions(String userFinanceId) {
        if(userFinanceId == null || userFinanceId.equals("")){
            throw new BadRequestException("계좌식별번호 누락");
        }

        // 사용자의 모든 계좌 불러오기
        List<PersonAccounts> userAccounts = personAccountsRepository.findByUserFinanceId(userFinanceId);

        if (userAccounts.isEmpty()) {
            throw new BadRequestException("계좌 없음");
        }

        // 계좌 연동용 해시맵
        Map<String, Long> accountBalances = new HashMap<>();
        for (PersonAccounts account : userAccounts) {
            accountBalances.put(account.getFintechUseNum(), account.getCurrentBalance());
        }

        // 거래 생성 시작일을 현재 날짜 기준으로 두 달 전으로 설정
        LocalDateTime currentDate = LocalDateTime.now().minusDays(60); // 60일 전부터 시작

        // 50-100 랜덤값으로 거래 목록 생성 수 설정
        int transactionCount = random.nextInt(51) + 50;

        // 지정된 수만큼 반복하며 거래 생성
        for (int i = 0; i < transactionCount; i++) {
            int transactionType = random.nextInt(3); // 0: deposit, 1: withdrawal, 2: transfer
            PersonAccounts account = userAccounts.get(random.nextInt(userAccounts.size()));
            Long currentBalance = accountBalances.get(account.getFintechUseNum());

            if (transactionType == 2) { // Transfer
                PersonAccounts receiverAccount;
                do {
                    receiverAccount = userAccounts.get(random.nextInt(userAccounts.size()));
                } while (receiverAccount.getFintechUseNum().equals(account.getFintechUseNum()));

                Long maxAmount = currentBalance;
                Long amount = generateAmountWithinBalance(maxAmount);
                String apiTranId = UUID.randomUUID().toString();
                createTransferTransaction(account, receiverAccount, amount, currentDate, apiTranId, accountBalances);
            } else { // Deposit or Withdrawal
                Long maxAmount = transactionType == 0 ? Long.MAX_VALUE : currentBalance;
                Long amount = generateAmountWithinBalance(maxAmount);
                createDepositOrWithdrawalTransaction(account, transactionType == 0 ? "11" : "22",
                        transactionType == 0 ? "입금" : "출금", amount, currentDate, UUID.randomUUID().toString(), accountBalances);
            }

            // 각 거래 후 시간을 현재로 향해 조금씩 앞으로 이동
            currentDate = currentDate.plusDays(random.nextInt(2))   // 0~1일 증가
                    .plusHours(random.nextInt(24))  // 0~23시간 증가
                    .plusMinutes(random.nextInt(60)); // 0~59분 증가

            // 현재 시간을 넘지 않도록 제한
            if (currentDate.isAfter(LocalDateTime.now())) {
                currentDate = LocalDateTime.now();
            }
        }

        // 최종 거래 내역의 잔고를 PersonAccounts의 currentBalance로 업데이트
        for (PersonAccounts account : userAccounts) {
            Long finalBalance = accountBalances.get(account.getFintechUseNum());
            account.setCurrentBalance(finalBalance);
            personAccountsRepository.save(account); // 최종 잔고 업데이트
        }
    }

    /**
     * 실제 잔고 기반 거래금액 생성
     * @param maxAmount 최대 거래 가능 금액
     * @return 생성된 거래 금액
     */
    private Long generateAmountWithinBalance(Long maxAmount) {
        Long upperLimit = Math.min(maxAmount, 500_000L);
        return (upperLimit > 1) ? ThreadLocalRandom.current().nextLong(1, upperLimit + 1) : 0L;
    }

    /**
     * 입금 또는 출금 거래 생성
     * @param account 계좌 엔티티
     * @param tranTypeCode 거래 타입 코드
     * @param description 거래 설명
     * @param amount 거래 금액
     * @param transactionDate 거래 일시
     * @param apiTranId 트랜잭션 ID
     * @param accountBalances 계좌별 잔고 맵
     */
    private void createDepositOrWithdrawalTransaction(PersonAccounts account, String tranTypeCode,
                                                      String description, Long amount, LocalDateTime transactionDate,
                                                      String apiTranId, Map<String, Long> accountBalances) {

        Long currentBalance = accountBalances.get(account.getFintechUseNum());
        Long afterBalance = "11".equals(tranTypeCode) ? currentBalance + amount : currentBalance - amount;

        BankTransaction transaction = new BankTransaction();
        transaction.setAmount(amount);
        transaction.setTranTypeCode(tranTypeCode);
        transaction.setTransactionDate(transactionDate);
        transaction.setDescription(description);
        transaction.setApiTranId(apiTranId);

        transaction.setFintechUseNum(account.getFintechUseNum());

        // 입금과 출금에 따른 계좌 설정
        if ("22".equals(tranTypeCode)) {
            transaction.setWithdrawalOrgCode(Integer.parseInt(account.getBankCode()));
            transaction.setWithdrawalAccountNumber(account.getAccountNum());
        } else if ("11".equals(tranTypeCode)) {
            transaction.setDepositOrgCode(Integer.parseInt(account.getBankCode()));
            transaction.setDepositAccountNumber(account.getAccountNum());
        }

        transaction.setBeforeBalance(currentBalance);
        transaction.setAfterBalance(afterBalance);

        transaction.setStatus("S");
        transaction.setRspCode("A0000");

        //계좌 연결
        transaction.setPersonAccounts(account);

        bankTransactionRepository.save(transaction);

        //다음 생성을위한 잔고 업데이트
        accountBalances.put(account.getFintechUseNum(), afterBalance);


        //마지막 거래일자 입력
        account.setLastTransactionDate(transactionDate);
        personAccountsRepository.save(account);
    }

    /**
     * 송금 거래 생성
     * @param senderAccount 송금인 계좌
     * @param receiverAccount 수취인 계좌
     * @param amount 거래 금액
     * @param transactionDate 거래 일시
     * @param apiTranId 트랜잭션 ID
     * @param accountBalances 계좌별 잔고 맵
     */
    private void createTransferTransaction(PersonAccounts senderAccount, PersonAccounts receiverAccount,
                                           Long amount, LocalDateTime transactionDate, String apiTranId,
                                           Map<String, Long> accountBalances) {

        Long senderCurrentBalance = accountBalances.get(senderAccount.getFintechUseNum());
        Long senderAfterBalance = senderCurrentBalance - amount;

        // 송금 출금 거래 생성 (보내는 쪽 정보 포함)
        BankTransaction senderTransaction = new BankTransaction();
        senderTransaction.setAmount(amount);
        senderTransaction.setTranTypeCode("33"); // 송금 출금 코드
        senderTransaction.setTransactionDate(transactionDate);
        senderTransaction.setDescription("송금 출금");
        senderTransaction.setApiTranId(apiTranId);
        senderTransaction.setFintechUseNum(senderAccount.getFintechUseNum());
        senderTransaction.setWithdrawalOrgCode(Integer.parseInt(senderAccount.getBankCode()));
        senderTransaction.setWithdrawalAccountNumber(senderAccount.getAccountNum());
        senderTransaction.setDepositOrgCode(Integer.parseInt(receiverAccount.getBankCode())); // 받는 쪽 정보 포함
        senderTransaction.setDepositAccountNumber(receiverAccount.getAccountNum()); // 받는 쪽 계좌 번호 포함
        senderTransaction.setBeforeBalance(senderCurrentBalance);
        senderTransaction.setAfterBalance(senderAfterBalance);
        senderTransaction.setStatus("S");
        senderTransaction.setRspCode("A0000");
        //송금계좌 연결
        senderTransaction.setPersonAccounts(senderAccount);

        bankTransactionRepository.save(senderTransaction);

        accountBalances.put(senderAccount.getFintechUseNum(), senderAfterBalance);

        Long receiverCurrentBalance = accountBalances.get(receiverAccount.getFintechUseNum());
        Long receiverAfterBalance = receiverCurrentBalance + amount;

        // 송금 입금 거래 생성 (받는 쪽 정보 포함)
        BankTransaction receiverTransaction = new BankTransaction();
        receiverTransaction.setAmount(amount);
        receiverTransaction.setTranTypeCode("44"); // 송금 입금 코드
        receiverTransaction.setTransactionDate(transactionDate);
        receiverTransaction.setDescription("송금 입금");
        receiverTransaction.setApiTranId(apiTranId);
        receiverTransaction.setFintechUseNum(receiverAccount.getFintechUseNum());
        receiverTransaction.setDepositOrgCode(Integer.parseInt(receiverAccount.getBankCode()));
        receiverTransaction.setDepositAccountNumber(receiverAccount.getAccountNum());
        receiverTransaction.setWithdrawalOrgCode(Integer.parseInt(senderAccount.getBankCode())); // 보내는 쪽 정보 포함
        receiverTransaction.setWithdrawalAccountNumber(senderAccount.getAccountNum()); // 보내는 쪽 계좌 번호 포함
        receiverTransaction.setBeforeBalance(receiverCurrentBalance);
        receiverTransaction.setAfterBalance(receiverAfterBalance);
        receiverTransaction.setStatus("S");
        receiverTransaction.setRspCode("A0000");

        //수취계좌 연결
        receiverTransaction.setPersonAccounts(receiverAccount);

        bankTransactionRepository.save(receiverTransaction);

        accountBalances.put(receiverAccount.getFintechUseNum(), receiverAfterBalance);

        //마지막 거래일자 입력 후 저장
        //마지막 거래일자 입력
        senderAccount.setLastTransactionDate(transactionDate);
        receiverAccount.setLastTransactionDate(transactionDate);
        List<PersonAccounts> batch = new ArrayList<>();
        batch.add(senderAccount);
        batch.add(receiverAccount);
        personAccountsRepository.saveAll(batch);
    }
}

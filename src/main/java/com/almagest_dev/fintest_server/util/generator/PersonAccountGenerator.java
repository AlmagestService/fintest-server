package com.almagest_dev.fintest_server.util.generator;

import com.almagest_dev.fintest_server.entity.BankAccountProducts;
import com.almagest_dev.fintest_server.entity.OrgCode;
import com.almagest_dev.fintest_server.entity.PersonAccounts;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

// 사용자별 랜덤 계좌 데이터 생성을 위한 유틸리티 클래스
@Component
public class PersonAccountGenerator {
    private final Random random = new Random();

    /**
     * 사용자별 랜덤 계좌 리스트 생성
     * @param userFinanceId 사용자 금융식별자
     * @param allProducts 전체 계좌상품 리스트
     * @param orgInfo 기관 정보 리스트
     * @param userName 사용자 이름
     * @return 생성된 계좌 리스트
     */
    public List<PersonAccounts> createRandomAccountsForUser(String userFinanceId, List<BankAccountProducts> allProducts, List<OrgCode> orgInfo, String userName){

        List<PersonAccounts> userAccounts = new ArrayList<>();

        // 생성할 계좌 개수 3~10개 중 랜덤 선택
        int accountCount = random.nextInt(8) + 3;

        // 계좌 상품 중 랜덤으로 3~10 선택
        List<BankAccountProducts> selectedProducts = getRandomProducts(accountCount, allProducts);

        // 한 번 생성된 랜덤 이름 저장
//        String accountHolderName = generateRandomKoreanName();

        //전달받은 이름으로 계좌생성
        String accountHolderName = userName;



        // 선택된 상품 기반으로 사용자 데이터 생성
        for (BankAccountProducts product : selectedProducts) {
            PersonAccounts account = new PersonAccounts();
            account.setUserFinanceId(userFinanceId);
            account.setFintechUseNum(UUID.randomUUID().toString());

            Long accountLength = 0L;

            // 선택된 은행의 계좌번호길이에 맞게 생성 10~14
            for (OrgCode orgCode : orgInfo) {
                if(product.getOrgCode().equals(orgCode.getCode().toString())){
                    accountLength = orgCode.getAccountLength();
                }
            }

            account.setAccountNum(String.format("%" + accountLength + "d", Math.abs(random.nextLong()) % (long) Math.pow(10, accountLength)).trim());


            // 모든 계좌에 동일한 소유주 이름 설정
            account.setAccountHolder(accountHolderName);

            // 상품의 은행 코드 설정
            account.setBankCode(product.getOrgCode());

            // 생성일 설정 (6개월~1년 전)
            account.setAccountIssueDate(generateRandomCreationDate());

            // 98% 확률로 인증된 계좌 설정
            account.setVerified(random.nextInt(100) < 98 ? "Y" : "N");

            // 0 ~ 10,000,000 사이의 랜덤 잔고 설정
            account.setCurrentBalance((long) random.nextInt(10_000_000));

            // b선택된 상품의 product_code 설정
            account.setProductCode(product.getProductCode());

            //마지막 거래일자 입력(생성시 임의로 두달전)
            account.setLastTransactionDate(LocalDateTime.now().minus(2, ChronoUnit.MONTHS));

            userAccounts.add(account);
        }
        return userAccounts;
    }

    /**
     * 계좌상품 리스트에서 랜덤하게 N개 추출
     * @param count 추출 개수
     * @param products 전체 상품 리스트
     * @return 랜덤 추출된 상품 리스트
     */
    private List<BankAccountProducts> getRandomProducts(int count, List<BankAccountProducts> products) {

        Set<Integer> indices = new HashSet<>();

        while (indices.size() < count) {
            indices.add(random.nextInt(products.size()));
        }

        List<BankAccountProducts> selectedProducts = new ArrayList<>();
        for (int index : indices) {
            selectedProducts.add(products.get(index));
        }

        return selectedProducts;
    }

//    private String generateRandomKoreanName() {
//        String[] firstNames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "안", "진", "엄", "한", "주"};
//        String[] lastNames = {"민준", "서연", "지후", "서현", "수빈", "예은", "지민", "지원", "현우", "민규", "하나", "우리", "연아",
//                "혜련", "민정", "영지", "세연", "범수", "재근", "성철", "효정", "혜원", "지윤", "선호", "건희", "태오", "수진"};
//        return firstNames[random.nextInt(firstNames.length)] + lastNames[random.nextInt(lastNames.length)];
//    }

    /**
     * 랜덤 계좌 생성일(6개월~1년 전) 반환
     * @return 생성일(LocalDateTime)
     */
    private LocalDateTime generateRandomCreationDate() {
        long minDays = 180;  // 6 months
        long maxDays = 365;  // 1 year
        long randomDays = minDays + (long) (random.nextDouble() * (maxDays - minDays));
        return LocalDateTime.now().minus(randomDays, ChronoUnit.DAYS);
    }


}

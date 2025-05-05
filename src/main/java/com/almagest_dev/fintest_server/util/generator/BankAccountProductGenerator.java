package com.almagest_dev.fintest_server.util.generator;

import com.almagest_dev.fintest_server.entity.BankAccountProducts;

import java.math.BigDecimal;
import java.util.*;

// 랜덤 은행 계좌상품 데이터 생성을 위한 유틸리티 클래스
public class BankAccountProductGenerator {

    private static final String[] ORG_CODES = {
            "101", "102", "103", "104", "105", "106", "107", "108", "109",
            "110", "111", "112", "113", "114", "115", "116", "117", "118", "119"
    };

    private static final String[] PRODUCT_TYPES = {
            "스마트 계좌", "프리미엄 계좌", "저축 계좌", "VIP 계좌",
            "모바일 전용 계좌", "청년 저축 계좌", "기본 플랜 계좌", "패밀리 계좌",
            "연금 저축 계좌", "고액 저축 계좌", "자유 적립 계좌", "단기 투자 계좌"
    };

    /**
     * 랜덤 은행 계좌상품 리스트 생성
     * @return 생성된 계좌상품 리스트
     */
    public static List<BankAccountProducts> generateRandomBankProducts() {
        List<BankAccountProducts> products = new ArrayList<>();
        Random random = new Random();

        Map<String, Set<String>> usedProductTypesByOrg = new HashMap<>();

        int productCount = random.nextInt(8) + 3; // 3~10개 사이의 상품 수
        for (int i = 0; i < productCount; i++) {
            String orgCode = getRandomOrgCode(random);

            String productName = getUniqueProductTypeForOrg(orgCode, random, usedProductTypesByOrg);
            String description = generateProductDescription(productName);
            BigDecimal interestRate = generateInterestRate(productName, random); // 계좌 타입에 맞는 이자율 생성

            products.add(new BankAccountProducts(orgCode, productName, description, interestRate));
        }
        return products;
    }

    /**
     * 랜덤 은행 코드 반환
     * @param random Random 객체
     * @return 은행 코드 문자열
     */
    private static String getRandomOrgCode(Random random) {
        return ORG_CODES[random.nextInt(ORG_CODES.length)];
    }

    /**
     * 기관별 중복 없는 상품 타입 반환
     * @param orgCode 기관 코드
     * @param random Random 객체
     * @param usedProductTypesByOrg 기관별 사용된 상품 타입 맵
     * @return 상품 타입 문자열
     */
    private static String getUniqueProductTypeForOrg(String orgCode, Random random, Map<String, Set<String>> usedProductTypesByOrg) {
        Set<String> usedProductTypes = usedProductTypesByOrg.computeIfAbsent(orgCode, k -> new HashSet<>());

        String productType;
        do {
            productType = PRODUCT_TYPES[random.nextInt(PRODUCT_TYPES.length)];
        } while (usedProductTypes.contains(productType));

        usedProductTypes.add(productType);
        return productType;
    }

    /**
     * 상품명에 따른 상품 설명 반환
     * @param productName 상품명
     * @return 상품 설명
     */
    private static String generateProductDescription(String productName) {
        switch (productName) {
            case "스마트 계좌":
                return "고객 맞춤형 서비스와 혜택 제공, 높은 이자율 보장";
            case "프리미엄 계좌":
                return "안정적인 저축 이자 제공, 예금자 보호 프로그램 적용";
            case "저축 계좌":
                return "일반 저축 계좌로, 안정적인 금리 제공";
            case "VIP 계좌":
                return "VIP 고객 전용 맞춤형 혜택과 전담 상담 서비스 제공";
            case "모바일 전용 계좌":
                return "모바일 전용 계좌로 간편한 접근성과 관리 기능 제공";
            case "청년 저축 계좌":
                return "청년층을 위한 특별 저축 상품으로, 높은 혜택과 혜택 포인트 적립";
            case "기본 플랜 계좌":
                return "단기 및 장기 예금을 모두 지원하는 기본 플랜 계좌";
            case "패밀리 계좌":
                return "가족 단위 사용에 적합한 패밀리 계좌로, 자녀 계좌 연계 기능 제공";
            case "연금 저축 계좌":
                return "노후 대비를 위한 연금 저축 계좌, 세제 혜택 제공";
            case "고액 저축 계좌":
                return "고액 자산 고객을 위한 고액 저축 상품, 높은 이자율 보장";
            case "자유 적립 계좌":
                return "자유롭게 적립 가능한 계좌, 수수료 절감 혜택";
            case "단기 투자 계좌":
                return "단기 투자에 적합한 계좌, 유동성 자산 관리에 용이";
            default:
                return "기본 저축 계좌로 안정적인 금리 제공";
        }
    }

    /**
     * 상품명에 따른 랜덤 이자율 생성
     * @param productName 상품명
     * @param random Random 객체
     * @return 이자율(BigDecimal)
     */
    private static BigDecimal generateInterestRate(String productName, Random random) {
        double rate;
        if ("프리미엄 계좌".equals(productName) || "VIP 계좌".equals(productName) || "스마트 계좌".equals(productName) || "고액 저축 계좌".equals(productName)) {
            // 프리미엄 및 VIP 계좌의 이자율을 1.5%~2.0% 사이로 설정
            rate = 1.5 + (random.nextDouble() * 0.5);
        } else {
            // 일반 계좌의 이자율을 0.2%~1.5% 사이로 설정
            rate = 0.2 + (random.nextDouble() * 1.3);
        }
        return new BigDecimal(String.format("%.2f", rate));
    }
}

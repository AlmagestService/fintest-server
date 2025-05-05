package com.almagest_dev.fintest_server.config;
import com.almagest_dev.fintest_server.util.ApiRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// WebMvc 관련 설정 및 인터셉터 등록을 담당하는 설정 클래스
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApiRequestInterceptor apiRequestInterceptor;

    /**
     * API 요청 인터셉터 등록
     * @param registry 인터셉터 레지스트리
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiRequestInterceptor)
                .addPathPatterns("/fintest/api/**"); // 모든 API 요청에 적용
    }

    /**
     * RestTemplate 빈 등록
     * @return RestTemplate 객체
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}


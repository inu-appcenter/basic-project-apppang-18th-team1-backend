package com.team1.appang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

//외부 API(Supabase 등) 호출에 사용할 RestClient를 Bean으로 등록
//new로 직접 생성하지 않고 Spring이 관리하는 객체로 주입받기 위함
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        //외부 API가 응답을 안 주고 지연될 때 요청 스레드가 무한정 붙잡히지 않도록 타임아웃 설정
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
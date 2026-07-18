package com.team1.appang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

//외부 API(Supabase 등) 호출에 사용할 RestClient를 Bean으로 등록
//new로 직접 생성하지 않고 Spring이 관리하는 객체로 주입받기 위함
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
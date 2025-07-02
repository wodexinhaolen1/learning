package com.example.backgroundagriculture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsFilterConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");    // 允许前端地址跨域
        configuration.setAllowCredentials(true);                    // 允许携带凭证
        configuration.addAllowedMethod("*");                        // 允许所有HTTP方法
        configuration.addAllowedHeader("*");                        // 允许所有请求头
        configuration.addExposedHeader("*");                        // 允许所有响应头
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);     // 对所有接口生效
        
        return new CorsFilter(source);
    }
} 
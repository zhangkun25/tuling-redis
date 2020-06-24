package com.tuling.sample.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

@Configuration
@EnableConfigurationProperties(BloomProperties.class)
public class BloomConfig {

    @Autowired
    private BloomProperties bloomProperties;

    @Bean
    public BloomFilter<String> bloomFilter(){
        return BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")),bloomProperties.getExpectedInsertions(),bloomProperties.getFpp());
    }
}

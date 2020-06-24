package com.tuling.sample.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bloom.filter")
public class BloomProperties {
    //存入数据规模
    private Integer expectedInsertions;
    //误判率
    private double fpp;

    public Integer getExpectedInsertions() {
        return expectedInsertions;
    }

    public void setExpectedInsertions(Integer expectedInsertions) {
        this.expectedInsertions = expectedInsertions;
    }

    public double getFpp() {
        return fpp;
    }

    public void setFpp(double fpp) {
        this.fpp = fpp;
    }
}

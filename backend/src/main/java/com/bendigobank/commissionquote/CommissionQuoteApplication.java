package com.bendigobank.commissionquote;

import com.bendigobank.commissionquote.config.VendorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(VendorProperties.class)
public class CommissionQuoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommissionQuoteApplication.class, args);
    }
}

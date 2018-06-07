package ru.kpfu.itis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@EnableCaching
public class SaasConfig {

    public static final String CACHE_MANAGER = "cache_manager";
    public static final String EXPIRED_CACHE = "expired";


    @Autowired
    private PaymentConfig paymentConfig;

    @Bean
    public ObjectMapper simpleObjectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = CACHE_MANAGER)
    public CacheManager expiredCacheManager() {
        return new ConcurrentMapCacheManager(EXPIRED_CACHE);
    }

    @PostConstruct
    public void setup() {
        // Set your secret key: remember to change this to your live secret key in production
        // See your keys here: https://dashboard.stripe.com/account/apikeys
        Stripe.apiKey = paymentConfig.getStripeServerToken();
    }
}

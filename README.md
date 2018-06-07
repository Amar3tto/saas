# saas
Saas module settings.

1. Configure @Bean PaymentConfig in your CoreConfig.
2. Add @ComponentScan("ru.kpfu.itis") annotation to your WebConfig and PersistenceConfig
3.  Set freemarker template path:
    FreemarkerConfigurer.setTemplatePaths("classpath:saas/views/");
4. Implement TenantService
5. Implement EmailService
6. Add implements Tenantable to classes whose objects belong to a certain tenant 
7. Create class which extends PayPalOrderService.
8. Add extends BillingUser to main User entity.
9. Create @Bean which extends SaasUtils and make sure you properly obtain current user from context.
10. Create Filter which extends SubscriptionCheckFilter.
11. Add SaasConfig to WebAppInitializer.

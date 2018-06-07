# saas

Saas module settings.

Configure @Bean PaymentConfig in your CoreConfig.
Add @ComponentScan("ru.kpfu.itis") annotation to your WebConfig and PersistenceConfig
Set freemarker template path: FreemarkerConfigurer.setTemplatePaths("classpath:saas/views/");
Implement TenantService
Implement EmailService
Add implements Tenantable to classes whose objects belong to a certain tenant
Create class which extends PayPalOrderService.
Add extends BillingUser to main User entity.
Create @Bean which extends SaasUtils and make sure you properly obtain current user from context.
Create Filter which extends SubscriptionCheckFilter.
Add SaasConfig to WebAppInitializer.

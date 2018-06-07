package ru.kpfu.itis.service;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.config.SaasConfig;
import ru.kpfu.itis.model.BillingUser;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SaasPaymentService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SaasPaymentService.class);

    @Autowired
    private TenantService tenantService;

    @Autowired
    private SaasService saasService;

    @Autowired
    @Qualifier(SaasConfig.CACHE_MANAGER)
    private CacheManager cacheManager;

    @CacheEvict(allEntries = true, value = {SaasConfig.EXPIRED_CACHE})
    @Scheduled(fixedDelay = 15 * 60 * 1000, initialDelay = 15 * 60 * 1000)
    public void reportExpiredCacheEvict() {
        LOGGER.info("Flush expired cache");
    }

    private void expiredCacheClear() {
        Cache expiredCache = cacheManager.getCache(SaasConfig.EXPIRED_CACHE);
        expiredCache.clear();
        LOGGER.info("Flush expired cache");
    }

    @Transactional
    public void deleteBillingCard(Long tenantId) throws Exception {
        BillingUser user = tenantService.getBillingUser(tenantId);
        Customer customer = Customer.retrieve(user.getBillingId());
        if (customer == null) {
            throw new EntityNotFoundException();
        }
        ExternalAccountCollection sources = customer.getSources();
        for (ExternalAccount account : sources.getData()) {
            if (account.getId().equals(customer.getDefaultSource()) && account instanceof Card) {
                Card card = (Card) account;
                card.delete();
            }
        }
        saasService.deleteCard(user);
    }

    @Transactional
    public void saveBillingCard(Long tenantId, String token) throws Exception {
        // Token is created using Stripe.js or Checkout!
        // Get the payment token submitted by the form:
        BillingUser user = tenantService.getBillingUser(tenantId);
        // Create a Customer:
        Map<String, Object> customerParams = new HashMap<>();
        if (user.getBillingId() == null) {
            customerParams.put("email", user.getEmail());
            customerParams.put("source", token);
            Customer customer = Customer.create(customerParams);
            user.setBillingId(customer.getId());
            tenantService.save(user);
        } else {
            Customer customer = Customer.retrieve(user.getBillingId());
            customerParams.put("source", token);
            customer.update(customerParams);
        }
        saasService.saveCard(getCustomerCard(user), user);
        expiredCacheClear();
    }

    @Transactional
    public Subscription subscribeUser(Long tenantId, ru.kpfu.itis.model.Plan plan) throws Exception {
        BillingUser user = tenantService.getBillingUser(tenantId);
        Map<String, Object> params = new HashMap<>();
        params.put("customer", user.getBillingId());
        params.put("plan", plan.getCode());
        Subscription subscription = Subscription.create(params);
        saasService.saveSubscription(subscription, user, plan);
        return subscription;
    }

    public Subscription getSubscriptionInfo(Long tenantId) {
        BillingUser billingUser = tenantService.getBillingUser(tenantId);
        if (billingUser == null) {
            return null;
        }
        return getSubscriptionInfo(billingUser);
    }

    public Subscription getSubscriptionInfo(BillingUser currentUser) {
        try {
            Customer customer = Customer.retrieve(currentUser.getBillingId());
            if (customer == null) {
                return null;
            }
            CustomerSubscriptionCollection subscriptions = customer.getSubscriptions();
            List<Subscription> subscriptionsData = subscriptions.getData();
            if (subscriptionsData.size() == 0) {
                return null;
            }
            for (Subscription subscription : subscriptionsData) {
                if (!subscription.getPlan().getName().startsWith("hidden")) {
                    return subscription;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Stripe get subscription error " + e);
        }
        return null;
    }

    public Card getCustomerCard(BillingUser billingUser) {
        try {
            Customer customer = Customer.retrieve(billingUser.getBillingId());
            if (customer == null) {
                return null;
            }
            return getCustomerCard(customer);
        } catch (Exception e) {
            return null;
        }
    }

    public Card getCustomerCard(Customer customer) {
        ExternalAccountCollection sources = customer.getSources();
        for (ExternalAccount account : sources.getData()) {
            if (account.getId().equals(customer.getDefaultSource()) && account instanceof Card) {
                return (Card) account;
            }
        }
        return null;
    }

    public String createStripeAccount(String email) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("country", "US");
        params.put("type", "standard");
        params.put("email", email);
        Account acct = Account.create(params);
        return acct.getId();
    }

    @Cacheable(value = SaasConfig.EXPIRED_CACHE, cacheManager = SaasConfig.CACHE_MANAGER)
    public Boolean isAccountExpired(Long tenantId) {
        try {
            BillingUser user = tenantService.getBillingUser(tenantId);
            if (tenantService.isAdmin(user)) {
                return false;
            }
            Customer customer = Customer.retrieve(user.getBillingId());
            if (customer == null) {
                return true;
            }
            CustomerSubscriptionCollection subscriptions = customer.getSubscriptions();
            List<Subscription> subscriptionsData = subscriptions.getData();
            if (subscriptionsData.size() == 0) {
                return true;
            }
            Subscription subscription = subscriptionsData.get(0);
            if (Objects.equals(subscription.getStatus(), "canceled") ||
                    Objects.equals(subscription.getStatus(), "unpaid") || Objects.equals(subscription.getStatus(), "past_due")) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public void switchPlan(Long tenantId, ru.kpfu.itis.model.Plan plan) throws Exception {
        BillingUser user = tenantService.getBillingUser(tenantId);
        Subscription subscription = getSubscriptionInfo(user);
        if (subscription == null) {
            LOGGER.info("User tariff is null for {}", tenantId);
            subscription = subscribeUser(tenantId, plan);
            expiredCacheClear();
        }
        if (subscription.getPlan().getName().equals(plan.getTitle())) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("plan", plan.getCode());
        params.put("trial_end", "now");
        subscription.update(params);
        saasService.updateSubscriptionPlan(subscription, plan);
    }

}

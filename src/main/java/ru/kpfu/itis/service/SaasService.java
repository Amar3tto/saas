package ru.kpfu.itis.service;

import com.stripe.model.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.kpfu.itis.dto.TenantDto;
import ru.kpfu.itis.model.BillingUser;
import ru.kpfu.itis.model.Card;
import ru.kpfu.itis.model.Subscription;
import ru.kpfu.itis.model.utils.PaymentStatus;
import ru.kpfu.itis.repository.CardRepository;
import ru.kpfu.itis.repository.PlanRepository;
import ru.kpfu.itis.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SaasService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SaasPaymentService.class);

    @Autowired
    private TenantService tenantService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PlanRepository planRepository;

    public void updateStripeData(Long tenantId, String stripeAccount) {
        BillingUser billingUser = tenantService.getBillingUser(tenantId);
        billingUser.setStripeAccount(stripeAccount);
        tenantService.save(billingUser);
    }

    public void updatePaypalData(Long tenantId, String paypalAccount) {
        BillingUser billingUser = tenantService.getBillingUser(tenantId);
        billingUser.setPaypalAccount(paypalAccount);
        tenantService.save(billingUser);
    }

    public Card getUserCard(Long tenantId) {
        BillingUser user = tenantService.getBillingUser(tenantId);
        if (user == null || user.getCardId() == null) {
            return null;
        }
        return cardRepository.findOne(user.getCardId());
    }

    public void deleteCard(BillingUser user) {
        cardRepository.delete(user.getCardId());
    }

    public void saveCard(com.stripe.model.Card card, BillingUser user) {
        if (card == null) {
            return;
        }
        Card dbCard = new Card();
        dbCard.setId(card.getId());
        dbCard.setStatus(card.getStatus());
        dbCard.setLast4(card.getLast4());
        dbCard = cardRepository.save(dbCard);
        user.setCardId(dbCard.getId());
        tenantService.save(user);
    }

    @Transactional
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void updateTariffs() {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("limit", 100);
            List<Plan> plans = Plan.list(params).getData();
            List<String> plansCodes = plans.stream().map(Plan::getId).collect(Collectors.toList());
            deleteOldPlans(plansCodes);
            Map<String, ru.kpfu.itis.model.Plan> plansMap = planRepository.findAll().stream().collect(Collectors.toMap(ru.kpfu.itis.model.Plan::getCode, plan -> plan));
            for (Plan plan : plans) {
                createPlan(plansMap.get(plan.getId()), plan);
            }
            LOGGER.debug("Saved " + plans.size() + " plans");
        } catch (Exception e) {
            LOGGER.error("Stripe error on fetching plans. " + e);
        }
    }

    public ru.kpfu.itis.model.Plan findPlanByCode(String code) {
        return planRepository.findByCode(code);
    }

    @Transactional
    public void deletePlan(String code) {
        planRepository.deleteByCode(code);
    }

    public void deleteOldPlans(List<String> codes) {
        planRepository.deleteByCodeNotIn(codes);
    }

    public ru.kpfu.itis.model.Plan createPlan(ru.kpfu.itis.model.Plan dbPlan, Plan plan) {
        if (dbPlan == null) {
            dbPlan = new ru.kpfu.itis.model.Plan();
        }
        dbPlan.setCode(plan.getId());
        dbPlan.setInterval(plan.getInterval());
        dbPlan.setIntervalCount(plan.getIntervalCount());
        dbPlan.setTitle(plan.getName());
        dbPlan.setTariffOpen(true);
        dbPlan.setPrice(plan.getAmount());
        dbPlan.setTrial(plan.getTrialPeriodDays() != 0);
        return planRepository.save(dbPlan);
    }

    public void saveSubscription(com.stripe.model.Subscription subscription, BillingUser billingUser, ru.kpfu.itis.model.Plan plan) {
        if (subscription == null) {
            return;
        }
        Subscription dbSubscription = new Subscription();
        dbSubscription.setId(subscription.getId());
        try {
            dbSubscription.setPaymentStatus(PaymentStatus.valueOf(subscription.getStatus().toUpperCase()));
        } catch (Exception e) {
            dbSubscription.setPaymentStatus(PaymentStatus.OK);
        }
        dbSubscription.setPlanId(plan.getId());
        dbSubscription.setUserId(billingUser.getId());
        dbSubscription.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
        subscriptionRepository.save(dbSubscription);
    }

    public List<Subscription> getSubscriptions(Long tenantId) {
        return subscriptionRepository.findByUserId(tenantId);
    }

    public void updateSubscriptionPlan(com.stripe.model.Subscription subscription, ru.kpfu.itis.model.Plan plan) {
        Subscription dbSubscription = subscriptionRepository.findOne(subscription.getId());
        dbSubscription.setPlanId(plan.getId());
        subscriptionRepository.save(dbSubscription);
    }

    public ru.kpfu.itis.model.Plan getPlanById(Long id) {
        return planRepository.findOne(id);
    }

    public List<TenantDto> getAllTenantsInfo() {
        List<BillingUser> tenants = tenantService.getAllTenants();
        List<TenantDto> tenantDtos = new ArrayList<>();
        for (BillingUser user : tenants) {
            TenantDto tenantDto = new TenantDto();
            tenantDto.setBillingUser(user);
            if (user.getCardId() != null) {
                tenantDto.setCard(cardRepository.findOne(user.getCardId()));
            }
            List<Subscription> subscriptions = getSubscriptions(user.getId());
            if (!CollectionUtils.isEmpty(subscriptions)) {
                Subscription subscription = subscriptions.get(0);
                tenantDto.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
                tenantDto.setPlan(planRepository.findOne(subscription.getPlanId()));
            }
            tenantDtos.add(tenantDto);
        }
        return tenantDtos;
    }
}

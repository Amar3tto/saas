package ru.kpfu.itis.dto;

import ru.kpfu.itis.model.BillingUser;
import ru.kpfu.itis.model.Card;
import ru.kpfu.itis.model.Plan;

public class TenantDto {

    private BillingUser billingUser;

    private Card card;

    private Long currentPeriodEnd;

    private Plan plan;

    public BillingUser getBillingUser() {
        return billingUser;
    }

    public void setBillingUser(BillingUser billingUser) {
        this.billingUser = billingUser;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Long getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public void setCurrentPeriodEnd(Long currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}

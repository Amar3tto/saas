package ru.kpfu.itis.model;

import javax.persistence.*;

@MappedSuperclass
public abstract class BillingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "billing_id")
    private String billingId;

    @Column(name = "card_id")
    private String cardId;

    @Column(name = "paypal_account")
    private String paypalAccount;

    @Column(name = "stripe_account")
    private String stripeAccount;

    public String getBillingId() {
        return billingId;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
    }

    public abstract String getEmail();

    public abstract String getName();

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaypalAccount() {
        return paypalAccount;
    }

    public void setPaypalAccount(String paypalAccount) {
        this.paypalAccount = paypalAccount;
    }

    public String getStripeAccount() {
        return stripeAccount;
    }

    public void setStripeAccount(String stripeAccount) {
        this.stripeAccount = stripeAccount;
    }
}

package ru.kpfu.itis.config;

public class PaymentConfig {

    private String payPalClientId;

    private String payPalClientSecret;

    private String payPalMode;

    private String stripeServerToken;

    private String stripeClientToken;

    private String stripeClientId;

    /*
    Sandbox or not
     */
    private String paypalApiUrl;

    public String getPayPalClientId() {
        return payPalClientId;
    }

    public void setPayPalClientId(String payPalClientId) {
        this.payPalClientId = payPalClientId;
    }

    public String getPayPalClientSecret() {
        return payPalClientSecret;
    }

    public void setPayPalClientSecret(String payPalClientSecret) {
        this.payPalClientSecret = payPalClientSecret;
    }

    public String getPayPalMode() {
        return payPalMode;
    }

    public void setPayPalMode(String payPalMode) {
        this.payPalMode = payPalMode;
    }

    public String getStripeServerToken() {
        return stripeServerToken;
    }

    public void setStripeServerToken(String stripeServerToken) {
        this.stripeServerToken = stripeServerToken;
    }

    public String getStripeClientToken() {
        return stripeClientToken;
    }

    public void setStripeClientToken(String stripeClientToken) {
        this.stripeClientToken = stripeClientToken;
    }

    public String getStripeClientId() {
        return stripeClientId;
    }

    public void setStripeClientId(String stripeClientId) {
        this.stripeClientId = stripeClientId;
    }

    public String getPaypalApiUrl() {
        return paypalApiUrl;
    }

    public void setPaypalApiUrl(String paypalApiUrl) {
        this.paypalApiUrl = paypalApiUrl;
    }
}

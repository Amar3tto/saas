package ru.kpfu.itis;

public interface WebConstants {

    interface Paypal {
        String PAYPAL_BASE_URL = "/paypal";
        String PAYPAL_ORDER_BASE_URL = PAYPAL_BASE_URL + "/order";
        String PAYPAL_ORDER_RETURN_URL = PAYPAL_ORDER_BASE_URL + "/return";
        String PAYPAL_ORDER_CANCEL_URL = PAYPAL_ORDER_BASE_URL + "/cancel";
        String PAYPAL_OAUTH_URL = PAYPAL_BASE_URL + "/oauth";
        String PAYPAL_TEST_URL = PAYPAL_BASE_URL + "/test";
    }

    interface Saas {
        String SAAS_BASE_URL = "/saas";
        String SAAS_TENANTS_INFO_URL = SAAS_BASE_URL + "/subscribers";
        String SAAS_INTEGRATIONS_URL = SAAS_BASE_URL + "/integrations";
        String SAAS_INTEGRATIONS_STRIPE_URL = SAAS_INTEGRATIONS_URL + "/stripe";
    }

    interface Stripe {
        String STRIPE_CONNECT_CALLBACK = "/stripe/connect";
        String PAYMENT_BASE_URL = "/payment";
        String PAYMENT_FAILED = PAYMENT_BASE_URL + "/failed";
        String PAYMENT_SUCCEEDED = PAYMENT_BASE_URL + "/succeeded";
        String PLAN_BASE_URL = "/plan";
        String PLAN_UPDATED_URL = PLAN_BASE_URL + "/update";
        String PLAN_CREATED_URL = PLAN_BASE_URL + "/create";
        String PLAN_DELETED_URL = PLAN_BASE_URL + "/delete";
    }
}

package ru.kpfu.itis.controller.webhooks.stripe;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.itis.WebConstants;
import ru.kpfu.itis.config.PaymentConfig;
import ru.kpfu.itis.controller.BaseApiController;
import ru.kpfu.itis.model.Plan;
import ru.kpfu.itis.service.EmailService;
import ru.kpfu.itis.service.SaasPaymentService;
import ru.kpfu.itis.service.SaasService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Controller
public class PaymentWebhooksController extends BaseApiController {

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentWebhooksController.class);

    public static final String TOKEN_URI = "https://connect.stripe.com/oauth/token";

    @Autowired
    private EmailService emailService;

    @Autowired
    private SaasService saasService;

    @Autowired
    private SaasPaymentService saasPaymentService;

    @Autowired
    private PaymentConfig paymentConfig;

    @RequestMapping(value = WebConstants.Stripe.PAYMENT_SUCCEEDED, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> paymentSucceededSendEmail(@RequestBody String event) {
        try {
            LOGGER.debug("Payment succeeded invoice caught!");
            Invoice invoice = getInvoiceFromEvent(event);
            Customer customer = Customer.retrieve(invoice.getCustomer());
            Plan plan = saasService.findPlanByCode(Subscription.retrieve(invoice.getSubscription()).getPlan().getId());
            if (plan == null) {
                LOGGER.debug("Cant find plan in database. Plan = null");
                return createGoodResponse();
            }
            if (invoice.getAmountDue() == 0) {
                LOGGER.debug("Amount = 0");
                if (Boolean.TRUE.equals(plan.getTrial())) {
                    emailService.sendTrialPeriodStartMessage(customer.getEmail());
                } else {
                    LOGGER.debug("This is plan with trial");
                }
                return createGoodResponse();
            }
            LOGGER.debug("Current plan is " + plan.getTitle());
            emailService.sendPaymentSuccessMessage(customer.getEmail(), plan, invoice.getAmountDue(), invoice.getCurrency());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return createGoodResponse();
    }

    @RequestMapping(value = WebConstants.Stripe.PAYMENT_FAILED, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> paymentFailedSendEmail(@RequestBody String event) {
        try {
            LOGGER.debug("Payment failed invoice caught!");
            Invoice invoice = getInvoiceFromEvent(event);
            Customer customer = Customer.retrieve(invoice.getCustomer());
            Plan plan = saasService.findPlanByCode(Subscription.retrieve(invoice.getSubscription()).getPlan().getId());
            if (plan == null) {
                LOGGER.debug("Cant find plan in database. Plan = null");
                return createGoodResponse();
            }
            if (saasPaymentService.getCustomerCard(customer) == null) {
                emailService.sendPaymentFailedAndNoCardMessage(customer.getEmail(), plan);
            } else {
                emailService.sendPaymentFailedMessage(customer.getEmail(), plan);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return createGoodResponse();
    }

    private Invoice getInvoiceFromEvent(String event) {
        Event eventObj = Event.GSON.fromJson(event, Event.class);
        return (Invoice) eventObj.getData().getObject();
    }

    @RequestMapping(value = WebConstants.Stripe.STRIPE_CONNECT_CALLBACK, method = RequestMethod.GET)
    public ResponseEntity<Object> updateStripeData(@RequestParam("code") String code, @RequestParam(value = "state", required = false) Long tenantId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URI uri = null;
        try {
            uri = new URIBuilder(TOKEN_URI)
                    .setParameter("client_secret", paymentConfig.getStripeServerToken())
                    .setParameter("grant_type", "authorization_code")
                    .setParameter("client_id", paymentConfig.getStripeClientId())
                    .setParameter("code", code)
                    .build();
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage());
        }

        // Make /oauth/token endpoint POST request
        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpResponse resp = null;
        try {
            resp = httpClient.execute(httpPost);

            // Grab access_token (use this as your user's API key)
            String bodyAsString = null;
            bodyAsString = EntityUtils.toString(resp.getEntity());

            Type t = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> map = new GsonBuilder().create().fromJson(bodyAsString, t);
            String token = map.get("access_token");
            String accountId = map.get("stripe_user_id");
            saasService.updateStripeData(tenantId, accountId);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return createGoodResponse();
    }
}

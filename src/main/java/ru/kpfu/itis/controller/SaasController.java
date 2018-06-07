package ru.kpfu.itis.controller;

import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.kpfu.itis.SaasUtils;
import ru.kpfu.itis.TimeAgo;
import ru.kpfu.itis.WebConstants;
import ru.kpfu.itis.config.PaymentConfig;
import ru.kpfu.itis.model.BillingUser;
import ru.kpfu.itis.model.Subscription;
import ru.kpfu.itis.service.SaasPaymentService;
import ru.kpfu.itis.service.SaasService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class SaasController {

    @Autowired
    private SaasService saasService;

    @Autowired
    private SaasUtils saasUtils;

    @Autowired
    private PaymentConfig paymentConfig;

    @Autowired
    private SaasPaymentService saasPaymentService;

    @RequestMapping(value = WebConstants.Saas.SAAS_TENANTS_INFO_URL, method = RequestMethod.GET)
    public String getTenantsInfo(Model model) {
        model.addAttribute("tenants", saasService.getAllTenantsInfo());
        return "tenantsInfo";
    }

    @RequestMapping(value = WebConstants.Saas.SAAS_INTEGRATIONS_URL, method = RequestMethod.GET)
    public String getIntegrationsPage(Model model, HttpServletRequest httpServletRequest) {
        BillingUser user = saasUtils.getCurrentUser(httpServletRequest);
        model.addAttribute("billingUser", user);
        model.addAttribute("stripeClientId", paymentConfig.getStripeClientId());
        model.addAttribute("mainDomain", SaasUtils.extractDomain(httpServletRequest));
        model.addAttribute("paypalClientId", paymentConfig.getPayPalClientId());
        model.addAttribute("paypalMode", paymentConfig.getPayPalMode());
        List<Subscription> subscriptionList = saasService.getSubscriptions(user.getId());
        model.addAttribute("subscription", subscriptionList.size() == 0 ? null : subscriptionList.get(0));
        model.addAttribute("card", saasService.getUserCard(user.getId()));
        model.addAttribute("tariff", subscriptionList.size() == 0 ? null : saasService.getPlanById(subscriptionList.get(0).getPlanId()));
        model.addAttribute("timeAgoUtil", new TimeAgo());
        model.addAttribute("now", new Date().getTime());
        model.addAttribute("expired", saasPaymentService.isAccountExpired(user.getId()));
        return "integration";
    }

    @RequestMapping(value = WebConstants.Saas.SAAS_INTEGRATIONS_STRIPE_URL, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createStripeAccount(HttpServletRequest httpServletRequest) {
        BillingUser billingUser = saasUtils.getCurrentUser(httpServletRequest);
        try {
            String accountId = saasPaymentService.createStripeAccount(billingUser.getEmail());
            saasService.updateStripeData(billingUser.getId(), accountId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (StripeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package ru.kpfu.itis.controller.webhooks.paypal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.itis.WebConstants;
import ru.kpfu.itis.service.SaasPaypalAPIService;
import ru.kpfu.itis.service.PaypalOrderPaymentService;
import ru.kpfu.itis.service.SaasService;

@Controller
public class PaypalWebhookController {

    private final Logger LOGGER = LoggerFactory.getLogger(PaypalWebhookController.class);

    @Autowired
    private SaasPaypalAPIService saasPaypalAPIService;

    @Autowired
    private SaasService saasService;

    @Autowired
    private PaypalOrderPaymentService paypalOrderPaymentService;

    @RequestMapping(value = WebConstants.Paypal.PAYPAL_ORDER_RETURN_URL, method = RequestMethod.GET)
    public String executePayment(@RequestParam("paymentId") String paymentId, @RequestParam(value = "PayerID") String payerId) {
        LOGGER.info("Caught paypal return webhook");
        paypalOrderPaymentService.executePayment(paymentId, payerId);
        return "paypal/succeed";
    }

    @RequestMapping(value = WebConstants.Paypal.PAYPAL_ORDER_CANCEL_URL, method = RequestMethod.GET)
    public String cancelPayment(@RequestParam(value = "paymentId", required = false) String paymentId) {
        LOGGER.info("Caught paypal cancel webhook");
        return "paypal/canceled";
    }

    @RequestMapping(value = WebConstants.Paypal.PAYPAL_OAUTH_URL, method = RequestMethod.GET)
    public String oauth(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) Long tenantId,
            Model model) {
        LOGGER.info("Caught paypal oauth webhook");
        String accessToken = saasPaypalAPIService.getAccessToken(code);
        boolean status = true;
        if (accessToken == null) {
            status = false;
        }
        String userEmail = saasPaypalAPIService.getUserEmail(accessToken);
        if (userEmail != null) {
            saasService.updatePaypalData(tenantId, userEmail);
        } else {
            status = false;
        }
        model.addAttribute("success", status);
        return "callbacks/paypal/paypal_oauth";
    }
}

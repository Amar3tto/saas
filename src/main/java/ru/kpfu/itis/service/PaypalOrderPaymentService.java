package ru.kpfu.itis.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kpfu.itis.config.PaymentConfig;
import ru.kpfu.itis.model.BillingUser;
import ru.kpfu.itis.model.interfaces.Tenantable;
import ru.kpfu.itis.model.utils.PaymentStatus;
import ru.kpfu.itis.repository.PaymentRepository;

import javax.persistence.EntityNotFoundException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class PaypalOrderPaymentService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PaypalOrderPaymentService.class);

    @Autowired
    private PaymentConfig paymentConfig;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private PaymentRepository paymentRepository;

    public abstract String getTotal(Tenantable tenantable);

    public abstract String getCurrency(Tenantable tenantable);

    public abstract String getItemName(Tenantable tenantable);

    /*
    info about order (id etc.) for execution in future
     */
    public abstract String getCustom(Tenantable tenantable);

    /*
    returns payment page
     */
    public String createPaymentForOrder(Tenantable order, String domain) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.00", otherSymbols);
        if (order == null) {
            throw new EntityNotFoundException("Invalid order id");
        }

        Details details = new Details();
        details.setShipping("0");
        details.setFee("0");
        details.setSubtotal(getTotal(order));

        Amount amount = new Amount();
        amount.setCurrency(getCurrency(order));
        amount.setTotal(getTotal(order));
        amount.setDetails(details);

        BillingUser billingUser = tenantService.getBillingUser(order.getTenantId());
        if (billingUser == null || billingUser.getPaypalAccount() == null) {
            throw new EntityNotFoundException("Paypal account is not connected");
        }
        Payee payee = new Payee();
        payee.setEmail(billingUser.getPaypalAccount());

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setCustom(getCustom(order));
        transaction.setPayee(payee);
        transaction.setDescription("This is the payment transaction description.");
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Item item = new Item();
        item
                .setName(getItemName(order))
                .setQuantity("1")
                .setCurrency(getCurrency(order))
                .setPrice(getTotal(order));
        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        itemList.setItems(items);

        transaction.setItemList(itemList);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(domain + "/paypal/order/cancel");
        redirectUrls.setReturnUrl(domain + "/paypal/order/return");
        payment.setRedirectUrls(redirectUrls);

        try {
            Payment createdPayment = payment.create(getApiContext());
            ru.kpfu.itis.model.Payment dbPayment = new ru.kpfu.itis.model.Payment();
            dbPayment.setId(createdPayment.getId());
            dbPayment.setUserId(billingUser.getId());
            dbPayment.setStatus(PaymentStatus.UNPAID);
            paymentRepository.save(dbPayment);
            return createdPayment.getLinks().get(1).getHref();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public void executePayment(String paymentId, String payerId) {
        try {
            Payment payment = Payment.get(getApiContext(), paymentId);
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            Payment executedPayment = payment.execute(getApiContext(), paymentExecution);
            LOGGER.info("Payment with id {} executed", executedPayment.getId());
            ru.kpfu.itis.model.Payment dbPayment = paymentRepository.findOne(paymentId);
            dbPayment.setStatus(PaymentStatus.OK);
            paymentRepository.save(dbPayment);
            afterPaymentExecuted(executedPayment);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /*
    check executedPayment.getTransactions().get(0).getCustom() for info
     */
    public abstract void afterPaymentExecuted(Payment executedPayment);

    private APIContext getApiContext() {
        return new APIContext(paymentConfig.getPayPalClientId(), paymentConfig.getPayPalClientSecret(), paymentConfig.getPayPalMode());
    }
}

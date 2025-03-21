package com.product.services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class PayPalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private APIContext apiContext;

    @SuppressWarnings("deprecation")
    @PostConstruct
    public void init() throws PayPalRESTException {
        Map<String, String> config = new HashMap<>();
        config.put("mode", mode);
        apiContext = new APIContext(new OAuthTokenCredential(clientId, clientSecret, config).getAccessToken());
        apiContext.setConfigurationMap(config);
    }

    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    public Refund refundPayment(String saleId, double amount, String currency) throws PayPalRESTException {
        Sale sale = Sale.get(apiContext, saleId);

        Amount refundAmount = new Amount();
        refundAmount.setCurrency(currency);
        refundAmount.setTotal(String.format(Locale.US, "%.2f", amount));

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setAmount(refundAmount);

        return sale.refund(apiContext, refundRequest);
    }

    public APIContext getAPIContext() {
        return apiContext;
    }
}
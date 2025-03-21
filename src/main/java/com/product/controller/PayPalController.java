package com.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Refund;
import com.paypal.base.rest.PayPalRESTException;
import com.product.entity.Product;
import com.product.entity.ProductOrder;
import com.product.services.PayPalService;
import com.product.services.ProductOrderService;
import com.product.services.ProductServices;

@Controller
@RequestMapping("/payment")
public class PayPalController {

    @Autowired
   // private TransactionRepository transactionRepositor;
    ProductServices productServices;
    
    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    PayPalService payPalService;
    @GetMapping("/buy/{id}")
    public String showProductDetail(@PathVariable int id, Model model) {
        try {
            Product product = productServices.getProductById(id);
            if (product == null) {
                return "error";
            }
            model.addAttribute("productName", product.getName());
            model.addAttribute("amount", product.getPrice());
            return "payment";
        } catch (Exception e) {
            System.out.println(e);
            return "error";
        }
    }

    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam String name, @RequestParam double price, Model model) {
        System.out.println("Entered in payment");
        model.addAttribute("productName", name);
        model.addAttribute("amount", price);
        return "payment";
    }

    @PostMapping("/payment")
    public RedirectView createPayment(@RequestParam double amount) {
        try {
            String cancelUrl = "http://localhost:8081/payment/cancel";
            String successUrl = "http://localhost:8081/payment/success";
            Payment payment = payPalService.createPayment(
                    amount,
                    "USD",
                    "paypal",
                    "sale",
                    "Payment for product",
                    cancelUrl,
                    successUrl);

            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
        return new RedirectView("/error");
    }

    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            Model model) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            System.out.println("Execute: " + payment.toJSON());
            if (payment.getState().equals("approved")) {
                model.addAttribute("message", "Payment successful!");
                model.addAttribute("saleId", payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId());
                model.addAttribute("amount", payment.getTransactions().get(0).getAmount().getTotal());
                model.addAttribute("currency", payment.getTransactions().get(0).getAmount().getCurrency());
                return "paymentSucess";
            }
        } catch (PayPalRESTException e) {
            model.addAttribute("message", "Error occurred: " + e.getMessage());
            return "paymentError";
        }
        return "paymentError";
    }

    @GetMapping("/cancel")
    public String paymentCancel(Model model) {
        model.addAttribute("message", "Payment was canceled.");
        return "index";
    }

    @GetMapping("/error")
    public String paymentError(Model model) {
        model.addAttribute("message", "An error occurred during payment.");
        return "paymentError";
    }
    @GetMapping("/orders")
    public String showOrders(Model model) {
        List<ProductOrder> orders = productOrderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "ordered"; // This refers to ordered.html in templates folder
    }
    @PostMapping("/refund")
    public String refundPayment(
            @RequestParam String saleId,
            @RequestParam double amount,
            @RequestParam String currency,
            Model model) {
        try {
            Refund refund = payPalService.refundPayment(saleId, amount, currency);
            model.addAttribute("refundId", refund.getId());
            model.addAttribute("saleId", saleId);
            model.addAttribute("amount", amount);
            model.addAttribute("currency", currency);
            model.addAttribute("refundDate", refund.getCreateTime());
            return "refund";
        } catch (PayPalRESTException e) {
            model.addAttribute("message", "Refund failed: " + e.getMessage());
            return "paymentError";
        }
    }

    //  @PostMapping("/refund")
//    public String refundPayment(
//            @RequestParam String saleId,
//            @RequestParam double amount,
//            @RequestParam String currency,
//            Model model) {
//        try {
//            Refund refund = payPalService.refundPayment(saleId, amount, currency);
//            
//            // Get current date and time
//            String refundDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//            model.addAttribute("refundId", refund.getId());
//            model.addAttribute("saleId", saleId);
//            model.addAttribute("amount", amount);
//            model.addAttribute("currency", currency);
//            model.addAttribute("refundDate", refundDate);
//
//            return "refundSuccess"; 
//        } catch (PayPalRESTException e) {
//            model.addAttribute("message", "Refund failed: " + e.getMessage());
//            return "refundError";
//        }
    
}


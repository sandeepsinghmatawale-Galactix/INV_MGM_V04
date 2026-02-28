package com.barinventory.billing.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barinventory.billing.dtos.BillResponse;
import com.barinventory.billing.dtos.CreateBillRequest;
import com.barinventory.billing.service.BillingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/billing/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillingService billingService;

    /**
     * GET /billing/bills
     * Show logged-in user's bills
     */
    @GetMapping
    public String billListPage(@AuthenticationPrincipal UserDetails userDetails,
                               Model model) {

        List<BillResponse> bills =
                billingService.getBillsForUser(userDetails.getUsername());

        // Compute total here — Thymeleaf SpEL does NOT support Java stream lambdas
        BigDecimal totalSpent = bills.stream()
                .map(BillResponse::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("bills", bills);
        model.addAttribute("totalSpent", totalSpent);

        return "billing/bill-list";
    }

    /**
     * GET /billing/bills/new
     * Show bill creation form
     */
    @GetMapping("/new")
    public String newBillForm(Model model) {

        model.addAttribute("brands", billingService.getAllActiveBrands2());
        model.addAttribute("billRequest", new CreateBillRequest());

        return "billing/bill-create";
    }

    /**
     * POST /billing/bills/new
     * Save bill and redirect to detail page
     */
    @PostMapping("/new")
    public String saveBill(@Valid @ModelAttribute("billRequest") CreateBillRequest request,
                           BindingResult result,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model,
                           RedirectAttributes ra) {

        if (result.hasErrors()
                || request.getItems() == null
                || request.getItems().isEmpty()) {

            model.addAttribute("brands", billingService.getAllActiveBrands2());
            model.addAttribute("errorMsg", "Add at least one item to the bill.");

            return "billing/bill-create";
        }

        try {
            BillResponse bill =
                    billingService.createBill(request, userDetails.getUsername());

            ra.addFlashAttribute("successMsg",
                    "Bill #" + bill.getBillId()
                            + " saved. Total: ₹" + bill.getGrandTotal());

            return "redirect:/billing/bills/" + bill.getBillId();

        } catch (Exception e) {

            model.addAttribute("brands", billingService.getAllActiveBrands2());
            model.addAttribute("errorMsg", e.getMessage());

            return "billing/bill-create";
        }
    }

    /**
     * GET /billing/bills/{id}
     * Show bill details
     */
    @GetMapping("/{id}")
    public String billDetailPage(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {

        model.addAttribute("bill",
                billingService.getBillById(id, userDetails.getUsername()));

        return "billing/bill-detail";
    }
}
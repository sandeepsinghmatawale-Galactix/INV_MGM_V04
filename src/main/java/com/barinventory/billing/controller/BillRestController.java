package com.barinventory.billing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barinventory.billing.dtos.BillResponse;
import com.barinventory.billing.dtos.CreateBillRequest;
import com.barinventory.billing.service.BillingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Handles BOTH: - REST API → /api/billing/bills/** (returns JSON
 * via @ResponseBody) - Web/HTML → /billing/bills/** (returns Thymeleaf views)
 *
 * Same BillingService, zero duplication.
 */
//@RestController
//@RequestMapping
@RequiredArgsConstructor
public class BillRestController {

	private final BillingService billingService;

	// ═══════════════════════════════════════════════════════════════
	// REST API (/api/billing/bills/**)
	// ═══════════════════════════════════════════════════════════════

	@PostMapping("/api/billing/bills")
	@ResponseBody
	public ResponseEntity<BillResponse> createBillApi(@Valid @RequestBody CreateBillRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(billingService.createBill(request, userDetails.getUsername()));
	}

	@GetMapping("/api/billing/bills")
	@ResponseBody
	public ResponseEntity<List<BillResponse>> myBillsApi(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(billingService.getBillsForUser(userDetails.getUsername()));
	}

	@GetMapping("/api/billing/bills/{id}")
	@ResponseBody
	public ResponseEntity<BillResponse> getBillApi(@PathVariable Long id,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(billingService.getBillById(id, userDetails.getUsername()));
	}

	// ═══════════════════════════════════════════════════════════════
	// WEB / Thymeleaf (/billing/bills/**)
	// ═══════════════════════════════════════════════════════════════

	/** GET /billing/bills → my bills list page */
	@GetMapping("/billing/bills")
	public String billListPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		model.addAttribute("bills", billingService.getBillsForUser(userDetails.getUsername()));
		return "billing/bill-list";
	}

	/** GET /billing/bills/new → bill creation form */
	@GetMapping("/billing/bills/new")
	public String newBillForm(Model model) {
		model.addAttribute("brands", billingService.getAllActiveBrands2());
		model.addAttribute("billRequest", new CreateBillRequest());
		return "billing/bill-create";
	}

	/** POST /billing/bills/new → save bill, redirect to detail */
	@PostMapping("/billing/bills/new")
	public String saveBill(@Valid CreateBillRequest request, BindingResult result,
			@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes ra) {

		if (result.hasErrors() || request.getItems() == null || request.getItems().isEmpty()) {
			model.addAttribute("brands", billingService.getAllActiveBrands2());
			model.addAttribute("errorMsg", "Add at least one item to the bill.");
			return "billing/bill-create";
		}
		try {
			BillResponse bill = billingService.createBill(request, userDetails.getUsername());
			ra.addFlashAttribute("successMsg", "Bill #" + bill.getBillId() + " saved. Total: ₹" + bill.getGrandTotal());
			return "redirect:/billing/bills/" + bill.getBillId();
		} catch (Exception e) {
			model.addAttribute("brands", billingService.getAllActiveBrands2());
			model.addAttribute("errorMsg", e.getMessage());
			return "billing/bill-create";
		}
	}

	/** GET /billing/bills/{id} → bill detail page */
	@GetMapping("/billing/bills/{id}")
	public String billDetailPage(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
		model.addAttribute("bill", billingService.getBillById(id, userDetails.getUsername()));
		return "billing/bill-detail";
	}
}
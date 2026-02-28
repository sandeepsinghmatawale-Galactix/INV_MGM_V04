package com.barinventory.billing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barinventory.billing.dtos.BrandBillingDTO;
import com.barinventory.billing.dtos.BrandDTO;
import com.barinventory.billing.dtos.BrandSizeDTO;
import com.barinventory.billing.entity.Brand;
import com.barinventory.billing.entity.BrandSize;
import com.barinventory.billing.service.BillingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Handles BOTH: - REST API → /api/billing/brands/** (returns JSON
 * via @ResponseBody) - Web/HTML → /billing/brands/** (returns Thymeleaf views)
 *
 * Same BillingService, zero duplication.
 */
@Controller
@RequestMapping
@RequiredArgsConstructor
public class BrandController {

	private final BillingService billingService;

	// ═══════════════════════════════════════════════════════════════
	// REST API (/api/billing/brands/**)
	// ═══════════════════════════════════════════════════════════════

	@PostMapping("/api/billing/brands")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BrandDTO> createApi(@RequestBody BrandDTO dto) {
		return ResponseEntity.ok(billingService.createBrand(dto));
	}

	@PutMapping("/api/billing/brands/{id}")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BrandDTO> updateApi(@PathVariable Long id, @RequestBody BrandDTO dto) {
		return ResponseEntity.ok(billingService.updateBrand(id, dto));
	}

	@DeleteMapping("/api/billing/brands/{id}")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deactivateApi(@PathVariable Long id) {
		billingService.deactivateBrand(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/api/billing/brands")
	@ResponseBody
	public ResponseEntity<List<BrandBillingDTO>> getAllApi() {
		return ResponseEntity.ok(billingService.getAllActiveBrands2());
	}

	@GetMapping("/api/billing/brands/category/{category}")
	@ResponseBody
	public ResponseEntity<List<BrandDTO>> getByCategoryApi(@PathVariable Brand.Category category) {
		return ResponseEntity.ok(billingService.getBrandsByCategory(category));
	}

	// ═══════════════════════════════════════════════════════════════
	// WEB / Thymeleaf (/billing/brands/**)
	// ═══════════════════════════════════════════════════════════════

	/** GET /billing/brands → brand list page */
	@GetMapping("/billing/brands")
	public String brandListPage(Model model) {
		model.addAttribute("brands", billingService.getAllActiveBrands2());
		model.addAttribute("categories", Brand.Category.values());
		return "billing/brand-list";
	}

	/** GET /billing/brands/new → create form */
	@GetMapping("/billing/brands/new")
	@PreAuthorize("hasRole('ADMIN')")
	public String newBrandForm(Model model) {
		model.addAttribute("brand", new BrandDTO());
		model.addAttribute("categories", Brand.Category.values());
		model.addAttribute("pageTitle", "Add New Brand");
		return "billing/brand-form";
	}

	/** POST /billing/brands/new → save and redirect */
	@PostMapping("/billing/brands/new")
	@PreAuthorize("hasRole('ADMIN')")
	public String saveBrand(@Valid @ModelAttribute("brand") BrandDTO dto, BindingResult result, Model model,
			RedirectAttributes ra) {

		if (result.hasErrors()) {
			model.addAttribute("categories", Brand.Category.values());
			model.addAttribute("pageTitle", "Add New Brand");
			return "billing/brand-form";
		}
		try {
			billingService.createBrand(dto);
			ra.addFlashAttribute("successMsg", "Brand '" + dto.getName() + "' created.");
		} catch (Exception e) {
			ra.addFlashAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/billing/brands";
	}

	/** GET /billing/brands/{id}/edit → edit form */
	@GetMapping("/billing/brands/{id}/edit")
	@PreAuthorize("hasRole('ADMIN')")
	public String editBrandForm(@PathVariable Long id, Model model) {
		model.addAttribute("brand", billingService.getBrandById(id));
		model.addAttribute("categories", Brand.Category.values());
		model.addAttribute("pageTitle", "Edit Brand");
		return "billing/brand-form";
	}

	/** POST /billing/brands/{id}/edit → update and redirect */
	@PostMapping("/billing/brands/{id}/edit")
	@PreAuthorize("hasRole('ADMIN')")
	public String updateBrand(@PathVariable Long id, @Valid @ModelAttribute("brand") BrandDTO dto, BindingResult result,
			Model model, RedirectAttributes ra) {

		if (result.hasErrors()) {
			model.addAttribute("categories", Brand.Category.values());
			model.addAttribute("pageTitle", "Edit Brand");
			return "billing/brand-form";
		}
		try {
			billingService.updateBrand(id, dto);
			ra.addFlashAttribute("successMsg", "Brand updated.");
		} catch (Exception e) {
			ra.addFlashAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/billing/brands";
	}

	/** POST /billing/brands/{id}/delete → soft delete */
	@PostMapping("/billing/brands/{id}/delete")
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteBrand(@PathVariable Long id, RedirectAttributes ra) {
		try {
			billingService.deactivateBrand(id);
			ra.addFlashAttribute("successMsg", "Brand deactivated.");
		} catch (Exception e) {
			ra.addFlashAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/billing/brands";
	}

	/** GET /billing/brands/{id}/sizes → size management page */
	@GetMapping("/billing/brands/{id}/sizes")
	@PreAuthorize("hasRole('ADMIN')")
	public String manageSizes(@PathVariable Long id, Model model) {
		model.addAttribute("brand", billingService.getBrandById(id));
		model.addAttribute("newSize", new BrandSizeDTO());
		model.addAttribute("packagings", BrandSize.Packaging.values());
		return "billing/brand-sizes";
	}

	/** POST /billing/brands/{id}/sizes → add a size */
	@PostMapping("/billing/brands/{id}/sizes")
	@PreAuthorize("hasRole('ADMIN')")
	public String addSize(@PathVariable Long id, @ModelAttribute("newSize") BrandSizeDTO dto, RedirectAttributes ra) {
		try {
			billingService.addSizeToBrand(id, dto);
			ra.addFlashAttribute("successMsg", "Size added.");
		} catch (Exception e) {
			ra.addFlashAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/billing/brands/" + id + "/sizes";
	}

	/** POST /billing/brands/{brandId}/sizes/{sizeId}/delete → remove a size */
	@PostMapping("/billing/brands/{brandId}/sizes/{sizeId}/delete")
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteSize(@PathVariable Long brandId, @PathVariable Long sizeId, RedirectAttributes ra) {
		try {
			billingService.deactivateSize(sizeId);
			ra.addFlashAttribute("successMsg", "Size removed.");
		} catch (Exception e) {
			ra.addFlashAttribute("errorMsg", e.getMessage());
		}
		return "redirect:/billing/brands/" + brandId + "/sizes";
	}
}
package com.barinventory.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.barinventory.config.WellConfig;
import com.barinventory.entity.*;
import com.barinventory.repository.BarRepository;
import com.barinventory.repository.InventorySessionRepository;
import com.barinventory.repository.UserRepository;
import com.barinventory.service.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final BarService barService;
    private final ProductService productService;
    private final PricingService pricingService;
    private final InventorySessionService sessionService;
    private final ReportService reportService;
    private final InventorySessionRepository sessionRepo;
    private final UserRepository userRepository;
    private final BarRepository barRepository;

    // ================= LOGIN =================

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // ================= ROOT REDIRECT =================

    @GetMapping("/")
    public String redirectToDashboard(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    // ================= DASHBOARD =================

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {

        User currentUser = (User) authentication.getPrincipal();

        model.addAttribute("user", currentUser);
        model.addAttribute("username", currentUser.getName());
        model.addAttribute("role", currentUser.getRole());

        if (currentUser.getRole() == Role.ADMIN) {

            List<Bar> bars = barRepository.findAll();
            model.addAttribute("bars", bars);
            model.addAttribute("totalBars", bars.size());
            model.addAttribute("totalUsers", userRepository.count());

        } else {

            Long barId = currentUser.getBarId();
            if (barId != null) {
                Bar bar = barRepository.findById(barId).orElse(null);
                model.addAttribute("bar", bar);
                model.addAttribute("barId", barId);

                if (currentUser.getRole() == Role.BAR_OWNER) {
                    long staffCount =
                            userRepository.countByBar_BarIdAndRole(barId, Role.BAR_STAFF);
                    model.addAttribute("staffCount", staffCount);
                }
            }
        }

        return "dashboard";
    }

    // ================= BARS =================

    @GetMapping("/bars")
    public String listBars(Model model) {
        model.addAttribute("bars", barService.getAllActiveBars());
        return "list";
    }

    @GetMapping("/bars/new")
    public String newBarForm() {
        return "form";
    }

    @PostMapping("/bars/new")
    public String createBar(@RequestParam String barName,
                            @RequestParam String location,
                            @RequestParam(required = false) String contactNumber,
                            @RequestParam(required = false) String ownerName) {

        Bar bar = Bar.builder()
                .barName(barName)
                .location(location)
                .contactNumber(contactNumber)
                .ownerName(ownerName)
                .active(true)
                .build();

        barService.createBar(bar);
        return "redirect:/dashboard";
    }

    // ================= PRODUCTS =================

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllActiveProducts());
        return "list";
    }

    @GetMapping("/products/new")
    public String newProductForm() {
        return "productNew";
    }

    @PostMapping("/products/new")
    public String createProduct(@RequestParam String productName,
                                @RequestParam String category,
                                @RequestParam(required = false) String brand,
                                @RequestParam(required = false) String volumeML,
                                @RequestParam String unit) {

        Product product = Product.builder()
                .productName(productName)
                .category(category)
                .brand(brand)
                .volumeML(volumeML != null && !volumeML.isEmpty()
                        ? new BigDecimal(volumeML)
                        : null)
                .unit(unit)
                .active(true)
                .build();

        productService.createProduct(product);
        return "redirect:/products";
    }

    // ================= REPORTS =================

    @GetMapping("/reports/{barId}/daily")
    public String dailyReport(@PathVariable Long barId,
                              @RequestParam(required = false) String date,
                              Model model) {

        LocalDateTime reportDate =
                date != null ? LocalDateTime.parse(date) : LocalDateTime.now();

        model.addAttribute("bar", barService.getBarById(barId));
        model.addAttribute("report",
                reportService.getDailySalesReport(barId, reportDate));

        return "reports/daily";
    }

    // ================= REST INITIALIZE =================

    @PostMapping("/initialize")
    public ResponseEntity<InventorySessionDTO> initializeSession(
            @RequestParam Long barId,
            @RequestParam String shiftType,
            @RequestParam String notes) {

        InventorySession inv =
                sessionService.initializeSession(barId, shiftType, notes);

        InventorySessionDTO dto = InventorySessionDTO.builder()
                .sessionId(inv.getSessionId())
                .barId(inv.getBar().getBarId())
                .barName(inv.getBar().getBarName())
                .sessionStartTime(inv.getSessionStartTime())
                .status(inv.getStatus())
                .shiftType(inv.getShiftType())
                .notes(inv.getNotes())
                .build();

        return ResponseEntity.ok(dto);
    }

    // ================= HELPER =================

    private BigDecimal parseDecimal(String value) {
        return (value != null && !value.isEmpty())
                ? new BigDecimal(value)
                : BigDecimal.ZERO;
    }
}
package com.Grocery.controller;

import com.Grocery.model.category;
import com.Grocery.model.grocery;
import com.Grocery.model.order;
import com.Grocery.model.orderItem;
import com.Grocery.model.service;
import com.Grocery.model.teamMember;
import com.Grocery.model.user;

import com.Grocery.repository.cartRepo;
import com.Grocery.repository.categoryRepo;
import com.Grocery.repository.groceryRepo;
import com.Grocery.repository.orderRepo;
import com.Grocery.repository.serviceRepo;
import com.Grocery.repository.teamRepo;
import com.Grocery.repository.userRepo;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class adminController {

    @Autowired
    private categoryRepo categoryRepo;

    @Autowired
    private groceryRepo groceryRepo;

    @Autowired
    private cartRepo cartRepo;

    @Autowired
    private serviceRepo serviceRepo;

    @Autowired
    private teamRepo teamRepo;

    @Autowired
    private orderRepo orderRepo;

    @Autowired
    private userRepo userRepo;

    // ------------------ Dashboard --------------------
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<category> categories = categoryRepo.findAll();
        List<grocery> groceries = groceryRepo.findAll();
        List<order> orders = orderRepo.findByStatus("Pending"); // ✅ Pending only

        model.addAttribute("categories", categories);
        model.addAttribute("groceries", groceries);
        model.addAttribute("orders", orders); // ✅ Send to template
        return "adminDashboard";
    }

    // ------------------ Add Category ------------------
    @GetMapping("/category/new")
    public String showCategoryForm(Model model) {
        model.addAttribute("category", new category());
        return "addCategory";
    }

    @PostMapping("/category/save")
    public String saveCategory(@RequestParam("name") String name,
                               @RequestParam("imageFile") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String uploadDir = "uploads/categories/";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                Files.write(filePath, file.getBytes());

                category cat = new category();
                cat.setName(name);
                cat.setImage(fileName);
                categoryRepo.save(cat);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/categories";
    }

    // ------------------ Add Grocery ------------------
    @GetMapping("/grocery/new")
    public String showGroceryForm(Model model) {
        model.addAttribute("grocery", new grocery());
        model.addAttribute("categories", categoryRepo.findByIsDeleteFalse());
        return "addGrocery";
    }

    @PostMapping("/grocery/save")
    public String saveGrocery(@ModelAttribute grocery g,
                              @RequestParam("imageFile") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String uploadDir = "uploads/groceries/";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                Files.write(filePath, file.getBytes());

                g.setImage(fileName);
            }

            groceryRepo.save(g);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/groceries";
    }
    
    

    @PostMapping("/grocery/update")
    public String updateGrocery(@ModelAttribute grocery g,
                                @RequestParam("imageFile") MultipartFile file) {
        try {
            grocery existing = groceryRepo.findById(g.getId()).orElse(null);
            if (existing == null) return "redirect:/admin/groceries";

            // Update fields
            existing.setName(g.getName());
            existing.setPrice(g.getPrice());
            existing.setDiscountPercent(g.getDiscountPercent());
            existing.setCategory(g.getCategory());

            // Save new image only if uploaded
            if (file != null && !file.isEmpty()) {
                String uploadDir = "uploads/groceries/";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);
                Files.write(filePath, file.getBytes());

                existing.setImage(fileName);
            }

            groceryRepo.save(existing);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/admin/groceries";
    }

    
    // ------------------ Delete Category ------------------
    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        Optional<category> c = categoryRepo.findById(id);
        if (c.isPresent()) {
            category cat = c.get();
            cat.setDelete(true);
            categoryRepo.save(cat);
        }
        return "redirect:/admin/categories";
    }
    
    @PostMapping("/category/update")
    public String updateCategory(@RequestParam("id") Long id,
                                 @RequestParam("name") String name,
                                 @RequestParam("imageFile") MultipartFile file) {
        Optional<category> optionalCategory = categoryRepo.findById(id);
        if (optionalCategory.isPresent()) {
            category cat = optionalCategory.get();
            cat.setName(name);

            // Update image only if new file is uploaded
            if (file != null && !file.isEmpty()) {
                try {
                    String uploadDir = "uploads/categories/";
                    File folder = new File(uploadDir);
                    if (!folder.exists()) folder.mkdirs();

                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.write(filePath, file.getBytes());

                    cat.setImage(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            categoryRepo.save(cat);
        }
        return "redirect:/admin/categories";
    }

    // ------------------ Delete Grocery ------------------
    @GetMapping("/grocery/delete/{id}")
    public String deleteGrocery(@PathVariable Long id) {
        Optional<grocery> g = groceryRepo.findById(id);
        if (g.isPresent()) {
            grocery item = g.get();
            item.setDelete(true);
            groceryRepo.save(item);
        }
        return "redirect:/admin/groceries";
    }

    // ======================= 🔗 CATEGORY & GROCERY SEPARATE PAGES ======================
    @GetMapping("/categories")
    public String showAdminCategories(Model model) {
        model.addAttribute("categories", categoryRepo.findByIsDeleteFalse());
        return "adminCategories";
    }

    @GetMapping("/groceries")
    public String showAdminGroceries(Model model) {
        model.addAttribute("groceries", groceryRepo.findByDeleteFalseAndCategoryIsDeleteFalse());
        model.addAttribute("categories", categoryRepo.findByIsDeleteFalse());
        return "adminGroceries";
    }

    
    @GetMapping("/orders")
    public String viewOrders(Model model) {
        // ✅ Fetch only confirmed orders
        List<order> orders = orderRepo.findByStatus("Confirmed");
        List<category> categories = categoryRepo.findAll();
        List<grocery> groceries = groceryRepo.findAll();

        // Map: orderId -> (productName -> quantity)
        Map<String, Map<String, Integer>> ordersProductMap = new HashMap<>();

        for (order ord : orders) {
            String orderId = ord.getOrderId();
            ordersProductMap.putIfAbsent(orderId, new HashMap<>());
            Map<String, Integer> productMap = ordersProductMap.get(orderId);

            // ✅ Loop through orderItems instead of productName in order table
            if (ord.getOrderItems() != null) {
                for (orderItem oi : ord.getOrderItems()) {
                    String product = oi.getGroceryItem().getName();
                    int qty = oi.getQuantity();

                    productMap.put(product, productMap.getOrDefault(product, 0) + qty);
                }
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("categories", categories);
        model.addAttribute("groceries", groceries);
        model.addAttribute("ordersProductMap", ordersProductMap);

        return "adminOrders";
    }

    
    @PostMapping("/confirmOrder")
    public String confirmOrder(@RequestParam Long id) {
        order order = orderRepo.findById(id).orElse(null);
        if (order != null) {
            order.setStatus("Confirmed");
            
            orderRepo.save(order);
        }
        return "redirect:/admin/orders"; // Reloads the dashboard with updated table
    }



    // ======================= 🟣 USERS ======================
    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "adminUser";
    }
}
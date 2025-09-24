package com.Grocery.controller;

import com.Grocery.model.category;
import com.Grocery.model.grocery;
import com.Grocery.model.order;
import com.Grocery.model.orderItem;
import com.Grocery.model.service;
import com.Grocery.model.user;
import com.Grocery.repository.adminRepo;
import com.Grocery.repository.cartRepo;
import com.Grocery.repository.categoryRepo;
import com.Grocery.repository.groceryRepo;
import com.Grocery.repository.orderRepo;
import com.Grocery.repository.serviceRepo;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
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
	private orderRepo orderRepo;

	@Autowired
	private userRepo userRepo;

	@Autowired
	private adminRepo adminRepo;

	// ------------------ Dashboard --------------------
	@GetMapping("/dashboard")
	public String adminDashboard(HttpServletRequest request, Model model) {
		// ✅ Check if admin is logged in
		if (request.getSession().getAttribute("adminUser") == null) {
			return "redirect:/admin/login"; // Redirect to login page
		}

		// ✅ Fetch all categories and groceries
		List<category> categories = categoryRepo.findAll();
		List<grocery> groceries = groceryRepo.findAll();

		// ✅ Fetch only pending orders
		List<order> orders = orderRepo.findByStatus("Pending");

		// ✅ Add data to the model
		model.addAttribute("categories", categories);
		model.addAttribute("groceries", groceries);
		model.addAttribute("orders", orders);

		return "adminDashboard"; // ✅ Show admin dashboard
	}

	// ------------------ Add Category ------------------
	@GetMapping("/category/new")
	public String showCategoryForm(Model model) {
		model.addAttribute("category", new category());
		return "addCategory";
	}

	@PostMapping("/category/save")
	public String saveCategory(@RequestParam("name") String name, @RequestParam("imageFile") MultipartFile file) {
		try {
			if (file != null && !file.isEmpty()) {
				String uploadDir = "uploads/categories/";
				File folder = new File(uploadDir);
				if (!folder.exists())
					folder.mkdirs();

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
	public String saveGrocery(@ModelAttribute grocery g, @RequestParam("imageFile") MultipartFile file) {
		try {
			if (file != null && !file.isEmpty()) {
				String uploadDir = "uploads/groceries/";
				File folder = new File(uploadDir);
				if (!folder.exists())
					folder.mkdirs();

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
	public String updateGrocery(@ModelAttribute grocery g, @RequestParam("imageFile") MultipartFile file) {
		try {
			grocery existing = groceryRepo.findById(g.getId()).orElse(null);
			if (existing == null)
				return "redirect:/admin/groceries";

			// Update fields
			existing.setName(g.getName());
			existing.setPrice(g.getPrice());
			existing.setDiscountPercent(g.getDiscountPercent());
			existing.setCategory(g.getCategory());

			// Save new image only if uploaded
			if (file != null && !file.isEmpty()) {
				String uploadDir = "uploads/groceries/";
				File folder = new File(uploadDir);
				if (!folder.exists())
					folder.mkdirs();

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
	public String updateCategory(@RequestParam("id") Long id, @RequestParam("name") String name,
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
					if (!folder.exists())
						folder.mkdirs();

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

	// ======================= 🔗 CATEGORY & GROCERY SEPARATE PAGES
	// ======================
	@GetMapping("/categories")
	public String showAdminCategories(@RequestParam(required = false) String search, // search by name
			@RequestParam(required = false) String dateFilter, // today, yesterday, etc.
			@RequestParam(required = false) String startDate, // custom start
			@RequestParam(required = false) String endDate, // custom end
			@RequestParam(required = false, defaultValue = "name") String sortBy, // sort field
			@RequestParam(required = false, defaultValue = "asc") String order, // asc/desc
			Model model) {

		List<category> categories = categoryRepo.findByIsDeleteFalse();

		// ✅ Search by name
		if (search != null && !search.isEmpty()) {
			categories = categories.stream().filter(c -> c.getName().toLowerCase().contains(search.toLowerCase()))
					.toList();
		}

		// ✅ Handle predefined date filters
		LocalDate today = LocalDate.now();
		if (dateFilter != null && !dateFilter.isEmpty()) {
			switch (dateFilter) {
			case "today" -> {
				categories = categories.stream().filter(c -> today.equals(c.getCreatedDate())).toList();
			}
			case "yesterday" -> {
				LocalDate yesterday = today.minusDays(1);
				categories = categories.stream().filter(c -> yesterday.equals(c.getCreatedDate())).toList();
			}
			case "thisWeek" -> {
				LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
				LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);
				categories = categories.stream().filter(c -> c.getCreatedDate() != null
						&& !c.getCreatedDate().isBefore(startOfWeek) && !c.getCreatedDate().isAfter(endOfWeek))
						.toList();
			}
			case "lastWeek" -> {
				LocalDate startOfLastWeek = today.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
				LocalDate endOfLastWeek = today.minusWeeks(1).with(java.time.DayOfWeek.SUNDAY);
				categories = categories.stream().filter(c -> c.getCreatedDate() != null
						&& !c.getCreatedDate().isBefore(startOfLastWeek) && !c.getCreatedDate().isAfter(endOfLastWeek))
						.toList();
			}
			case "thisMonth" -> {
				LocalDate startOfMonth = today.withDayOfMonth(1);
				LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
				categories = categories.stream().filter(c -> c.getCreatedDate() != null
						&& !c.getCreatedDate().isBefore(startOfMonth) && !c.getCreatedDate().isAfter(endOfMonth))
						.toList();
			}
			case "lastMonth" -> {
				LocalDate startOfLastMonth = today.minusMonths(1).withDayOfMonth(1);
				LocalDate endOfLastMonth = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());
				categories = categories.stream()
						.filter(c -> c.getCreatedDate() != null && !c.getCreatedDate().isBefore(startOfLastMonth)
								&& !c.getCreatedDate().isAfter(endOfLastMonth))
						.toList();
			}
			case "thisYear" -> {
				LocalDate startOfYear = today.withDayOfYear(1);
				LocalDate endOfYear = today.withDayOfYear(today.lengthOfYear());
				categories = categories.stream().filter(c -> c.getCreatedDate() != null
						&& !c.getCreatedDate().isBefore(startOfYear) && !c.getCreatedDate().isAfter(endOfYear))
						.toList();
			}
			case "lastYear" -> {
				LocalDate startOfLastYear = today.minusYears(1).withDayOfYear(1);
				LocalDate endOfLastYear = today.minusYears(1).withDayOfYear(today.minusYears(1).lengthOfYear());
				categories = categories.stream().filter(c -> c.getCreatedDate() != null
						&& !c.getCreatedDate().isBefore(startOfLastYear) && !c.getCreatedDate().isAfter(endOfLastYear))
						.toList();
			}

			}
		}

		// ✅ Handle Custom Date Range
		if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
			LocalDate start = LocalDate.parse(startDate);
			LocalDate end = LocalDate.parse(endDate);

			categories = categories.stream().filter(c -> c.getCreatedDate() != null
					&& !c.getCreatedDate().isBefore(start) && !c.getCreatedDate().isAfter(end)).toList();
		}

		// ✅ Sorting with null-safe handling
		categories = categories.stream().sorted((c1, c2) -> {
			if ("createdDate".equals(sortBy)) {
				LocalDate d1 = c1.getCreatedDate();
				LocalDate d2 = c2.getCreatedDate();

				if (d1 == null && d2 == null)
					return 0;
				if (d1 == null)
					return order.equals("asc") ? -1 : 1;
				if (d2 == null)
					return order.equals("asc") ? 1 : -1;

				return order.equals("asc") ? d1.compareTo(d2) : d2.compareTo(d1);
			} else { // default sort by name
				return order.equals("asc") ? c1.getName().compareToIgnoreCase(c2.getName())
						: c2.getName().compareToIgnoreCase(c1.getName());
			}
		}).toList();

		// ✅ Send data + filters back to frontend
		model.addAttribute("categories", categories);
		model.addAttribute("search", search);
		model.addAttribute("dateFilter", dateFilter);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("order", order);

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

	// ------------------ Admin Login ------------------
	@GetMapping("/login")
	public String showLoginPage() {
		return "adminLogin"; // thymeleaf page name
	}

	@PostMapping("/login")
	public String processLogin(@RequestParam String username, @RequestParam String password, HttpServletRequest request,
			Model model) {
		// ✅ Check admin from DB
		com.Grocery.model.admin admin = adminRepo.findByUsernameAndPassword(username, password);

		if (admin != null) {
			// ✅ Store admin in session
			request.getSession().setAttribute("adminUser", admin);
			return "redirect:/admin/dashboard"; // redirect to dashboard
		} else {
			// ❌ Invalid login → send back to login page with error
			model.addAttribute("error", "Invalid username or password");
			return "adminLogin";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/admin/login";
	}
}
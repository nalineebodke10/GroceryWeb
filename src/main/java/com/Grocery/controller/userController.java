package com.Grocery.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.Grocery.model.user;
import com.Grocery.model.category;
import com.Grocery.model.grocery;
import com.Grocery.model.order;
import com.Grocery.model.orderItem;
import com.Grocery.model.cart;

import com.Grocery.repository.userRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import com.Grocery.repository.categoryRepo;
import com.Grocery.repository.groceryRepo;
import com.Grocery.repository.orderRepo;
import com.Grocery.repository.orderItemRepo;
import com.Grocery.repository.cartRepo;

import com.Grocery.repository.serviceRepo;
import com.Grocery.repository.teamRepo;

@Controller
@RequestMapping("/user")
public class userController {

    @Autowired
    private userRepo repo;

    @Autowired
    private categoryRepo categoryRepo;

    @Autowired
    private groceryRepo groceryRepo;
    
    @Autowired
    private orderItemRepo orderItemRepo;

    @Autowired
    private cartRepo cartRepo;

    @Autowired
    private serviceRepo serviceRepo;
    
    @Autowired
    private orderRepo orderRepo;

    @Autowired
    private teamRepo teamRepo;

    // ----------- Registration Page -----------
    @GetMapping("/register")
    public String showUserForm(Model model) {
        model.addAttribute("user", new user());
        return "registration";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("user") user u) {       
        repo.save(u); 
        return "redirect:/user/login";
    }

    // ----------- Login Page -----------
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String mobile,
                            @RequestParam String password,
                            Model model, HttpSession session) {
        user existingUser = repo.findByMobileAndPassword(mobile, password);
        if (existingUser != null) {
            session.setAttribute("loginUser", existingUser);
            return "redirect:/user/home";
        } else {
            model.addAttribute("error", "Invalid credentials!");
            return "login";
        }
    }

    // ----------- Home Page with Categories -----------
    @GetMapping("/home")
    public String userHome(HttpSession session, Model model) {
        List<category> categories = categoryRepo.findByIsDeleteFalse();
        model.addAttribute("categories", categories != null ? categories : new ArrayList<>());

        List<grocery> topOffers = groceryRepo.findTop8ByDeleteFalseAndCategoryIsDeleteFalseOrderByDiscountPercentDesc();
        model.addAttribute("topOffers", topOffers != null ? topOffers : new ArrayList<>());

        user loggedInUser = (user) session.getAttribute("loginUser");
        int cartCount = 0;
        if (loggedInUser != null) {
            List<cart> userCart = cartRepo.findByUserId(loggedInUser.getId());
            cartCount = userCart != null ? userCart.size() : 0;
        }
        model.addAttribute("cartCount", cartCount);

        return "home";
    }


    // ----------- Show Grocery Items by Category -----------
    @GetMapping("/category/{id}/items")
    public String showItemsByCategory(@PathVariable Long id,
                                      HttpSession session,
                                      Model model) {

        List<grocery> items = groceryRepo.findByCategoryIdAndDeleteFalseAndCategoryIsDeleteFalse(id);
        model.addAttribute("groceryItems", items);

        user loggedInUser = (user) session.getAttribute("loginUser");
        if (loggedInUser != null) {
            List<cart> cartItems = cartRepo.findByUserId(loggedInUser.getId());
            model.addAttribute("cartCount", cartItems.size());               }

        return "groceryItems";
    }

    // ----------- Add to Cart -----------
    @PostMapping("/addToCart")
    public String addToCart(@RequestParam Long groceryId,
                            @RequestParam int quantity,
                            @RequestParam(required = false) String redirectUrl,
                            HttpSession session) {

        user loggedInUser = (user) session.getAttribute("loginUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        grocery item = groceryRepo.findById(groceryId).orElse(null);

        if (item != null) {
            cart existingCartItem = cartRepo.findByGroceryItemIdAndUser(groceryId, loggedInUser);

            if (existingCartItem != null) {
                existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
                double subtotal = existingCartItem.getQuantity() * existingCartItem.getGroceryItem().getPrice();
                existingCartItem.setSubtotal(subtotal);
                cartRepo.save(existingCartItem);
            } else {
                cart newCart = new cart();
                newCart.setGroceryItem(item);
                newCart.setUser(loggedInUser);  // ✅ using user object
                newCart.setQuantity(quantity);
                newCart.setSubtotal(item.getPrice() * quantity);
                cartRepo.save(newCart);
            }
        }

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return "redirect:" + redirectUrl;
        }

        return "redirect:/user/home";
    }

    // ----------- Show Cart -----------
    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        user loggedInUser = (user) session.getAttribute("loginUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        String mobile = loggedInUser.getMobile();
        user u=(user) session.getAttribute("loginUser");
        List<cart> cartItems = cartRepo.findByUserId(u.getId());

        double totalPrice = 0;
        for (cart item : cartItems) {
            if (item != null && item.getGroceryItem() != null) {
                totalPrice += item.getGroceryItem().getPrice() * item.getQuantity();
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

   
        return "cartItems";
    }
    
    // ----------- Update Cart Quantity -----------
    @PostMapping("/cart/updateQuantity")
    public String updateCartQuantity(@RequestParam("cartId") List<Long> cartIds,
                                     @RequestParam("quantity") List<Integer> quantities,
                                     HttpSession session,
                                     Model model) {

        user loggedInUser = (user) session.getAttribute("loginUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        String mobile = loggedInUser.getMobile();

        for (int i = 0; i < cartIds.size(); i++) {
            Long id = cartIds.get(i);
            int qty = quantities.get(i);

            cart cartItem = cartRepo.findById(id).orElse(null);
            if (cartItem != null) {
                cartItem.setQuantity(qty);
                cartItem.setSubtotal(cartItem.getGroceryItem().getPrice() * qty);
                cartRepo.save(cartItem);
            }
        }

        user u=(user) session.getAttribute("loginUser");
        List<cart> cartItems = cartRepo.findByUserId(u.getId());
        double totalPrice = 0;
        for (cart item : cartItems) {
            totalPrice += item.getGroceryItem().getPrice() * item.getQuantity();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cartItems";
    }

    // ----------- Remove from Cart -----------
    @GetMapping("/cart/remove")
    public String removeCartItem(@RequestParam Long id,
                                 HttpSession session) {
        user loggedInUser = (user) session.getAttribute("loginUser");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        cartRepo.deleteById(id);
        return "redirect:/user/cart";
    }

    // ----------- Contact Us Page -----------
    @GetMapping("/contact")
    public String contactForm() {
        return "contact";
    }

    // ----------- About Us Page -----------
    @GetMapping("/about")
    public String showAboutPage(Model model) {
        model.addAttribute("services", serviceRepo.findAll());
        model.addAttribute("team", teamRepo.findAll());
        return "about";
    }

    // ----------- Default Redirect -----------
    @GetMapping("/")
    public String indexPage() {
        return "redirect:/user/login";
    }
    @Transactional

    @PostMapping("/checkout")
    public String placeOrder(HttpSession session, Model model) {

        user u = (user) session.getAttribute("loginUser");
        if (u == null) {
            return "redirect:/user/home";
        }

        List<cart> cartItems = cartRepo.findByUserId(u.getId());
        if (cartItems == null || cartItems.isEmpty()) {
            // no items in cart -> redirect or show message
            return "redirect:/cart";
        }

        // calculate total
        double total = cartItems.stream().mapToDouble(cart::getSubtotal).sum();

        // create Order (no productName / quantity here)
        order o = new order();
        o.setOrderId("ORD" + new Random().nextInt(10000));
        o.setCustomerName(u.getUserName());
        o.setTotalAmount(total);
        o.setDate(LocalDate.now());
        o.setStatus("Pending");
        o.setUser(u); // link user if needed

        // save order first to get generated ID
        order savedOrder = orderRepo.save(o);

        for (cart c : cartItems) {
            orderItem oi = new orderItem();
            oi.setGroceryItem(c.getGroceryItem());
            oi.setQuantity(c.getQuantity());
            oi.setOrder(savedOrder);
            orderItemRepo.save(oi);
        }

        // clear user's cart
        cartRepo.deleteByUserId(u.getId());

        model.addAttribute("order", savedOrder);
        return "orderConfirmation";
    }


}

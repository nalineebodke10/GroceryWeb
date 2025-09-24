package com.Grocery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/contact")
    public String contactForm() {
        return "contact"; // your Thymeleaf template name
    }

    @PostMapping("/contact")
    public String submitContactForm(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam String message,
                                    Model model) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("your_email@gmail.com"); // replace with your email
            mailMessage.setSubject("New Contact Form Message");
            mailMessage.setText("Name: " + name + "\nEmail: " + email + "\nMessage: " + message);

            mailSender.send(mailMessage);

            model.addAttribute("successMsg", "Message sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMsg", "Error sending message. Please try again later.");
        }

        return "contact"; // reload form page
    }
}

package com.Grocery;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		// ✅ Serve product images correctly
		registry.addResourceHandler("/GroceryWeb/uploads/groceries/**").addResourceLocations("file:uploads/groceries/");

		// ✅ Serve category images correctly
		registry.addResourceHandler("/GroceryWeb/uploads/categories/**")
				.addResourceLocations("file:uploads/categories/");
	}
}

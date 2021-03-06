package com.app.controller;

import com.app.model.Cart;
import com.app.model.Product;
import com.app.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@SessionAttributes("cart")
public class ProductController {
    @Autowired
    private IProductService productService;

    @ModelAttribute("cart")
    public Cart setupCart() {
        return new Cart();
    }

    @GetMapping("/shop")
    public ModelAndView showShop() {
        ModelAndView modelAndView = new ModelAndView("/shop");
        modelAndView.addObject("products", productService.findAll());
        return modelAndView;
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, @ModelAttribute Cart cart, @RequestParam("action") String action, Model model) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return "/error.404";
        }
        if (action.equals("addquantity")) {
            cart.addProduct(productOptional.get());
            return "redirect:/shopping-cart";
        }
        if(action.equals("show")){
            model.addAttribute("product",productService.findById(id).get());
            return "/show";
        }
        if(action.equals("delete")){
            cart.deleteProduct(productOptional.get());
            System.out.println(id);
            System.out.println(productOptional.get());
            return "redirect:/shopping-cart";
        }
        cart.addProduct(productOptional.get());
        return "redirect:/shop";
    }

    @GetMapping("/pay")
    public String pay(@ModelAttribute Cart cart, RedirectAttributes redirectAttributes){
        double total = cart.countTotalPayment();
        cart.getProducts().clear();
        redirectAttributes.addFlashAttribute("total",total);
        return "redirect:/shopping-cart";
    }
}
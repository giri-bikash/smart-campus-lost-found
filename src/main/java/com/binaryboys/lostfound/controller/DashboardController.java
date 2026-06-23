package com.binaryboys.lostfound.controller;

import com.binaryboys.lostfound.model.Item;
import com.binaryboys.lostfound.model.User;
import com.binaryboys.lostfound.repository.ItemRepository;
import com.binaryboys.lostfound.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final UserService userService;
    private final ItemRepository itemRepository;

    public DashboardController(UserService userService, ItemRepository itemRepository) {
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Item> allItems = itemRepository.findByUserId(user.getId());

        List<Item> lostItems = allItems.stream()
            .filter(i -> i.getType() == Item.ItemType.LOST)
            .collect(Collectors.toList());

        List<Item> foundItems = allItems.stream()
            .filter(i -> i.getType() == Item.ItemType.FOUND)
            .collect(Collectors.toList());

        long resolvedCount = allItems.stream()
            .filter(i -> i.getStatus() == Item.Status.RESOLVED)
            .count();

        model.addAttribute("lostItems", lostItems);
        model.addAttribute("foundItems", foundItems);
        model.addAttribute("lostCount", lostItems.size());
        model.addAttribute("foundCount", foundItems.size());
        model.addAttribute("resolvedCount", resolvedCount);

        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
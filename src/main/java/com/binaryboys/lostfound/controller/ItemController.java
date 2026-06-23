package com.binaryboys.lostfound.controller;

import com.binaryboys.lostfound.model.Item;
import com.binaryboys.lostfound.model.User;
import com.binaryboys.lostfound.repository.ItemRepository;
import com.binaryboys.lostfound.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class ItemController {

    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemController(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    // ── Show post lost form ───────────────────────────
    @GetMapping("/items/lost/new")
    public String newLostItem(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("itemType", "LOST");
        return "items/post-item";
    }

    // ── Show post found form ──────────────────────────
    @GetMapping("/items/found/new")
    public String newFoundItem(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("itemType", "FOUND");
        return "items/post-item";
    }

    // ── Save item ─────────────────────────────────────
    @PostMapping("/items/save")
    public String saveItem(@Valid @ModelAttribute("item") Item item,
                           BindingResult result,
                           @RequestParam("photo") MultipartFile photo,
                           @RequestParam("type") String type,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {

        item.setType(Item.ItemType.valueOf(type));

        if (result.hasErrors()) {
            model.addAttribute("itemType", type);
            return "items/post-item";
        }

        // Handle photo upload
        if (!photo.isEmpty()) {
            try {
                String uploadDir = "uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                Path path = Paths.get(uploadDir + filename);
                Files.write(path, photo.getBytes());
                item.setPhotoUrl(filename);
            } catch (Exception e) {
                // Photo upload failed — continue without photo
            }
        }

        // Set logged-in user
        User user = userService.findByEmail(userDetails.getUsername());
        item.setUser(user);
        item.setStatus(Item.Status.OPEN);

        itemRepository.save(item);

        return "redirect:/dashboard?success=true";
    }

    // ── Browse found items ────────────────────────────
    @GetMapping("/items")
    public String browseItems(Model model) {
        model.addAttribute("items",
            itemRepository.findByTypeAndStatus(
                Item.ItemType.FOUND, Item.Status.OPEN));
        return "items/browse";
    }
}
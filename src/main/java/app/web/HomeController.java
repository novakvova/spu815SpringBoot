package app.web;

import app.entities.User;
import app.repositories.UserRepository;
import app.seeder.SeederDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.*;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeederDb seederDb;

    @Autowired
    public HomeController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          SeederDb seederDb) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seederDb=seederDb;
    }

    @GetMapping("/")
    public String index(Model model) {
        seederDb.SeedAllTabels();
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "create";
    }

    @PostMapping("/create")
    public String create(@Valid User user, BindingResult result, Model model) {
        if(result.hasErrors())
            return "create";

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/";
    }
}

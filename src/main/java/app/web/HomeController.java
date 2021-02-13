package app.web;

import app.dto.FindUserDTO;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public String index(FindUserDTO find, Model model) {
        seederDb.SeedAllTabels();
        Stream<User> stream = userRepository.findAll().stream();
        System.out.println("-----Begin Query-----");
        if(find.getName()!=null) {
            stream = stream.filter(u->u.getName().startsWith(find.getName()));
        }
        if(find.getEmail()!=null) {
            stream = stream.filter(u->u.getEmail().startsWith(find.getEmail()));
        }
        stream = stream.sorted(Comparator.comparing(User::getName));
        //stream = stream.skip(2);
        //stream = stream.limit(2);
        System.out.println("-----Run Query-----");
        List<User> users = stream.collect(Collectors.toList());//userRepository.findAll();
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

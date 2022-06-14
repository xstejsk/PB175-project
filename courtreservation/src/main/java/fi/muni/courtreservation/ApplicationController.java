package fi.muni.courtreservation;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Radim Stejskal 514102
 */
@Controller
public class ApplicationController {

    /**
     *
     * @param model MVC model
     * @return view template of the homepage, adjusts navbar based on user role
     */
    @GetMapping("/index")
    public String homePage(Model model){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            model.addAttribute("isLoggedIn", true);
            Set<String> roles = ((UserDetails) principal).getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            if (roles.contains("ADMIN")){
                model.addAttribute("role", "ADMIN");
            }

        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "index";
    }

    /**
     *
     * @return homepage view template
     */
    @GetMapping("")
    public String redirectToHomePage(){
        return "redirect:/index";
    }

    /**
     *
     * @return login view template
     */
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    /**
     *
     * @return logout view template
     */
    @GetMapping("/logout")
    public String logout(){
        return "login";
    }


}

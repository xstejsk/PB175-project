package fi.muni.courtreservation.user;

import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author Radim Stejskal 514102
 */
@Controller
@RequestMapping(path = "users")
public class UserController{

    private final UserService userService;
    private User userToUpdate = new User();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Initialises and adds attributes to the MVC model
     * @param model MVC model
     */
    @ModelAttribute
    public void addAttributes(Model model){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) userService.loadUserByUsername(userName);
        model.addAttribute("userRole", user.getUserRole().name());
        model.addAttribute("reservationToUpdate", userToUpdate);
    }

    /**
     *
     * @param model MVC model
     * @return view template containing all users
     */
    @GetMapping("/all")
    public String getAllPagesOfAllUsers(Model model){
        return getOnePageOfAllUsers(model, 1, null);
    }

    /**
     *
     * @param model MVC model
     * @param currentPage page number to be shown
     * @param keyword substring of user email
     * @return view template containing details of users that match with the keyword, empty keyword
     * matches all users
     */
    @GetMapping("/all/{pageNumber}")
    public String getOnePageOfAllUsers(Model model, @PathVariable("pageNumber") int currentPage, String keyword){
        if (keyword == null || keyword.isEmpty()){
            Page<User> page = userService.getPageOfAllUsers(currentPage);
            int totalPages = page.getTotalPages();
            long totalItems = page.getTotalElements();
            List<User> userList = page.getContent();

            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("users", userList);
            return "users/allUsers";

        }return getFilteredUsers(model, keyword);
    }

    /**
     *
     * @param model MVC model
     * @param keyword substring of user email which is used to filter listed users, empty keyword
     * matches all users
     * @return view template containing users filtered by the keyword
     */
    @GetMapping("/filtered")
    public String getFilteredUsers(Model model, String keyword){
        if (keyword == null || keyword.isEmpty()){
            return "redirect:/users/users/all";
        }
        model.addAttribute("filteredUsers", userService.getUserByKeyword(keyword));
        return "users/filteredUsers";
    }

}

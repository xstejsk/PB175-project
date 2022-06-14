package fi.muni.courtreservation.registration;

import fi.muni.courtreservation.customexceptions.InvalidEmailException;
import fi.muni.courtreservation.customexceptions.InvalidTokenException;
import fi.muni.courtreservation.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * @author Radim Stejskal 514102
 */
@Controller
@AllArgsConstructor
@RequestMapping(path = "registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;

    /**
     * Creates a new User with the user details contained in the RegistrationRequest parameter,
     * and sends a confirmation email to the newly created user
     * @param request Registration request containing registration details such as username etc.
     * @param bindingResult binding result for the view template
     * @return login view template
     */
    @PostMapping()
    public String register(@Valid @ModelAttribute RegistrationRequest request, BindingResult bindingResult){
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            bindingResult.addError(new FieldError("registrationRequest", "email", "This email is already taken"));
        }
        if (bindingResult.hasErrors()){
            return "registration";
        }

        new Thread(() -> registrationService.register(request)).start();

        return "redirect:/login?registered";
    }

    /**
     * Shows user registration form
     * @param model MVC model
     * @return registration view template
     */
    @GetMapping()
    public String showRegistrationForm(Model model){
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "registration";
    }

    /**
     * Enables the new user account by confirming the token
     * @param token string representation of the confirmation token
     * @return registration view template with a registration result message
     */
    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        try {
            registrationService.confirmToken(token);
            return  "redirect:/registration?success";
        }catch (InvalidEmailException e){
            return  "redirect:/registration?confirmed";
        }catch (InvalidTokenException e){
            return "redirect:/registration?expired";
        }
    }
}

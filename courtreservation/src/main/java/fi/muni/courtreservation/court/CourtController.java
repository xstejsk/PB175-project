package fi.muni.courtreservation.court;

import fi.muni.courtreservation.reservation.ReservationService;
import fi.muni.courtreservation.user.User;
import fi.muni.courtreservation.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Radim Stejskal 514102
 */
@Controller
@RequestMapping("/courts")
@AllArgsConstructor
public class CourtController {
    private final CourtService courtService;
    private final UserService userService;
    private final ReservationService reservationService;

    /**
     *
     * @param model MVC model instance
     * @return template with listed all courts from the database
     */
    @GetMapping()
    public String getAllCourts(Model model){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) userService.loadUserByUsername(userName);
        model.addAttribute("userRole", user.getUserRole().name());
        model.addAttribute("courts", courtService.getAllCourts());
        return "courts/allCourts";
    }

    /**
     * Deletes court with given id from the database as well as all reservations associated with the
     * court
     * @param id id of the court to be deleted
     * @return template with listed all courts from the database
     */
    @GetMapping("/deleteCourt/{id}")
    public String deleteCourtAndAssociatedReservations(@PathVariable(value = "id") long id){
        reservationService.deleteReservationsByCourtId(id);
        courtService.deleteCourt(id);
        return "redirect:/courts";
    }

    /**
     * creates a single court object and saves it in the database
     * @return template with listed all courts from the database
     */
    @PostMapping("/add")
    public String  addCourt(){
        courtService.addCourts(1);
        return "redirect:/courts";
    }
}

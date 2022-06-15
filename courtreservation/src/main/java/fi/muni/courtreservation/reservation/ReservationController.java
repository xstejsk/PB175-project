package fi.muni.courtreservation.reservation;

import fi.muni.courtreservation.customexceptions.InvalidReservationException;
import fi.muni.courtreservation.user.User;
import fi.muni.courtreservation.user.UserRole;
import fi.muni.courtreservation.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Radim Stejskal 514102
 */
@Controller
@RequestMapping(path = "/reservations")
public class ReservationController {

    private final Reservation reservationToUpdate = new Reservation();

    private final ReservationService reservationService;
    private final UserService userService;

    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    /**
     * Initialises and adds attributes to the MVC model
     * @param model MVC model
     */
    @ModelAttribute
    public void addAttributes(Model model){
        model.addAttribute("reservation", new Reservation());
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) userService.loadUserByUsername(userName);
        model.addAttribute("userRole", user.getUserRole().name());
        model.addAttribute("activeReservationsByUser", reservationService.getActiveReservationsByUser(user));
        model.addAttribute("reservationToUpdate", reservationToUpdate);
    }

    /**
     *
     * @return new reservation view template
     */
    @GetMapping("/showNewReservationForm")
    public String showNewReservationForm(){
        return "reservations/newReservation";
    }

    /**
     *
     * @return view template containing reservations of the logged-in user
     */
    @GetMapping
    public String getReservations(){
        return "reservations/userReservations";
    }

    /**
     *
     * @param model MVC model
     * @return view template containing reservations of all users
     */
    @GetMapping("/all")
    public String getAllPagesOfAllReservations(Model model){
        return getOnePageOfAllReservations(model, 1, null);
    }

    /**
     *
     * @param model MVC model
     * @param keyword substring of the user's email to be used in filtering all reservations
     * @return view template containing reservations of all users with emails that match
     * the keyword, empty keyword matches all emails
     */
    @GetMapping("/filtered")
    public String getFilteredReservations(Model model, String keyword){
        if (keyword == null || keyword.isEmpty()){
            return "redirect:/reservations/all";
        }
        model.addAttribute("filteredReservations", reservationService.getReservationsByKeyword(keyword));
        return "reservations/filteredReservations";
    }

    /**
     *
     * @param model MVC model
     * @param currentPage page number to be displayed
     * @param keyword substring of the user's email to be used in filtering all reservations
     * @return view template containing reservations of all users with emails that match
     * the keyword, empty keyword matches all emails
     */
    @GetMapping("/all/{pageNumber}")
    public String getOnePageOfAllReservations(Model model, @PathVariable("pageNumber") int currentPage, String keyword){
        if (keyword == null || keyword.isEmpty()){
            Page<Reservation> page = reservationService.getPageOfAllReservations(currentPage);
            int totalPages = page.getTotalPages();
            long totalItems = page.getTotalElements();
            List<Reservation> reservationList = page.getContent();

            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("reservations", reservationList);
            return "reservations/allReservations";

        }return getFilteredReservations(model, keyword);
    }

    /**
     * Saves a new reservation into the database
     * @param reservation reservation object to be saved into the database
     * @param bindingResult binding result of the view template
     * @return new reservation form view template
     */
    @PostMapping("/saveReservation")
    public String saveReservation(@ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult){
        UserDetails user = userService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        reservation.setUser((User) user);
        try {
            reservationService.saveReservation(reservation);
        }catch (InvalidReservationException e){
            bindingResult.addError(new FieldError("reservation", "start", e.getMessage()));
            return "reservations/newReservation";
        }
        return "redirect:/reservations";
    }

    /**
     * Saves the updated reservation into the database
     * @param reservation reservation with updated details
     * @param bindingResult binding result of the view template
     * @return template view containing the reservation of the logged-in user
     */
    @PostMapping("/updateReservation")
    public String updateReservation(@ModelAttribute("reservationToUpdate") Reservation reservation, BindingResult bindingResult){
        try {
            reservationService.saveReservation(reservation);
        }catch (InvalidReservationException e){
            bindingResult.addError(new FieldError("reservationToUpdate", "start", e.getMessage()));
            return "reservations/updateReservation";
        }
        return "redirect:/reservations";
    }

    /**
     * Shows a form to update selected reservation
     * @param id id of the reservation to be updated
     * @return form view template to update the selected reservation
     * @throws AccessDeniedException thrown when user tries to access a reservation that does not
     * belong to him, unless the user has the admin role
     */
    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable (value = "id") Long id) throws AccessDeniedException{
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails user = userService.loadUserByUsername(userName);
        if (reservationService.userOwnsReservation((User) user, id) || ((User) user).getUserRole() == UserRole.ADMIN){
            Reservation reservationById = reservationService.getReservationById(id);
            if (reservationById.getStart().isBefore(LocalDateTime.now())){
                return "redirect:/reservations?isOngoing";
            }
            reservationToUpdate.setUser(reservationById.getUser());
            reservationToUpdate.setId(reservationById.getId());
            return "reservations/updateReservation";
        }
        throw new AccessDeniedException("invalid reservation id");
    }

    /**
     *
     * @param id id of the reservation to  be deleted from the database
     * @return view template containing all reservations
     * @throws AccessDeniedException thrown when user tries to delete a reservation that does not
     * belong to him, unless the user has the admin role
     */
    @GetMapping("/deleteReservation/{id}")
    public String checkedDelete(@PathVariable(value = "id") long id) throws AccessDeniedException {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails user = userService.loadUserByUsername(userName);
        reservationService.deleteReservation(id, (User) user);
        if (((User) user).getUserRole() == UserRole.USER){
            return "redirect:/reservations";
        }
        return "redirect:/reservations/all";
    }
}


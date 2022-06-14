package fi.muni.courtreservation.startup;

import fi.muni.courtreservation.court.CourtService;
import fi.muni.courtreservation.user.User;
import fi.muni.courtreservation.user.UserRepository;
import fi.muni.courtreservation.user.UserRole;
import fi.muni.courtreservation.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Radim Stejskal 514102
 */
@Component
@AllArgsConstructor
public class CommandLineAppStartupRunner implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    private UserService userService;
    private CourtService courtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * configure these fields yourself
     */
    private final String adminEmail = "stejskalrad@gmail.com";
    private final String adminPw = "password";

    /**
     * Creates an admin account after running the application for the first time, it is HIGHLY
     * advised to change the admin's password after running the application
     * @param args application arguments
     */
    @Override
    public void run(String...args){
        try {
            String encodedPw = bCryptPasswordEncoder.encode(adminPw);
            User admin = new User("Radim",
                    "Stejskal",
                    adminEmail,
                    encodedPw,
                    UserRole.ADMIN,
                    adminEmail);
            userRepository.save(admin);
            userService.enableUser("stejskalrad@gmail.com");
            courtService.addCourts(3);
        }catch (Exception ignored){

        }

    }
}

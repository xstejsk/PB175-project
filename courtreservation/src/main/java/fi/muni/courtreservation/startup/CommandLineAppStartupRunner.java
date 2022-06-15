package fi.muni.courtreservation.startup;

import fi.muni.courtreservation.court.CourtService;
import fi.muni.courtreservation.registration.email.EmailService;
import fi.muni.courtreservation.user.User;
import fi.muni.courtreservation.user.UserRepository;
import fi.muni.courtreservation.user.UserRole;
import fi.muni.courtreservation.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

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
    private final EmailService emailSender;

    /**
     * configure this field yourself
     */
    private final String adminEmail = "stejskalrad@gmail.com";

    /**
     * Creates an admin account after running the application for the first time, it is HIGHLY
     * advised to change the admin's password after running the application
     * @param args application arguments
     */
    @Override
    public void run(String...args){
        CharacterRule alphabets = new CharacterRule(EnglishCharacterData.Alphabetical);
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit);
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String password = passwordGenerator.generatePassword(6, alphabets, digits);
        new Thread(() -> emailSender.send(adminEmail, password)).start();

        try {
            String encodedPw = bCryptPasswordEncoder.encode(password);
            User admin = new User("Admin",
                    "",
                    adminEmail,
                    encodedPw,
                    UserRole.ADMIN,
                    adminEmail);
            userRepository.save(admin);
            userService.enableUser(adminEmail);
            courtService.addCourts(3);
        }catch (Exception ignored){

        }

    }
}

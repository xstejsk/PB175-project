package fi.muni.courtreservation.user;

import fi.muni.courtreservation.registration.token.ConfirmationToken;
import fi.muni.courtreservation.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Radim Stejskal 514102
 */
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private static final int PAGE_SIZE = 15;

    private static final String USER_NOT_FOUND_MSG = "user with email %s not found";

    /**
     *
     * @param pageNumber number of page to be shown
     * @return all users from the databse
     */
    public Page<User> getPageOfAllUsers(int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE);
        return userRepository.findAll(pageable);
    }

    /**
     *
     * @param keyword substring of user email
     * @return all users from database with email matching the keyword
     */
    public List<User> getUserByKeyword(String keyword){
        return userRepository.findAllByKeyword(keyword);
    }

    /**
     *
     * @param email is interchangeable with the username
     * @return UserDetails object with given email
     * @throws UsernameNotFoundException thrown when no user with given email exists
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    /**
     * Saves the user into the database
     * @param user user object to be saved into the database
     * @return string representation of the confirmation token
     */
    public String saveUser(User user){
        Optional<User> newUser = userRepository.findUserByEmail(user.getEmail());
        if (newUser.isPresent() && newUser.get().getEnabled()){
            throw new IllegalStateException("account with this email is already registered");
        }else if (newUser.isPresent() && !newUser.get().getEnabled()){
            user = newUser.get();
        }
        else if (newUser.isEmpty()){
            userRepository.save(user);
        }
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                user);

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    /**
     *
     * @param email email of the user that is to be enabled
     */
    public void enableUser(String email) {
        userRepository.enableUser(email);
    }
}

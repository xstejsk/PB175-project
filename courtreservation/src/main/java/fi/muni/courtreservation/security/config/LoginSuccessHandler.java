package fi.muni.courtreservation.security.config;

import fi.muni.courtreservation.user.User;
import fi.muni.courtreservation.user.UserRole;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Radim Stejskal 514102
 */
@Configuration
@EnableWebSecurity
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /**
     * Redirects user to reservation template view based on his role
     * @param request http request
     * @param response http response
     * @param authentication authentication object
     * @throws IOException input output exception
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        User user = (User) authentication.getPrincipal();
        String redirectURL;

        if (user.getUserRole() == UserRole.ADMIN) {
            redirectURL = "reservations/all";
        } else {
            redirectURL = "reservations";
        }
        response.sendRedirect(redirectURL);
    }
}

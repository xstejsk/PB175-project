package fi.muni.courtreservation.registration;

import fi.muni.courtreservation.customexceptions.InvalidTokenException;
import fi.muni.courtreservation.registration.email.EmailSender;
import fi.muni.courtreservation.registration.token.ConfirmationToken;
import fi.muni.courtreservation.registration.token.ConfirmationTokenService;
import fi.muni.courtreservation.user.User;
import fi.muni.courtreservation.user.UserRole;
import fi.muni.courtreservation.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * contains details from the registration form view template
 * @author Radim Stejskal 514102
 */
@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    /**
     * Edit this field yourself
     */
    private final String host = "https://court-reservations-project.herokuapp.com";

    /**
     * Creates a new User with the user details contained in the RegistrationRequest parameter,
     * and sends a confirmation email to the newly created user
     * @param request Registration request containing registration details such as username etc.
     */
    public void register(RegistrationRequest request){
        String token = userService.saveUser(
                new User(request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        UserRole.USER,
                        request.getEmail())
        );
        String link = host + "/registration/confirm?token=" + token;
        emailSender.send(request.getEmail(), buildEmail(request.getFirstName(), link));
    }

    /**
     * Enables the newly created account associated with the confirmation token
     * @param token string representation of the confirmation token
     * @throws InvalidTokenException exception to denote invalid confirmation token
     */
    @Transactional
    public void confirmToken(String token) throws InvalidTokenException {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new InvalidTokenException("Email is already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token has expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableUser(confirmationToken.getUser().getEmail());
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;" +
                "color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"" +
                "border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\"" +
                " cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"" +
                "border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\"" +
                " border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                 \n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;" +
                "Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;" +
                "font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:" +
                "inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" " +
                "align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=" +
                "\"border-collapse:collapse;max-width:580px;width:100%!important\"" +
                " width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" " +
                "cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" " +
                "align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" " +
                "style=\"border-collapse:collapse;max-width:580px;width:100%!important\" " +
                "width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;" +
                "line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;" +
                "color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;" +
                "line-height:25px;color:#0b0c0c\"> Thank you for registering." +
                " Please click on the below link to activate your account: </p><blockquote style=" +
                "\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;" +
                "font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;" +
                "line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> " +
                "</p></blockquote>\n Link will expire in 30 minutes." +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}

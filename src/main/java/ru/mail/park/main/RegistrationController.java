package ru.mail.park.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Solovyev on 06/09/16.
 */

@RestController
public class RegistrationController {
    private final AccountService accountService;

    @Autowired
    public RegistrationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(path = "/api/guest/", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity guestLogin(@RequestBody RegistrationRequest body,
                                HttpSession httpSession) {
        final String unsafeLogin = body.getLogin();
        final String password = new BigInteger(130, new SecureRandom()).toString(32);
        if (StringUtils.isEmpty(unsafeLogin)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }
        final String login = HtmlUtils.htmlEscape(unsafeLogin);
        final UserProfile existingUser = accountService.getUserByName(login);
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }
        final UserProfile user = accountService.addUser(login, password, "");
        httpSession.setAttribute("userId", user.getId().getId());
        return ResponseEntity.ok(new SuccessResponse(login));
    }

    @SuppressWarnings("unused")
    private static final class RegistrationRequest {
        private String login;

        private RegistrationRequest() {
        }

        private RegistrationRequest(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }

    }

    private static final class SuccessResponse {
        private String login;

        private SuccessResponse(String login) {
            this.login = login;
        }

        @SuppressWarnings("unused")
        public String getLogin() {
            return login;
        }
    }

}

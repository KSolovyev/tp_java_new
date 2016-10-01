package ru.mail.park.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;

import javax.servlet.http.HttpSession;

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

    @RequestMapping(path = "/api/user/", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody RegistrationRequest body,
                                HttpSession httpSession) {
        final String sessionId = httpSession.getId();

        final String login = body.getLogin();
        final String password = body.getPassword();
        final String email = body.getEmail();
        if (StringUtils.isEmpty(login)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }
        final UserProfile existingUser = accountService.getUser(login);
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }

        accountService.addUser(login, password, email);
        return ResponseEntity.ok(new SuccessResponse(login));
    }

    @RequestMapping(path = "/api/session", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity auth(@RequestParam(name = "login") String login,
                               @RequestParam(name = "password") String password) {
        if (StringUtils.isEmpty(login)
                || StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
        }
        final UserProfile user = accountService.getUser(login);
        if (user.getPassword().equals(password)) {
            return ResponseEntity.ok(new SuccessResponse(user.getLogin()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
    }

    private static final class RegistrationRequest {
        private String login;
        private String password;
        private String email;

        private RegistrationRequest() {
        }

        private RegistrationRequest(String login, String password, String email) {
            this.login = login;
            this.password = password;
            this.email = email;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
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

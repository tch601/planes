package BsmchFlights.Controllers;

import BsmchFlights.Exceptions.NoRecordFound;
import BsmchFlights.Exceptions.UserExist;
import BsmchFlights.Models.MyJWT;
import BsmchFlights.Models.User;
import BsmchFlights.Services.UsersService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersService usersService;

    @CrossOrigin
    @GetMapping("")
    public List<User> getAll() {
        return this.usersService.getAll();
    }

    @CrossOrigin
    @PostMapping(path = "/insert", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> insertUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(this.usersService.insertUser(user), HttpStatus.OK);
        } catch (UserExist exception) {
            logger.error(exception.getMessage());

            return new ResponseEntity<>(exception.getMessage(), HttpStatus.ALREADY_REPORTED);
        }

    }
    @CrossOrigin
    @PostMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(this.usersService.updateUser(user), HttpStatus.OK);
        } catch (NoRecordFound exception) {
            logger.error(exception.getMessage());

            return new ResponseEntity<>(exception.getMessage(), HttpStatus.ALREADY_REPORTED);
        }

    }
    @CrossOrigin
    @GetMapping("/login/{email}/{password}")
    public ResponseEntity<String> getUserByEmailAndPass(@PathVariable String email, @PathVariable String password) {
        String jwt = this.usersService.getUserByEmailAndPass(email, password);
        HttpStatus hts;
        System.out.println("login");

        if (jwt != null) {
            hts = HttpStatus.OK;
        } else {
            hts = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(jwt, hts);
    }

    @CrossOrigin
    @GetMapping("/jwt/{jwt}")
    public ResponseEntity<User> getUser(@PathVariable String jwt) {
        User user = new User();
        HttpStatus hts = HttpStatus.OK;

        if (!jwt.equals("---")) {
            try {
                user = MyJWT.decodeUser(jwt);
            } catch (JWTVerificationException ignored) {
            } catch (Exception e) {
                hts = HttpStatus.NOT_FOUND;
            }
        }

        return new ResponseEntity<>(user, hts);
    }
}

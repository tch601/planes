package BsmchFlights.Services;

import BsmchFlights.Exceptions.NoRecordFound;
import BsmchFlights.Exceptions.UserExist;
import BsmchFlights.Models.MyJWT;
import BsmchFlights.Models.User;
import BsmchFlights.Repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReservationsService.class);
    public List<User> getAll() {
        return this.usersRepository.findAll();
    }
    public String updateUser(User user) throws NoRecordFound {
        if(this.usersRepository.findById(user.getId())!=null){
            logger.info("update user with id " + user.getId());
            this.usersRepository.save(user);

            return MyJWT.getUserJWTToken(user);
        }
        logger.warn("try to update user not exist, user with id " + user.getId());
        throw new NoRecordFound("this user is not exist change email or password");
    }

    public String insertUser(User user) throws UserExist {
        if(this.usersRepository.findByEmailAndPassword(user.getEmail(),user.getPassword())==null
            && this.usersRepository.findById(user.getId())!=null){
            this.usersRepository.save(user);
            logger.info("insert new user with id " + user.getId());
            return MyJWT.getUserJWTToken(user);
        }
        logger.info("try to insert new user, but user already exist. with id " + user.getId());
        throw new UserExist("this user is already exist change email or password");
    }
    public String getUserByEmailAndPass(String email, String password) {
        User user = this.usersRepository.findByEmailAndPassword(email, password);
        logger.info("try get user with email " + email + " and password "+password);
        if (user == null) {
            System.out.println(email);
            user = new User();
        }

        return MyJWT.getUserJWTToken(user);
    }

    ;
}

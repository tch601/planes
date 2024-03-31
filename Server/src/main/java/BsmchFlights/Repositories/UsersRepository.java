package BsmchFlights.Repositories;

import BsmchFlights.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
    User findByEmailAndPassword(String email, String password);
}

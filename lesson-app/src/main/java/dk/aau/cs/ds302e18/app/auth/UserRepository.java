package dk.aau.cs.ds302e18.app.auth;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository to access the authentication information of the user
 */
public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
    User findById(long id); // This method is not verified to work
}

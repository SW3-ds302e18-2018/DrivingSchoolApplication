package dk.aau.cs.ds302e18.app.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * The repository used to access the account role
 */
public interface AuthGroupRepository extends JpaRepository<AuthGroup, Long> {
    List<AuthGroup> findByUsername(String username);
}

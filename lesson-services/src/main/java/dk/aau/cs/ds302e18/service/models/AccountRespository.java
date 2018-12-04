package dk.aau.cs.ds302e18.service.models;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The repository used to access account detail
 */
public interface AccountRespository extends JpaRepository<Account, Long>
{
    Account findByUsername(String username);
}
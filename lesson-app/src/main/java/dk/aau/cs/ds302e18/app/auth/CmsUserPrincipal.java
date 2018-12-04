package dk.aau.cs.ds302e18.app.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmsUserPrincipal implements UserDetails{

    private User user;
    private List<AuthGroup> authGroups;
    private Account account;

    public CmsUserPrincipal(User user, List<AuthGroup> authGroups, Account account){
        super();
        this.user = user;
        this.authGroups = authGroups;
        this.account = account;
    }

    /* Grants appropriate authorities to the users following the values in the database connected to from authGroups */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(null==authGroups){
            return Collections.emptySet();
        }
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        authGroups.forEach(group->{
           grantedAuthorities.add(new SimpleGrantedAuthority(group.getAuthGroup()));
        });
        return grantedAuthorities;

    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isActive();
    }

}

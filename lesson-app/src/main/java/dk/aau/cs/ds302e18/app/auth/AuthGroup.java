package dk.aau.cs.ds302e18.app.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/* Ensures that the table AuthGroup exists in the database and has the following columns.
   Also provides setters and getters. */
@Entity
@Table(name="AUTH_USER_GROUP")
public class AuthGroup {
    @Id
    @Column(name="AUTH_USER_GROUP_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="USERNAME")
    private String username;
    @Column(name="AUTH_GROUP")
    private String authGroup;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthGroup() {
        return authGroup;
    }

    public void setAuthGroup(String authGroup) {
        this.authGroup = authGroup;
    }
}

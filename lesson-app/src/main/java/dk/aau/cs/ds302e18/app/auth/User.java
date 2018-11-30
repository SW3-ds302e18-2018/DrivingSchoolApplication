package dk.aau.cs.ds302e18.app.auth;

import javax.persistence.*;
import java.util.ArrayList;

/* Ensures that the table User exists in the database and has the following columns.
   Also provides setters and getters. */
@Entity
@Table(name="USER")
public class User {
    @Id
    @Column(name="USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="USERNAME", nullable = false, unique = true)
    private String username;
    @Column(name="PASSWORD")
    private String password;
    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive;


    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public void setActive(boolean active)
    {
        isActive = active;
    }

}

package dk.aau.cs.ds302e18.app.domain;

public class AccountViewModel
{
    private String username;
    private String firstName;
    private String lastName;
    private String authGroup;

    public AccountViewModel()
    {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authGroup = authGroup;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getAuthGroup()
    {
        return authGroup;
    }

    public void setAuthGroup(String authGroup)
    {
        this.authGroup = authGroup;
    }

    @Override
    public String toString()
    {
        return "AccountViewModel{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", authGroup='" + authGroup + '\'' +
                '}';
    }
}

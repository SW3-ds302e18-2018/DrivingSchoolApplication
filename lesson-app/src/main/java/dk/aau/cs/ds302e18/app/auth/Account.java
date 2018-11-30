package dk.aau.cs.ds302e18.app.auth;

import javax.persistence.*;


/* Ensures that the table Account exists in the database and has the following columns.
   Also provides setters and getters. */
@Entity
@Table(name="Account")
public class Account
{
    @Id
    @Column(name="AUTH_USER_ACCOUNT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="USERNAME", nullable = false)
    private String username;
    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;
    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;
    @Column(name = "BIRTHDAY", nullable = false)
    private String birthday;
    @Column(name = "ADDRESS", nullable = false)
    private String address;
    @Column(name = "ZIP", nullable = false)
    private int zipCode;
    @Column(name = "CITY", nullable = false)
    private String city;

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

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public int getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(int zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    @Override
    public String toString()
    {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", zipCode=" + zipCode +
                ", city='" + city + '\'' +
                '}';
    }
}
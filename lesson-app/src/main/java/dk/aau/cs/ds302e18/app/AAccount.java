package dk.aau.cs.ds302e18.app;

import java.util.Objects;

public abstract class AAccount {

    private int userID;
    private String firstName;
    private String lastName;
    private String phonenumber;
    private String email;
    private String birthdate;
    private String address;
    private String zipCode;
    private String city;
    private String username;
    private String password;

    public AAccount(String firstName, String lastName, String phonenumber, String email, String birthdate, String address, String zipCode, String city, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phonenumber = phonenumber;
        this.email = email;
        this.birthdate = birthdate;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.username = username;
    }

    public int getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getAddress() {
        return address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AAccount{" +
                "userID=" + userID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phonenumber=" + phonenumber +
                ", email='" + email + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", address='" + address + '\'' +
                ", zipCode=" + zipCode +
                ", city='" + city + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AAccount AAccount = (AAccount) o;
        return userID == AAccount.userID &&
                phonenumber == AAccount.phonenumber &&
                zipCode == AAccount.zipCode &&
                Objects.equals(firstName, AAccount.firstName) &&
                Objects.equals(lastName, AAccount.lastName) &&
                Objects.equals(email, AAccount.email) &&
                Objects.equals(birthdate, AAccount.birthdate) &&
                Objects.equals(address, AAccount.address) &&
                Objects.equals(city, AAccount.city) &&
                Objects.equals(username, AAccount.username) &&
                Objects.equals(password, AAccount.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, firstName, lastName, phonenumber, email, birthdate, address, zipCode, city, username, password);
    }
}

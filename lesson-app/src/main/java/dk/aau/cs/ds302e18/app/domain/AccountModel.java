package dk.aau.cs.ds302e18.app.domain;

public class AccountModel {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String birthday;
    private String address;
    private int zipCode;
    private String city;
    private int notificationInMinutes;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getNotificationInMinutes() {
        return notificationInMinutes;
    }

    public void setNotificationInMinutes(int notificationInMinutes) {
        this.notificationInMinutes = notificationInMinutes;
    }

    public Account translateModelToAccount(){
        Account account = new Account();
        account.setUsername(this.username);
        account.setFirstName(this.firstName);
        account.setLastName(this.lastName);
        account.setEmail(this.email);
        account.setPhoneNumber(this.phoneNumber);
        account.setBirthday(this.birthday);
        account.setAddress(this.address);
        account.setZipCode(this.zipCode);
        account.setCity(this.city);
        account.setNotificationInMinutes(this.notificationInMinutes);
        return account;
    }
}

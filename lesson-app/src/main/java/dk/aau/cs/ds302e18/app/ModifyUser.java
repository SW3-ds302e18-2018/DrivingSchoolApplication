package dk.aau.cs.ds302e18.app;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class ModifyUser
{
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String birthday;
    private String address;
    private String zipCode;
    private String city;

    public ModifyUser(String username, String password, String firstName, String lastName, String phoneNumber,
                        String email, String birthday, String address, String zipCode, String city)
    {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthday = birthday;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;

        ResourceBundle reader = ResourceBundle.getBundle("application");
        String mysqlUrl = reader.getString("spring.datasource.url");
        String mysqlUsername = reader.getString("spring.datasource.username");
        String mysqlPassword = reader.getString("spring.datasource.password");

        int accountId = ThreadLocalRandom.current().nextInt(100,100000000);

        try
        {
            // create a mysql database connection
            String myDriver = "org.gjt.mm.mysql.Driver";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);

            Statement st = conn.createStatement();

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

            // note that i'm leaving "date_created" out of this insert statement
            st.executeUpdate("UPDATE INTO (user_id, isacitve, username, password) "
                    +"VALUES ('"+accountId+"', 1,'"+username+"','"+passwordEncoder.encode(password)+"')");
            st.executeUpdate("INSERT INTO account (auth_user_account_id, address, birthday, city, email, firstname," +
                    " lastname, phonenumber, username, zip)"
                    +"VALUES ('"+accountId+"', '"+address+"','"+birthday+"','"+city+"','"+email+"','"+firstName+"','"+lastName+"','"+phoneNumber+"','"+username+"','"+zipCode+"')");
            st.executeUpdate("INSERT INTO auth_user_group (auth_user_group_id, auth_group, username) "
                    +"VALUES ('"+accountId+"', 'ADMIN','"+username+"')");

            conn.close();
        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }
}

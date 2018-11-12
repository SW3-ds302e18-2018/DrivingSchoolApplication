package dk.aau.cs.ds302e18.app;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
@RequestMapping("/")
public class AccountController {

    private Connection conn;
    //This method will update the accounts information.
    public AccountController() {this.conn = new DBConnector().createConnectionObject();}

    public void editAccount(String address, String birthday, String email, String firstname, String city,
                            String lastname, int phonenumber, String username, int zip ) {
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("UPDATE `account` SET `address` = '"+address+"',  `birthday` = '"+birthday+"',   " +
                    "`email` = '"+email+"', `firstname` = '"+firstname+"', `lastname`='"+lastname+"', `city`='"+city+"', " +
                    " `phonenumber` = "+phonenumber+", `zip`="+zip+" WHERE `username` = '"+username+"'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @GetMapping(value = "/modifyAccount")
    public String getModifyPage()
    {
        return "modify-account";
    }

    @GetMapping(value = "/contact")
    public String getContactPage()
    {
        return "contact-formular";
    }

    @PostMapping(value = "/modifyAccount")
    public String postModifyPage(@ModelAttribute Student student){
        System.out.println(student.toString());
        new RegisterUser(student.getUsername(), student.getPassword(), student.getFirstName(), student.getLastName(),
                student.getPhonenumber(), student.getEmail(), student.getBirthdate(), student.getAddress(),
                student.getZipCode(), student.getCity());
        return "modify-account";
    }
    public void getUsername() {
        getUsername(); {this.conn = new DBConnector().createConnectionObject();}

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT`address`, `birthday`, `city`, `email`, `firstname`," +
                    " `lastname`, `phonenumber`, `username`, `zip` FROM `account` WHERE `username` = Sembrik");
            rs.next();
            String accountAddress = rs.getString("address");
            String accountBirthday = rs.getString("birthday");
            String accountCity = rs.getString("city");
            String accountFirstname = rs.getString("firstname");
            String accountLastname = rs.getString("lastname");
            String accountPhonenumber = rs.getString("phonenumber");
            String accountZip = rs.getString("zip");
            String accountUserName = rs.getString("username");

            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}

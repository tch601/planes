package BsmchFlights.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_flights_users")
public class User {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "image")
    private String image;

    @Column(name = "joindate")
    private String join_date;

    @Column(name = "creditcard")
    private String creditcard;

    @ManyToOne
    @JoinColumn(name = "permissions_id")
    @JsonProperty
    private Permission permission;

    public User() {
        this.username = "אורח";
        this.image = "https://as1.ftcdn.net/v2/jpg/02/59/39/46/1000_F_259394679_GGA8JJAEkukYJL9XXFH2JoC3nMguBPNH.jpg";
        this.permission = new Permission(3, "אורח");
    }

    public User(int id) {
        this.id = id;
    }

    public User(int id, String username, String firstname, String lastname, String email, String password, String join_date, String image, String creditcard, Permission permission) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.join_date = join_date;
        this.image = image;
        this.creditcard = creditcard;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJoindate() {
        return join_date;
    }

    public void setJoindate(String join_date) {
        this.join_date = join_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(String creditcard) {
        this.creditcard = creditcard;
    }

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date;
    }

    public Permission getPermissions() {
        return permission;
    }

    public void setPermissions(Permission permission) {
        this.permission = permission;
    }

    public String getAsString() {
        return (id + " " + username + " " + firstname + " " + lastname + " " + email + " " + password + " " + join_date + " " + image + " " + creditcard + " " + permission.toString());
    }

    public static User stringToUser(String string) {
        String[] details = string.split(" ");

        return new User(Integer.parseInt(details[0]), details[1], details[2], details[3], details[4], details[5], details[6], details[7], details[8], Permission.stringToPermission(details[9] + " " + details[10]));
    }
}

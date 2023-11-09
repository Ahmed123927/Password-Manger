package Model;

import java.time.LocalDate;

public class Account {

    String password;
    String email;
    int user_id;
    int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Account(String password, String email, int user_id, int flag, String website,  int id) {
        this.password = password;
        this.email = email;
        this.user_id = user_id;
        this.flag = flag;
        this.website = website;
        this.id = id;
    }

    public Account(String password, String email, int user_id, String website, LocalDate start_date, LocalDate end_date, int id,int flag) {
        this.password = password;
        this.email = email;
        this.user_id = user_id;
        this.website = website;
        this.start_date = start_date;
        this.end_date = end_date;
        this.id = id;
        this.flag = flag;
    }

    String website;
    LocalDate start_date;
    LocalDate end_date;



    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account(String password, String email, int user_id, String website, int id) {
        this.password = password;
        this.email = email;
        this.user_id = user_id;
        this.website = website;
        this.id = id;
    }



    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Account() {
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Account{" +
                "password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", user_id=" + user_id +
                ", flag=" + flag +
                ", website='" + website + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", id=" + id +
                '}';
    }
}

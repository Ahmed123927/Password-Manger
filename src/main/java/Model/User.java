package Model;

import Operations.AESEncryption;
import Operations.Hashing;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class User {
    int id;
    String userName;
    String email;
    String password;
    boolean admin;

    public User() throws NoSuchAlgorithmException {
    }

    public User( boolean admin,int id, String userName, String email, String password) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = Hashing.hashPassword(password);
        this.admin = admin;
    }

    public User(int id, String userName, String email, String password) throws NoSuchAlgorithmException {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = Hashing.hashPassword(password);
    }
    static AESEncryption aesEncryption=new AESEncryption();
   public  static SecretKey key;


    public static SecretKey getKey() {
        return key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
//        this.password = Hashing.hashPassword(password);
        this.password=password;

    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

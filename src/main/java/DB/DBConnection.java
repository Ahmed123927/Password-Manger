package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection SingletonInstance=null;



    private static final String Host="127.0.0.1";
    private static final int Port=5432;
    private static final String DB_name="pwdb";
    private static final String UserName="postgres";
    private static final String Password="root";

    private DBConnection() {
    }
    public static DBConnection getInstance(){
        if (SingletonInstance==null){
            SingletonInstance= new DBConnection();
        }
        return SingletonInstance;
    }
    public synchronized Connection getConnection() throws SQLException {

        Connection connection= DriverManager.getConnection(String.format("jdbc:postgresql://%s:%d/%s",Host,Port,DB_name),UserName,Password);
        return connection;
    }
}


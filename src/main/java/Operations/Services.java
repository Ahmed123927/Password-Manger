package Operations;

import Model.User;

import java.sql.SQLException;
import java.util.List;

public interface Services {
    void Register(User user) throws SQLException;

    User loginAuthentication(String user_name, String password) throws SQLException;
    void Delete(int id) throws SQLException;
User expiryUser(int userId) throws SQLException;
//User getUserById(int id) throws SQLException;

}

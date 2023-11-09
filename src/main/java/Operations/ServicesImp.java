package Operations;

import DB.DBConnection;
import Model.User;


import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ServicesImp implements Services{
    @Override
    public void Register(User user) throws SQLException {
        DBConnection dbConnection=DBConnection.getInstance();
        Connection connection =   dbConnection.getConnection();
        if (connection == null) {
            return;
        }
        if (user.getId() > 0) {
            String query = "UPDATE users SET user_name=?,password=?,email=? WHERE id=? ";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, AESEncryption.encrypt(user.getPassword()));
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setInt(4, user.getId());
                preparedStatement.executeUpdate();
            }
        } else {
            String query = "INSERT INTO users(user_name,password,email,admin) VALUES(?,?,?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, AESEncryption.encrypt(user.getPassword()));
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setBoolean(4,user.isAdmin());
                preparedStatement.executeUpdate();
            } catch (SQLException se) {
                System.out.println("Register Field try another user name or email");
            } finally {
                try {
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        }
    }
    @Override
    public User loginAuthentication(String user_name, String password) throws SQLException {
        DBConnection dbConnection=DBConnection.getInstance();
        Connection connection =   dbConnection.getConnection();
        if (connection == null) {
            return null;
        }

        String query = "SELECT * FROM users WHERE user_name=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user_name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String encryptedPassword = resultSet.getString("password");
                String hashedPassword = AESEncryption.decrypt(encryptedPassword);

                if (Hashing.verifyPassword(password, hashedPassword)) {

                    return new User(
                            resultSet.getBoolean("admin"),
                            resultSet.getInt("id"),
                            resultSet.getString("user_name"),
                            resultSet.getString("email"),
                            hashedPassword
                    );
                }
            }
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
        finally {
            try {
                connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        System.out.println("Login Failed");
        return null;
    }

    @Override
    public void Delete(int id) throws SQLException {
        DBConnection dbConnection=DBConnection.getInstance();
        Connection connection =   dbConnection.getConnection();
        if (connection == null) {
            return ;
        }
        String query="DELETE FROM users WHERE id=?";
        try (PreparedStatement preparedStatement=connection.prepareStatement(query)){
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException se){
                se.printStackTrace();
            }
        }

    }

    @Override
    public User expiryUser(int userId) throws SQLException {
        DBConnection dbConnection=DBConnection.getInstance();
        Connection connection =   dbConnection.getConnection();
        if (connection == null) {
            return null ;
        }
        String query="SELECT * FROM users WHERE id=?";
        try (PreparedStatement preparedStatement= connection.prepareStatement(query)){
            preparedStatement.setInt(1,userId);
            ResultSet resultSet= preparedStatement.executeQuery();
            if (resultSet.next()){
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("user_name"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException se){
                se.printStackTrace();
            }
        }
        return null;
    }

//    @Override
//    public User getUserById(int id) throws SQLException {
//        DBConnection dbConnection=DBConnection.getInstance();
//        Connection connection =   dbConnection.getConnection();
//        if (connection == null) {
//            return null ;
//        }
//        String query="SELECT * FROM users WHERE id=?";
//        try (PreparedStatement preparedStatement=connection.prepareStatement(query)){
//            preparedStatement.setInt(1,id);
//           ResultSet resultSet= preparedStatement.executeQuery();
//           if (resultSet.next()){
//               return new User(
//                       resultSet.getInt("id"),
//                       resultSet.getString(),
//               )
//           }
//        }
//    }


}






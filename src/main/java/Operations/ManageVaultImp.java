package Operations;

import DB.DBConnection;
import Model.Account;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManageVaultImp  implements ManageVault{


    Log appLogs=new Log("Logs.txt");


    public ManageVaultImp() throws IOException {
    }

    @Override
    public synchronized void addInVault(Account account) throws SQLException {
        DBConnection dbConnection=DBConnection.getInstance();
        Connection connection =   dbConnection.getConnection();
        if (connection == null) {
            return ;
        }
        LocalDate currentDate=LocalDate.now();
        int days = 1;
        LocalDate endDate = currentDate.plusDays(days);
        connection.setAutoCommit(false);
        String query = "INSERT INTO vaults (user_id, password, website,email,start_date,end_date,flag) VALUES (?, ?, ?,?,?,?,?)";
        try (PreparedStatement preparedStatement= connection.prepareStatement(query)){
            preparedStatement.setInt(1,account.getUser_id());
            preparedStatement.setString(2,AESEncryption.encrypt(account.getPassword()));
            preparedStatement.setString(3,account.getWebsite());
            preparedStatement.setString(4,account.getEmail());
            preparedStatement.setDate(5, Date.valueOf(currentDate));
            preparedStatement.setDate(6, Date.valueOf(endDate));
            preparedStatement.setInt(7,account.getFlag());

            preparedStatement.executeUpdate();
            connection.commit();
            appLogs.logger.info("Added to vault");


        }
        catch (SQLException se){
            se.printStackTrace();
            appLogs.logger.warning(se.getMessage());
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
    public synchronized List<Account> ViewAll(int id) throws SQLException {
        DBConnection dbConnection=DBConnection.getInstance();
        Connection connection =   dbConnection.getConnection();
        if (connection == null) {
            return null;
        }
        connection.setAutoCommit(false);
        String query="SELECT * FROM vaults WHERE user_id=?";
        List<Account> accounts=new ArrayList<>();
        try (PreparedStatement preparedStatement=connection.prepareStatement(query)){
            preparedStatement.setInt(1,id);
            ResultSet resultSet= preparedStatement.executeQuery();
            while (resultSet.next()){
                String pass=resultSet.getString("password");
                String password=AESEncryption.decrypt(pass);
                Account account=new Account(
                        password,
                        resultSet.getString("email"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("website"),

                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        resultSet.getInt("id"),
                        resultSet.getInt("flag")

                );
                accounts.add(account);
            }
                    connection.commit();
            //appLogs.logger.info("view All");
        }
        catch (SQLException se){
            se.printStackTrace();
            //appLogs.logger.warning(se.getMessage());
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException se){
                se.printStackTrace();
            }
        }
        return accounts;
    }

    @Override
    public synchronized void UpdateAccount(Account account) throws SQLException {
        DBConnection dbConnection = DBConnection.getInstance();
        Connection connection = dbConnection.getConnection();
        if (connection == null) {
            return;
        }

        LocalDate currentDate = LocalDate.now();
        int days = 1;
        LocalDate endDate = currentDate.plusDays(days);

        String query = "UPDATE vaults SET email=?, password=?, website=?, start_date=?, end_date=?, flag=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, account.getEmail());
            preparedStatement.setString(2, AESEncryption.encrypt(account.getPassword()));
            preparedStatement.setString(3, account.getWebsite());
            preparedStatement.setDate(4, Date.valueOf(account.getStart_date()));
            preparedStatement.setDate(5, Date.valueOf(account.getEnd_date()));
            preparedStatement.setInt(6, account.getFlag()); // Set the flag value here
            preparedStatement.setInt(7, account.getId());

            preparedStatement.executeUpdate();
            appLogs.logger.info("Account has been updated");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            appLogs.logger.warning(sqlException.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    @Override
    public synchronized void Delete(int id) throws SQLException {
        DBConnection dbConnection=DBConnection.getInstance();
        Connection connection =   dbConnection.getConnection();
        if (connection == null) {
            return ;
        }
        String query="DELETE FROM vaults WHERE id=?";
        try (PreparedStatement preparedStatement=connection.prepareStatement(query)){
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
            appLogs.logger.info("account has been deleted");
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
    public synchronized void exportFile(String filePath, String account) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(account);
            System.out.println("Account exported successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        }

        @Override
    public synchronized List<Account> importFile(String filePath) throws IOException, ClassNotFoundException {
        List<Account> accounts = new ArrayList<>();

        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            while (true) {
                try {
                    Account account = (Account) objectIn.readObject();
                    accounts.add(account);
                } catch (EOFException e) {
                    break;
                }
            }
            System.out.println("Accounts imported successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("error  " + e.getMessage());

        }

        return accounts;
    }

    @Override
    public List<Account> expiryDates() throws SQLException {
        DBConnection dbConnection = DBConnection.getInstance();
        Connection connection = dbConnection.getConnection();
        if (connection == null) {
            return null;
        }

        List<Account> accounts = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        String query = "SELECT * FROM vaults WHERE end_date <= ? AND flag=0";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, Date.valueOf(currentDate));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Account account = new Account(
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("website"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        resultSet.getInt("id"),
                        resultSet.getInt("flag")
                );
                accounts.add(account);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return accounts;
    }
    public synchronized void updateExpiredPasswords() {
        try {
            List<Account> expiredAccounts = expiryDates();

            if (expiredAccounts != null) {
                for (Account account : expiredAccounts) {
                    // Update the flag for the expired account
                    account.setFlag(0);
                    UpdateAccount(account);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

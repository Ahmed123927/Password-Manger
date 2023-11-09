package Operations;

import Model.Account;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ManageVault {
    void addInVault(Account account) throws SQLException;
    List<Account>ViewAll(int id) throws SQLException;
    void UpdateAccount(Account account) throws SQLException;
    void  Delete(int id) throws SQLException;
    void exportFile(String filePath, String account) throws IOException;
    public List<Account> importFile(String filePath) throws IOException, ClassNotFoundException;
    List<Account> expiryDates() throws SQLException;
}

package Operations;

import Model.Account;
import Model.User;
import Operations.ManageVaultImp;
import Operations.Services;

import java.sql.SQLException;
import java.util.List;

public class PasswordExpiryNotifier implements Runnable {
    private static final long CHECK_INTERVAL = 24 * 60 * 60 * 1000;

    private final ManageVaultImp manageVault;
    private final Services services;

    public PasswordExpiryNotifier(ManageVaultImp manageVault, Services services) {
        this.manageVault = manageVault;
        this.services = services;
    }

    @Override
    public void run() {
        while (true) {
            try {
                updateExpiredPasswords();

            } catch ( SQLException e) {
                e.printStackTrace();

            }
        }
    }

    private void updateExpiredPasswords() throws SQLException {
        List<Account> expiredAccounts = manageVault.expiryDates();

        if (expiredAccounts != null) {
            for (Account account : expiredAccounts) {
                System.out.println("Account ID " + account.getId() + " has an expired password.");

                account.setFlag(1);
                manageVault.UpdateAccount(account);

                User user = services.expiryUser(account.getUser_id());
                if (user != null) {
                    String subject = "Password Manager - Expired Password";
                    String receiver = user.getEmail();
                    String content = "Please update your account on " + account.getWebsite();
                    EmailSender.sendEmail(receiver, subject, content);
                } else {
                    System.out.println("User information not found for account: " + account.getId());
                }
            }
        }
    }
}

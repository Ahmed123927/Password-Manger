package org.example;
import Model.Account;
import Operations.*;

import DB.DBConnection;
import Model.User;


import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ServicesImp service=new ServicesImp();
        ManageVaultImp manageVault=new ManageVaultImp();
        PasswordExpiryNotifier passwordExpiryNotifier=new PasswordExpiryNotifier(manageVault,service);

        System.out.println(manageVault.expiryDates());
        executorService.submit(passwordExpiryNotifier);
        boolean x=true;
        while (x) {
            System.out.println("Welcome to Password Manger App!");
            System.out.println("-------------------");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.println("-------------------");
            System.out.println("Enter your choice ==>");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1: {
                    System.out.println("Login Form");
                    System.out.println("-----------");
                    System.out.println("Username: ");
                    String username = scanner.next();
                    System.out.println("Password: ");
                    String password = scanner.next();
                    System.out.println("-----------");
                    if (service.loginAuthentication(username, password) != null) {

                        logger.info("loged in");
                        User userLogin = service.loginAuthentication(username, password);
                        if (userLogin.isAdmin() == false) {
                            boolean y = true;
                            while (y) {
                                System.out.println("Hello User" + " " + username);
                                System.out.println("Welcome to the Password Vault!");
                                System.out.println("-------------------");
                                System.out.println("1. Add Account to Vault");
                                System.out.println("2. View All Accounts");
                                System.out.println("3. Update Account");
                                System.out.println("4. Delete Account");
                                System.out.println("5. Exit");
                                System.out.println("-------------------");
                                System.out.print("Enter your choice: ");


                                int choice1 = scanner.nextInt();
                                switch (choice1) {
                                    case 1: {
                                        System.out.println("Add Account to Vault");
                                        System.out.println("-------------------");
                                        System.out.println("Enter your Email :");
                                        System.out.println("-------------------");
                                        String emailVault = scanner.next();
                                        String passwordVault;
                                        System.out.println("Do u want to generate password ? \n" +
                                                "1-Yes \n" +
                                                "2-No \n");
                                        System.out.println("Enter your choice ==>");
                                        int generateChoice = scanner.nextInt();

                                        if (generateChoice == 1) {
                                            int length = 10;

                                            String pass = new Random().ints(length, 0, 4)
                                                    .mapToObj(i -> {
                                                        switch (i) {
                                                            case 0:
                                                                return "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                                                            case 1:
                                                                return "abcdefghijklmnopqrstuvwxyz";
                                                            case 2:
                                                                return "0123456789";
                                                            case 3:
                                                                return "@$!%*?&";
                                                            default:
                                                                return "";
                                                        }
                                                    })
                                                    .map(s -> s.charAt(new Random().nextInt(s.length())))
                                                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                                                    .toString();
                                            passwordVault = pass;
                                            System.out.println("Generated Password: " + pass);
                                        } else {
                                            System.out.println("Enter your Password :");
                                            System.out.println("-------------------");
                                            String pass = scanner.next();
                                            passwordVault = pass;
                                        }

                                        System.out.println("Enter Description :");
                                        System.out.println("-------------------");
                                        String DescriptionVault = scanner.next();

                                        boolean isStrongPassword = Stream.of(passwordVault)
                                                .anyMatch(p -> p.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"));

                                        if (isStrongPassword) {
                                            //    Account account = new Account(passwordVault, emailVault, userLogin.getId(), DescriptionVault, 1);
                                            Account account = new Account(passwordVault, emailVault, userLogin.getId(), 0, DescriptionVault, 1);
//                                        manageVault.addInVault(account);
                                            executorService.submit(() -> {
                                                try {
                                                    manageVault.addInVault(account);
                                                } catch (SQLException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            });
                                        } else {
                                            System.out.println("Cant add to vault \n");
                                            System.out.println("Password requirements \n" +
                                                    "--At least one lowercase letter.\n" +
                                                    "--At least one uppercase letter.\n" +
                                                    "--At least one digit.\n" +
                                                    "--At least one special character from the set @, $, !, %, *, ?, or &.\n" +
                                                    "--The password must be at least 8 characters long.");
                                        }
                                        logger.info("Account Add in vault");
                                        break;

                                    }
                                    case 2: {
                                        Future<List<Account>> futureUserAccounts = executorService.submit(() -> manageVault.ViewAll(userLogin.getId()));
                                        List<Account> userAccounts = futureUserAccounts.get();

                                        System.out.println(" View All Accounts");
                                        System.out.println("-------------------");

                                        int i = 1;
                                        for (Account account : userAccounts) {

                                            System.out.println("---------" + i++ + " --------");
                                            System.out.println("Email :" + account.getEmail());
                                            System.out.println("password :" + account.getPassword());
                                            System.out.println("description :" + account.getWebsite());
                                            System.out.println("Password created in :" + account.getStart_date());
                                            System.out.println("Password ended in :" + account.getEnd_date());

                                            System.out.println("-------------------");
                                            LocalDate currentDate = LocalDate.now();
                                            if (currentDate.isBefore(account.getEnd_date())) {
                                                System.out.println("Password still valid");
                                            } else {

                                                System.out.println("You should change your password");
                                            }
                                        }


                                        List<String> filteredPasswords = userAccounts.stream()
                                                .map(Account::getPassword)
                                                .filter(p -> p.length() >= 8)
                                                .collect(Collectors.toList());

                                        System.out.println(filteredPasswords);
                                        Iterator<Account> iterator = userAccounts.iterator();
                                        while (iterator.hasNext()) {
                                            Account account = iterator.next();
                                            iterator.remove();
                                        }


                                        break;

                                    }

                                    case 3: {
                                        Future<List<Account>> futureUserAccounts = executorService.submit(() -> manageVault.ViewAll(userLogin.getId()));
                                        List<Account> userAccounts = futureUserAccounts.get();

                                        Account accountUpdate = new Account();
                                        System.out.println("Update Account");

                                        int i = 1;
                                        for (Account account : userAccounts) {
                                            System.out.println("---------" + i++ + " --------");
                                            System.out.println("Email: " + account.getEmail());
                                            System.out.println("Password: " + account.getPassword());
                                            System.out.println("Description: " + account.getWebsite());
                                            System.out.println("-------------------");
                                        }

                                        System.out.println("Choose the account you want to update:");
                                        int updateChoice = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.println("If you need to update your Email, type it; if you don't, press enter:");
                                        System.out.println("-------------------");
                                        String emailUpdated = scanner.nextLine();
                                        System.out.println("If you need to update your password, type it; if you don't, press enter:");
                                        System.out.println("-------------------");
                                        String passwordUpdate = scanner.nextLine();
                                        System.out.println("If you need to update your description, type it; if you don't, press enter:");
                                        System.out.println("-------------------");
                                        String descriptionUpdate = scanner.nextLine();

                                        if (emailUpdated.equals("")) {
                                            accountUpdate.setEmail(userAccounts.get(updateChoice - 1).getEmail());
                                        } else {
                                            accountUpdate.setEmail(emailUpdated);
                                        }

                                        if (passwordUpdate.equals("")) {

                                            accountUpdate.setPassword(userAccounts.get(updateChoice - 1).getPassword());
                                        } else {
                                            boolean isStrongPassword = Stream.of(passwordUpdate)
                                                    .anyMatch(p -> p.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"));
                                            if (isStrongPassword == true) {
                                                LocalDate currentDate = LocalDate.now();
                                                int days = 1;
                                                LocalDate endDate = currentDate.plusDays(days);
                                                accountUpdate.setStart_date(currentDate);
                                                accountUpdate.setEnd_date(endDate);
                                                accountUpdate.setPassword(passwordUpdate);
                                                String Subject = "Password-Manger";
                                                String Reciver = userLogin.getEmail();

                                                String Content = "U updated password of " + userAccounts.get(updateChoice - 1).getWebsite();
                                                executorService.submit(() -> {
                                                    EmailSender.sendEmail(Reciver, Subject, Content);
                                                });

                                            } else {
                                                System.out.println("Cant update \n");
                                                System.out.println("Password requirements \n" +
                                                        "--At least one lowercase letter.\n" +
                                                        "--At least one uppercase letter.\n" +
                                                        "--At least one digit.\n" +
                                                        "--At least one special character from the set @, $, !, %, *, ?, or &.\n" +
                                                        "--The password must be at least 8 characters long.");
                                            }

                                        }

                                        if (descriptionUpdate.equals("")) {
                                            accountUpdate.setWebsite(userAccounts.get(updateChoice - 1).getWebsite());
                                        } else {
                                            accountUpdate.setWebsite(descriptionUpdate);
                                        }

                                        accountUpdate.setUser_id(userLogin.getId());
                                        accountUpdate.setId(userAccounts.get(updateChoice - 1).getId());
//                                    manageVault.UpdateAccount(accountUpdate);
                                        executorService.submit(() -> {
                                            try {
                                                manageVault.UpdateAccount(accountUpdate);
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });

                                        break;
                                    }
                                    case 4: {
                                        System.out.println("4. Delete Account");
                                        Future<List<Account>> futureUserAccounts = executorService.submit(() -> manageVault.ViewAll(userLogin.getId()));
                                        List<Account> userAccounts = futureUserAccounts.get();

                                        int i = 1;
                                        for (Account account : userAccounts) {
                                            System.out.println("---------" + i++ + " --------");
                                            System.out.println("Email: " + account.getEmail());
                                            System.out.println("Password: " + account.getPassword());
                                            System.out.println("Description: " + account.getWebsite());
                                            System.out.println("-------------------");
                                        }
                                        System.out.println("Select any Account to delete it");
                                        int deleteAccount = scanner.nextInt();

                                        executorService.submit(() -> {
                                            try {
                                                manageVault.Delete(userAccounts.get(deleteAccount - 1).getId());
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                                        String Subject = "Password-Manger";
                                        String Reciver = userLogin.getEmail();
                                        String Content = "U deleted account " + userAccounts.get(deleteAccount - 1).getWebsite();
                                        executorService.submit(() -> {
                                            EmailSender.sendEmail(Reciver, Subject, Content);
                                        });
                                        break;
                                    }

                                    case 5: {
                                        y = false;
                                        break;
                                    }
                                    default:
                                        break;

                                }
                            }
                        } else {


                                System.out.println("Hello Admin" + " " + username);
                                System.out.println("Welcome to Admin Dashboard");
                                System.out.println("-------------------");
                                System.out.println("1. @@@@@@@@");
                                System.out.println("2. @@@@@@@@");
                                System.out.println("3. @@2222222");
                                System.out.println("4. @@@@@@@@");
                                System.out.println("5. Exit");
                                System.out.println("-------------------");
                                System.out.print("Enter your choice: ");

                        }

                    }
                    break;
                }

                case 2:
                    System.out.println("--------------");
                    System.out.println("Username: ");
                    String username = scanner.next();

                    System.out.println("Password: ");
                    String password = scanner.next();
                    System.out.println("Email: ");
                    String email = scanner.next();
                    System.out.println("--------------");
                    boolean isStrongPassword = Stream.of(password)
                            .anyMatch(p -> p.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"));
                    if (isStrongPassword) {
                        User user = new User(false,0, username, email, password);

                        service.Register(user);
                        String Subject="Password-Manger";
                        String Reciver=email;
                        String Content="Welcomt to Family";
                        executorService.submit(()->{
                            EmailSender.sendEmail(Reciver,Subject,Content);
                        });
                    } else {
                        System.out.println("Failed to Register");
                        System.out.println("Password requirements \n" +
                                "--At least one lowercase letter.\n" +
                                "--At least one uppercase letter.\n" +
                                "--At least one digit.\n" +
                                "--At least one special character from the set @, $, !, %, *, ?, or &.\n" +
                                "--The password must be at least 8 characters long.");
                    }

            break;

                case 3:
                    x=false;
                    break;
                default:
                    break;
            }
        }

        executorService.shutdown();


    }
}



    package Operations;

    import org.mindrot.jbcrypt.BCrypt;

    public class Hashing {

        public static String hashPassword(String password) {

            return BCrypt.hashpw(password, BCrypt.gensalt());
        }

        public static boolean verifyPassword(String password, String hashedPassword) {
            return BCrypt.checkpw(password, hashedPassword);
        }
    }

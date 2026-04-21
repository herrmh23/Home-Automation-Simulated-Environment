package userAuthentication;



import java.io.*;

//Class to handle storage and authentication of User information.
public class UserManager {
	
	// naming our user text file.
    public static final String FILE_NAME = "users.txt";
    
    public static boolean hasAnyUsers() throws IOException {
    	File file = new File(FILE_NAME);

    	if (!file.exists()) {
    	return false;
    	}

    	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    	String line;

    	while ((line = br.readLine()) != null) {
    	String[] parts = line.split(":");

    	if (parts.length >= 3) {
    	return true;
    				}
    			}
    		}

    	return false;
    	}
    
    
    public static boolean adminExists() throws IOException {
    	File file = new File(FILE_NAME);

    	if (!file.exists()) {
    	return false;
    	
    	}

    	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    	String line;

    	while ((line = br.readLine()) != null) {
    	String[] parts = line.split(":");

    	if (parts.length < 3) {
    	continue;
    	
    	}

    	Role role = Role.valueOf(parts[2]);

    	if (role == Role.ADMIN) {
    	return true;
    	
    				}
    			}
    		}

    	return false;
    	}

    // New user registration.
    public static boolean registerUser(String u, String p) throws IOException {
        File file = new File(FILE_NAME);

        if (u == null || u.trim().isEmpty()) {
            System.out.println("Username cannot be empty.");
            return false;
        }

        if (p == null || p.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }

        u = u.trim();

        if (userExists(u)) {
            System.out.println("Username already exists.");
            return false;
        }

        String hashed = PasswordUtil.hashPass(p);
        Role role;

        if (!file.exists() || file.length() == 0) {
            role = Role.ADMIN;
            System.out.println("Creating first ADMIN account.");
        } else {
            if (!Session.isAdmin()) {
                System.out.println("Access denied. Only admins can create users.");
                return false;
            }

            role = Role.GUEST;
        }

        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(u + ":" + hashed + ":" + role + "\n");
        }

        return true;
    }

    public static boolean userExists(String username) throws IOException {
        File file = new File(FILE_NAME);

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        username = username.trim();

        if (!file.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");

                if (parts.length < 3) {
                    continue;
                }

                String storedUsername = parts[0];

                if (storedUsername.equalsIgnoreCase(username)) {
                    return true;
                }
            }
        }

        return false;
    }

    // User Authentication
    public static User authenticate(String u, String p) throws IOException {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return null;
        }

        String hashedInput = PasswordUtil.hashPass(p);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            // looping through our users.txt file, checking if the user name and password match
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");

                if (parts.length < 3) {
                    continue;
                }

                String storedUser = parts[0];
                String storedHash = parts[1];
                Role role = Role.valueOf(parts[2]);

                // if both user name and password match, the login is successful
                if (storedUser.equalsIgnoreCase(u.trim()) && storedHash.equals(hashedInput)) {
                    return new User(storedUser, storedHash, role);
                }
            }
        }

        return null;
    }
}

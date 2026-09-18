package com.mycompany.loginsystem;

import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

public class LoginSystem {

    // ==================== DECLARATIONS ====================
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, User> userDatabase = new TreeMap<>();
    private static final List<Message> sentMessages = new ArrayList<>();

    private static User loggedInUser;

    // ==================== MAIN APPLICATION ====================
    public static void main(String[] args) {

        printBanner();

        boolean running = true;

        while (running) {

            printMainMenu();

            switch (scanner.nextLine().trim()) {

                case "1":
                    registerUser();
                    break;

                case "2":
                    if (loginUser()) {
                        launchChat();
                    }
                    break;

                case "3":
                    running = false;
                    System.out.println(
                            "\nGoodbye! Thank you for using LoginSystem."
                    );
                    break;

                default:
                    System.out.println(
                            "\n[ERROR] Invalid option. Please enter 1, 2, or 3."
                    );
                    break;
            }
        }

        scanner.close();
    }

    // ==================== BANNER ====================
    private static void printBanner() {

        System.out.println("*************************************************");
        System.out.println("*              LOGIN SYSTEM APP                 *");
        System.out.println("*************************************************");
    }

    // ==================== MAIN MENU ====================
    private static void printMainMenu() {

        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("\nChoice (1-3): ");
    }

    // ==================== REGISTER USER ====================
    private static void registerUser() {

        System.out.println("\n--- REGISTER ---");

        String username = prompt("Username: ").trim();
        String password = prompt("Password: ");
        String phone = prompt("Phone (e.g. +2717563370): ").trim();
        String firstName = prompt("First name: ").trim();
        String lastName = prompt("Last name: ").trim();

        Validator validator = new Validator();

        if (!validator.isValidUsername(username)) {

            System.out.println(
                    "[ERROR] Username must contain '_' and be 1 to 5 characters long."
            );

            return;
        }

        if (!validator.isValidPassword(password)) {

            System.out.println(
                    "[ERROR] Password must be at least 8 characters and contain "
                    + "an uppercase letter, a digit, and a special character."
            );

            return;
        }

        if (!validator.isValidPhone(phone)) {

            System.out.println(
                    "[ERROR] Phone must start with '+' and contain 1 to 10 digits "
                    + "after the plus sign."
            );

            return;
        }

        if (firstName.isBlank() || lastName.isBlank()) {

            System.out.println(
                    "[ERROR] First name and last name are required."
            );

            return;
        }

        if (userDatabase.containsKey(username)) {

            System.out.println(
                    "[ERROR] Username already taken."
            );

            return;
        }

        userDatabase.put(
                username,
                new User(
                        username,
                        password,
                        phone,
                        firstName,
                        lastName
                )
        );

        System.out.println("\n[SUCCESS] Account created!");
    }

    // ==================== LOGIN ====================
    private static boolean loginUser() {

        System.out.println("\n--- LOGIN ---");

        String username = prompt("Username: ").trim();
        String password = prompt("Password: ");

        User user = userDatabase.get(username);

        if (user != null && user.authenticate(username, password)) {

            loggedInUser = user;

            System.out.println(
                    "\n[SUCCESS] Welcome, " + user.getFirstName() + "!"
            );

            return true;
        }

        System.out.println(
                "[ERROR] Incorrect username or password."
        );

        return false;
    }

    // ==================== CHAT ====================
    private static void launchChat() {

        System.out.println("\n=== QuickChat ===");

        int limit = readPositiveInt(
                "How many messages would you like to send? "
        );

        List<Message> sessionMessages = new ArrayList<>();

        boolean active = true;

        while (active) {

            System.out.println("\n1. Send Message");
            System.out.println("2. View Messages");
            System.out.println("3. Quit");
            System.out.print("Choice: ");

            switch (scanner.nextLine().trim()) {

                case "1":
                    composeMessage(sessionMessages, limit);
                    break;

                case "2":
                    showMessages(sessionMessages);
                    break;

                case "3":
                    active = false;
                    break;

                default:
                    System.out.println(
                            "[ERROR] Invalid option. Please choose 1, 2, or 3."
                    );
                    break;
            }
        }

        loggedInUser = null;
    }

    // ==================== COMPOSE MESSAGE ====================
    private static void composeMessage(
            List<Message> sessionMessages,
            int limit
    ) {

        if (sessionMessages.size() >= limit) {

            System.out.println(
                    "[ERROR] Message limit reached."
            );

            return;
        }

        String recipient = prompt("Recipient: ").trim();
        String body = prompt("Message: ");

        if (recipient.isBlank()) {

            System.out.println(
                    "[ERROR] Recipient is required."
            );

            return;
        }

        if (body.isBlank()) {

            System.out.println(
                    "[ERROR] Message cannot be blank."
            );

            return;
        }

        if (body.length() > 250) {

            System.out.println(
                    "[ERROR] Message cannot be longer than 250 characters."
            );

            return;
        }

        Message message = new Message(
                recipient,
                body
        );

        sessionMessages.add(message);
        sentMessages.add(message);

        System.out.println(
                "[SUCCESS] Message sent!"
        );
    }

    // ==================== SHOW MESSAGES ====================
    private static void showMessages(List<Message> messages) {

        if (messages.isEmpty()) {

            System.out.println(
                    "No messages have been sent in this session."
            );

            return;
        }

        System.out.println("\n--- SENT MESSAGES ---");

        for (Message message : messages) {

            System.out.println(
                    message.getId()
                            + " -> "
                            + message.getRecipient()
                            + ": "
                            + message.getBody()
            );
        }
    }

    // ==================== PROMPT ====================
    private static String prompt(String text) {

        System.out.print(text);

        return scanner.nextLine();
    }

    // ==================== READ POSITIVE INTEGER ====================
    private static int readPositiveInt(String label) {

        while (true) {

            System.out.print(label);

            String input = scanner.nextLine().trim();

            try {

                int value = Integer.parseInt(input);

                if (value > 0) {
                    return value;
                }

                System.out.println(
                        "[ERROR] Please enter a number greater than zero."
                );

            } catch (NumberFormatException exception) {

                System.out.println(
                        "[ERROR] Please enter a valid whole number."
                );
            }
        }
    }
}


// ==================== USER ====================
final class User {

    private final String username;
    private final String passwordHash;
    private final String phone;
    private final String firstName;
    private final String lastName;

    public User(
            String username,
            String password,
            String phone,
            String firstName,
            String lastName
    ) {

        this.username = username;

        this.passwordHash = PasswordUtil.hash(password);

        this.phone = phone;

        this.firstName = firstName;

        this.lastName = lastName;
    }

    public boolean authenticate(
            String username,
            String password
    ) {

        return this.username.equals(username)
                && passwordHash.equals(
                        PasswordUtil.hash(password)
                );
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }
}


// ==================== PASSWORD UTILITY ====================
final class PasswordUtil {

    private PasswordUtil() {
        // Utility class; do not instantiate.
    }

    public static String hash(String value) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte currentByte : hash) {

                result.append(
                        String.format(
                                "%02x",
                                currentByte
                        )
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 is not available.",
                    exception
            );
        }
    }
}


// ==================== VALIDATOR ====================
final class Validator {

    public boolean isValidUsername(String username) {

        return username != null
                && username.matches("[A-Za-z0-9_]{1,5}")
                && username.contains("_");
    }

    public boolean isValidPassword(String password) {

        return password != null
                && password.matches(
                        "(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}"
                );
    }

    public boolean isValidPhone(String phone) {

        return phone != null
                && phone.matches("\\+\\d{1,10}");
    }
}


// ==================== MESSAGE ====================
final class Message {

    private final String id;
    private final String recipient;
    private final String body;

    public Message(
            String recipient,
            String body
    ) {

        this.id = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        this.recipient = recipient;

        this.body = body;
    }

    public String getId() {
        return id;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getBody() {
        return body;
    }
}
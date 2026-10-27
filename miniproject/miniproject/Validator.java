
public class Validator {

    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Account holder name cannot be empty."
            );
        }
        return name.trim();
    }

    public static long validateAmount(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero."
            );
        }
        return amount;
    }
}

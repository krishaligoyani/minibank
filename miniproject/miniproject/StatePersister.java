
import java.io.*;
import java.nio.file.*;
import model.Account;

public class StatePersister {

    public static void save(Account[] accounts, Path file)
            throws IOException {
        Path parent = file.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (ObjectOutputStream out = new ObjectOutputStream(
                Files.newOutputStream(file))) {
            out.writeObject(accounts);
        }
    }

    public static Account[] load(Path file)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                Files.newInputStream(file))) {
            return (Account[]) in.readObject();
        }
    }
}

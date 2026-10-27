
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile)
            throws IOException {

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        StringBuilder report = new StringBuilder();

        report.append("=== END-OF-DAY REPORT ===\n");

        if (Files.exists(logsFolder)) {
            try (DirectoryStream<Path> stream =
                    Files.newDirectoryStream(logsFolder)) {

                for (Path file : stream) {
                    if (!Files.isRegularFile(file)) {
                        continue;
                    }

                    BasicFileAttributes attrs =
                        Files.readAttributes(
                            file, BasicFileAttributes.class
                        );

                    report.append("\nFile: ")
                          .append(file.getFileName()).append("\n");

                    report.append("Size: ")
                          .append(attrs.size()).append(" bytes\n");

                    report.append("Last Modified: ")
                          .append(attrs.lastModifiedTime()).append("\n");

                    try (BufferedReader reader =
                            Files.newBufferedReader(
                                file, StandardCharsets.UTF_8)) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");

                            if (parts.length != 2) {
                                continue;
                            }

                            try {
                                long amount = Long.parseLong(
                                    parts[1].trim()
                                );

                                if (amount <= 0) {
                                    continue;
                                }

                                if (parts[0].trim().equals("DEPOSIT")) {
                                    totalDeposits += amount;
                                } else if (
                                    parts[0].trim().equals("WITHDRAW")
                                ) {
                                    totalWithdrawals += amount;
                                }
                            } catch (NumberFormatException e) {
                                report.append("Invalid log entry: ")
                                      .append(line).append("\n");
                            }
                        }
                    }
                }
            }
        }

        report.append("\nTotal Deposits: Rs. ")
              .append(totalDeposits).append("\n");

        report.append("Total Withdrawals: Rs. ")
              .append(totalWithdrawals).append("\n");

        Path parent = reportFile.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(
            reportFile,
            report.toString(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}

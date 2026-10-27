import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import annotation.Audit;

public class AuditLogger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(Class<?> clazz, String methodName) {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(Audit.class)) {
                    Audit audit = method.getAnnotation(Audit.class);
                    String time = LocalDateTime.now().format(FORMATTER);
                    System.out.println("[AUDIT " + time + "] " + method.getName() + " -> action: " + audit.action());
                    return;
                }
            }
        } catch (Exception e) {
        }
    }
}

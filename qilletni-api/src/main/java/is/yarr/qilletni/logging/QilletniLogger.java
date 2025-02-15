//package is.yarr.qilletni.logging;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.URL;
//import java.util.Optional;
//
//public class QilletniLogger {
//
//    /**
//     * Creates a logger with the JAR name of the calling class included in the logger name.
//     *
//     * @return A logger instance associated with the calling class's JAR file.
//     */
//    public static Logger getLogger() {
//        // Determine the calling class
//        Class<?> callingClass = getCallingClass();
//        String jarName = getJarName(callingClass).orElse("UnknownJar");
//
//        // Use the JAR name as part of the logger name
//        return LoggerFactory.getLogger("Jar[" + jarName + "] " + callingClass.getName());
//    }
//
//
//}
//    
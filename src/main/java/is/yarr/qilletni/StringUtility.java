package is.yarr.qilletni;

public class StringUtility {
    
    public static String removeQuotes(String string) {
        if (string.length() >= 2 && string.startsWith("\"") && string.endsWith("\"")) {
            return string.substring(1, string.length() - 1);
        }
        
        return string;
    }
    
}

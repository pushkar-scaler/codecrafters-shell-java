import java.util.*;
import org.jline.reader.*;
import org.jline.terminal.*;
public class test_widget4 {
    static int tabCount = 0;
    static String lastBuf = "";
    
    static String lcp(List<String> strings) {
        if (strings.isEmpty()) return "";
        String prefix = strings.get(0);
        for (String s : strings) {
            while (s.indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
        }
        return prefix;
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(lcp(Arrays.asList("xyz_bar", "xyz_baz")));
    }
}

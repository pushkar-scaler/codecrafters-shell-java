import java.util.*;
import org.jline.reader.*;
import org.jline.terminal.*;
public class test_widget3 {
    static int tabCount = 0;
    static String lastBuf = "";
    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
        
        List<String> names = Arrays.asList("xyz_bar", "xyz_baz", "xyz_quz", "echo", "exit");
        
        reader.getWidgets().put("my-tab", () -> {
            String buf = reader.getBuffer().toString();
            List<String> matches = new ArrayList<>();
            for (String n : names) if (n.startsWith(buf)) matches.add(n);
            
            if (matches.size() == 1) {
                String extra = matches.get(0).substring(buf.length()) + " ";
                reader.getBuffer().write(extra);
                tabCount = 0;
            } else if (matches.size() > 1) {
                if (!buf.equals(lastBuf)) { tabCount = 0; lastBuf = buf; }
                if (tabCount == 0) {
                    System.out.print("\007"); System.out.flush();
                    tabCount++;
                } else {
                    Collections.sort(matches);
                    System.out.println("");
                    System.out.println(String.join("  ", matches));
                    reader.callWidget(LineReader.REDRAW_LINE);
                    tabCount = 0;
                }
            }
            return true;
        });
        reader.getKeyMaps().get(LineReader.MAIN).bind(new Reference("my-tab"), "\t");
        
        System.out.println("Line: " + reader.readLine("$ "));
    }
}

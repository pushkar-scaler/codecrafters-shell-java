import org.jline.reader.*;
import org.jline.terminal.*;
public class test_widget {
    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();
        
        reader.getWidgets().put("my-tab", () -> {
            System.out.println("\nTAB PRESSED!");
            return true;
        });
        reader.getKeyMaps().get(LineReader.MAIN).bind("my-tab", "\t");
        
        System.out.println("Line: " + reader.readLine());
    }
}

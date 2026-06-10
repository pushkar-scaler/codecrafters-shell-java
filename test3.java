import org.jline.reader.*;
import org.jline.terminal.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
public class test3 {
    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        DefaultParser parser = new DefaultParser();
        parser.setEscapeChars(null); 
        parser.setQuoteChars(null);
        Completer completer = new StringsCompleter("echo", "exit");
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).parser(parser).completer(completer).build();
        System.out.println("Line: " + reader.readLine());
    }
}

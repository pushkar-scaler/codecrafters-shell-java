import org.jline.reader.*;
import org.jline.terminal.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
public class test6 {
    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        DefaultParser parser = new DefaultParser();
        parser.setEscapeChars(null); 
        parser.setQuoteChars(null);
        Completer completer = new ArgumentCompleter(new StringsCompleter("xyz_bar", "xyz_baz", "xyz_quz"), NullCompleter.INSTANCE);
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).parser(parser).completer(completer).build();
        System.out.println("Line: " + reader.readLine());
    }
}

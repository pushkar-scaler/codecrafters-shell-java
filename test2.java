import java.io.*;
import org.jline.reader.*;
import org.jline.terminal.*;
import org.jline.reader.impl.DefaultParser;
public class test2 {
    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        DefaultParser parser = new DefaultParser();
        parser.setEscapeChars(null); 
        parser.setQuoteChars(null);
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).parser(parser).build();
        System.out.println("Line: " + reader.readLine());
    }
}

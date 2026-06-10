import org.jline.reader.*;
public class test8 {
    public static void main(String[] args) {
        LineReaderBuilder builder = LineReaderBuilder.builder();
        builder.option(LineReader.Option.AUTO_MENU, false);
        builder.option(LineReader.Option.AUTO_LIST, true);
        builder.option(LineReader.Option.LIST_AMBIGUOUS, true);
        System.out.println("OK");
    }
}

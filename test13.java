import org.jline.reader.*;
public class test13 {
    public static void main(String[] args) throws Exception {
        LineReader reader = LineReaderBuilder.builder()
            .option(LineReader.Option.AUTO_MENU, false)
            .option(LineReader.Option.AUTO_LIST, true)
            .option(LineReader.Option.LIST_AMBIGUOUS, true)
            .build();
        System.out.println("AUTO_MENU: " + reader.isSet(LineReader.Option.AUTO_MENU));
        System.out.println("AUTO_LIST: " + reader.isSet(LineReader.Option.AUTO_LIST));
        System.out.println("LIST_AMBIGUOUS: " + reader.isSet(LineReader.Option.LIST_AMBIGUOUS));
    }
}

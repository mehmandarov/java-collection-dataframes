import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmojiHelperTest {

    @Test
    public void toFlagEmoji() {
        EmojiHelper emoji = new EmojiHelper();
        // Test a few countries
        Assertions.assertEquals("🇳🇴", emoji.toFlagEmoji("NO"));
        Assertions.assertEquals("🇪🇸", emoji.toFlagEmoji("ES"));
        // Test special cases
        Assertions.assertEquals("🌐", emoji.toFlagEmoji("WWW"));
        Assertions.assertEquals("🏳", emoji.toFlagEmoji("**unknown**"));
    }
}

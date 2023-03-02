import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CountryTest
{
    @Test
    public void fromName()
    {
        Assertions.assertSame(Country.SWEDEN, Country.fromName("Sweden"));
        Assertions.assertSame(Country.USA, Country.fromName("United States"));
        Assertions.assertNull(Country.fromName("States"));
    }
}

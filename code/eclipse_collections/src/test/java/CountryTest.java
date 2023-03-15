import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CountryTest
{
    @Test
    public void getFlag()
    {
        Assertions.assertEquals("🇺🇸", Country.newIfAbsent("United States", "US").flag());
        Assertions.assertEquals("🇺🇸", Country.newIfAbsent("USA", "US").flag());
        Assertions.assertEquals("🇺🇸", Country.newIfAbsent("usa", "us").flag());
        Assertions.assertEquals("🇬🇧", Country.newIfAbsent("UK", "GB").flag());
    }

    @Test
    public void caseInsensitiveLookup()
    {
        Assert.assertSame(Country.newIfAbsent("USA", "US"), Country.newIfAbsent("usa", "us"));
        Assert.assertNotSame(Country.newIfAbsent("USA", "US"), Country.newIfAbsent("United States", "us"));
    }
}

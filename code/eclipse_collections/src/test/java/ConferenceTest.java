import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConferenceTest
{
    @Test
    public void createDevoxxGreece()
    {
        Conference conference = new Conference("Devoxx Greece","Greece","Athens","2023-05-04","2023-05-06","[talks,workshops]");
        Assertions.assertTrue(conference.hasTalks());
        Assertions.assertTrue(conference.hasWorkshops());
        Assertions.assertEquals(Country.GREECE, conference.country());
        Assertions.assertEquals(3, conference.durationInDays());
    }

    @Test
    public void createJChampionsConf()
    {
        Conference conference = new Conference("jChampionsConf","WWW","Online event","2023-01-19","2023-01-24","[talks,]");
        Assertions.assertTrue(conference.hasTalks());
        Assertions.assertFalse(conference.hasWorkshops());
        Assertions.assertEquals(Country.WWW, conference.country());
        Assertions.assertEquals(6, conference.durationInDays());
    }
}

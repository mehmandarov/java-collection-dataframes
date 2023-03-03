import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.Multimap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConferenceExplorerTest
{
    @Test
    public void loadConferences()
    {
        ConferenceExplorer explorer = new ConferenceExplorer();
        Assertions.assertEquals(7, explorer.getConferences().size());
    }

    @Test
    public void groupByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer();
        Multimap<Country, Conference> byCountry = explorer.groupByCountry();
        RichIterable<Conference> conferences = byCountry.get(Country.GREECE);
        Assertions.assertEquals(1, conferences.size());
        Assertions.assertEquals("Athens", conferences.getFirst().city());
    }

    @Test
    public void groupByCity()
    {
        ConferenceExplorer explorer = new ConferenceExplorer();
        Multimap<String, Conference> byCountry = explorer.groupByCity();
        RichIterable<Conference> conferences = byCountry.get("Athens");
        Assertions.assertEquals(1, conferences.size());
        Assertions.assertEquals(Country.GREECE, conferences.getFirst().country());
    }

    @Test
    public void sortByDaysToEvent()
    {
        ConferenceExplorer explorer = new ConferenceExplorer();
        ImmutableList<Conference> conferences = explorer.sortByDaysToEvent();
        Conference closestEvent = conferences.getFirst();
        Assertions.assertEquals("jChampionsConf", closestEvent.eventName());
        Conference furthestEvent = conferences.getLast();
        Assertions.assertEquals("Devoxx Greece", furthestEvent.eventName());
    }
}

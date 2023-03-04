import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
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

    @Test
    public void groupBySessionTypes()
    {
        ConferenceExplorer explorer = new ConferenceExplorer();
        ImmutableSetMultimap<SessionType, Conference> bySessionType = explorer.groupBySessionType();
        Assertions.assertEquals(7, bySessionType.get(SessionType.TALK).size());
        Assertions.assertEquals(6, bySessionType.get(SessionType.WORKSHOP).size());
        RichIterable<Conference> difference =
                bySessionType.get(SessionType.TALK).difference(bySessionType.get(SessionType.WORKSHOP));
        Assertions.assertEquals("jChampionsConf", difference.getFirst().eventName());
    }

    @Test
    public void getCountries()
    {
        ConferenceExplorer explorer = new ConferenceExplorer();
        ImmutableSet<String> flags = explorer.getCountries().collect(Country::getFlag);
        ImmutableSet<String> expectedFlags = Sets.immutable.with("🇬🇷", "🇵🇱", "🇺🇸", "🇩🇪", "🇷🇴", "🇸🇪", "🕸");
        Assertions.assertEquals(expectedFlags, flags);
    }

}

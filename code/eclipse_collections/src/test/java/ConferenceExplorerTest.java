import java.time.Month;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
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
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Assertions.assertEquals(7, explorer.getConferences().size());
    }

    @Test
    public void groupByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Multimap<Country, Conference> byCountry = explorer.groupByCountry();
        RichIterable<Conference> conferences = byCountry.get(Country.GREECE);
        Assertions.assertEquals(1, conferences.size());
        Assertions.assertEquals("Athens", conferences.getFirst().city());
    }

    @Test
    public void groupByCity()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Multimap<String, Conference> byCountry = explorer.groupByCity();
        RichIterable<Conference> conferences = byCountry.get("Athens");
        Assertions.assertEquals(1, conferences.size());
        Assertions.assertEquals(Country.GREECE, conferences.getFirst().country());
    }

    @Test
    public void sortByDaysToEvent()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        ImmutableList<Conference> conferences = explorer.sortByDaysToEvent();
        Conference closestEvent = conferences.getFirst();
        Assertions.assertEquals("jChampionsConf", closestEvent.eventName());
        Conference furthestEvent = conferences.getLast();
        Assertions.assertEquals("Devoxx Greece", furthestEvent.eventName());
    }

    @Test
    public void groupBySessionTypes()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
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
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        ImmutableSet<String> flags = explorer.getCountries().collect(Country::getFlag);
        ImmutableSet<String> expectedFlags = Sets.immutable.with("🇬🇷", "🇵🇱", "🇺🇸", "🇩🇪", "🇷🇴", "🇸🇪", "🕸");
        Assertions.assertEquals(expectedFlags, flags);
    }

    @Test
    public void countByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Bag<Country> expected = Bags.immutable.with(
                Country.SWEDEN,
                Country.USA,
                Country.ROMANIA,
                Country.GERMANY,
                Country.GREECE,
                Country.WWW,
                Country.POLAND);
        Assertions.assertEquals(expected, explorer.countByCountry());
    }

    @Test
    public void countBySessionType()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Bag<SessionType> expected = Bags.immutable.withOccurrences(
                SessionType.TALK, 7,
                SessionType.WORKSHOP, 6);
        Assertions.assertEquals(expected, explorer.countBySessionType());
    }

    @Test
    public void countByMonth()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        MutableBag<Month> expected = Bags.mutable.empty();
        expected.addOccurrences(Month.JANUARY, 1);
        expected.addOccurrences(Month.FEBRUARY, 1);
        expected.addOccurrences(Month.MARCH, 2);
        expected.addOccurrences(Month.APRIL, 2);
        expected.addOccurrences(Month.MAY, 1);
        Assertions.assertEquals(expected, explorer.countByMonth());
    }

    @Test
    public void conferenceDaysByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        ObjectLongMap<Country> expected = ObjectLongMaps.mutable.<Country>empty()
                .withKeyValue(Country.SWEDEN, 3L)
                .withKeyValue(Country.USA, 3L)
                .withKeyValue(Country.ROMANIA, 3L)
                .withKeyValue(Country.GREECE, 3L)
                .withKeyValue(Country.GERMANY, 3L)
                .withKeyValue(Country.WWW, 6L)
                .withKeyValue(Country.POLAND, 3L);
        Assertions.assertEquals(expected, explorer.conferenceDaysByCountry());
    }
}

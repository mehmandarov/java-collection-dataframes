import java.time.Month;
import java.util.HashMap;
import java.util.Map;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConferenceExplorerTest
{
    @Test
    public void loadConferences()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Assertions.assertEquals(7, explorer.getConferences().rowCount());
    }

    @Test
    public void sortByDaysToEvent()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame conferences = explorer.sortByDaysToEvent();
        String closestEvent = conferences.getValue("EventName", 0).stringValue();
        Assertions.assertEquals("jChampionsConf", closestEvent);
        String furthestEvent = conferences.getValue("EventName", conferences.rowCount() - 1).stringValue();
        Assertions.assertEquals("Devoxx Greece", furthestEvent);
    }

    @Test
    public void countByMonth()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<Month, Long> expected = new HashMap<>();
        expected.put(Month.JANUARY, 1L);
        expected.put(Month.FEBRUARY, 1L);
        expected.put(Month.MARCH, 2L);
        expected.put(Month.APRIL, 2L);
        expected.put(Month.MAY, 1L);
        DataFrame countByMonth = explorer.countByMonth();
        expected.forEach((k, v) -> {
            DataFrame result = countByMonth.selectBy("Month == '" + k + "' and MonthCount == " + v.longValue());
            Assertions.assertEquals(1, result.rowCount());
        });
    }

    // @Test
    // public void groupByCountry()
    // {
    //     ConferenceExplorer explorer = new ConferenceExplorer(2023);
    //     Map<Country, Set<Conference>> byCountry = explorer.groupByCountry();
    //     Set<Conference> conferences = byCountry.get(Country.GREECE);
    //     Assertions.assertEquals(1, conferences.size());
    //     Assertions.assertEquals("Athens", conferences.iterator().next().city());
    // }
    //
    // @Test
    // public void groupByCity()
    // {
    //     ConferenceExplorer explorer = new ConferenceExplorer(2023);
    //     Map<String, Set<Conference>> byCountry = explorer.groupByCity();
    //     Set<Conference> conferences = byCountry.get("Athens");
    //     Assertions.assertEquals(1, conferences.size());
    //     Assertions.assertEquals(Country.GREECE, conferences.iterator().next().country());
    // }
    //
    // @Test
    // public void groupBySessionTypes()
    // {
    //     ConferenceExplorer explorer = new ConferenceExplorer(2023);
    //     Map<SessionType, Set<Conference>> bySessionType = explorer.groupBySessionType();
    //     Assertions.assertEquals(7, bySessionType.get(SessionType.TALK).size());
    //     Assertions.assertEquals(6, bySessionType.get(SessionType.WORKSHOP).size());
    //     Set<Conference> difference =
    //             bySessionType.get(SessionType.TALK).stream()
    //                     .filter(each -> !bySessionType.get(SessionType.WORKSHOP).contains(each))
    //                     .collect(Collectors.toSet());
    //     Assertions.assertEquals("jChampionsConf", difference.iterator().next().eventName());
    // }
    //
    // @Test
    // public void getCountries()
    // {
    //     ConferenceExplorer explorer = new ConferenceExplorer(2023);
    //     Set<String> flags = explorer.getCountries().stream()
    //             .map(Country::getFlag)
    //             .collect(Collectors.toSet());
    //     Set<String> expectedFlags = Set.of("🇬🇷", "🇵🇱", "🇺🇸", "🇩🇪", "🇷🇴", "🇸🇪", "🕸");
    //     Assertions.assertEquals(expectedFlags, flags);
    // }
    //
    // @Test
    // public void countByCountry()
    // {
    //     ConferenceExplorer explorer = new ConferenceExplorer(2023);
    //     Map<Country, Long> expected = new HashMap<>();
    //     expected.put(Country.SWEDEN, 1L);
    //     expected.put(Country.USA, 1L);
    //     expected.put(Country.ROMANIA, 1L);
    //     expected.put(Country.GERMANY, 1L);
    //     expected.put(Country.GREECE, 1L);
    //     expected.put(Country.WWW, 1L);
    //     expected.put(Country.POLAND, 1L);
    //     Assertions.assertEquals(expected, explorer.countByCountry());
    // }
    //
    // @Test
    // public void countBySessionType()
    // {
    //     ConferenceExplorer explorer = new ConferenceExplorer(2023);
    //     Map<SessionType, Long> expected = new HashMap<>();
    //     expected.put(SessionType.TALK, 7L);
    //     expected.put(SessionType.WORKSHOP, 6L);
    //     Assertions.assertEquals(expected, explorer.countBySessionType());
    // }
    // @Test
    // public void conferenceDaysByCountry()
    // {
    //     ConferenceExplorer explorer = new ConferenceExplorer(2023);
    //     Map<Country, Long> expected = new HashMap<>();
    //     expected.put(Country.SWEDEN, 3L);
    //     expected.put(Country.USA, 3L);
    //     expected.put(Country.ROMANIA, 3L);
    //     expected.put(Country.GREECE, 3L);
    //     expected.put(Country.GERMANY, 3L);
    //     expected.put(Country.WWW, 6L);
    //     expected.put(Country.POLAND, 3L);
    //     Assertions.assertEquals(expected, explorer.conferenceDaysByCountry());
    // }
}

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

    @Test
    public void countByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<String, Long> expected = new HashMap<>();
        expected.put("SWEDEN", 1L);
        expected.put("UNITED STATES", 1L);
        expected.put("ROMANIA", 1L);
        expected.put("GERMANY", 1L);
        expected.put("GREECE", 1L);
        expected.put("WWW", 1L);
        expected.put("POLAND", 1L);
        DataFrame countByMonth = explorer.countByCountry();
        expected.forEach((k, v) -> {
            DataFrame result = countByMonth.selectBy("toUpper(Country) == '" + k + "' and CountryCount == " + v.longValue());
            Assertions.assertEquals(1, result.rowCount());
        });
    }

    @Test
    public void conferenceDaysByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<String, Long> expected = new HashMap<>();
        expected.put("SWEDEN", 3L);
        expected.put("UNITED STATES", 3L);
        expected.put("ROMANIA", 3L);
        expected.put("GREECE", 3L);
        expected.put("GERMANY", 3L);
        expected.put("WWW", 6L);
        expected.put("POLAND", 3L);
        DataFrame sumByCountry = explorer.conferenceDaysByCountry();
        expected.forEach((k, v) -> {
            DataFrame result = sumByCountry.selectBy("toUpper(Country) == '" + k + "' and Duration == " + v.longValue());
            Assertions.assertEquals(1, result.rowCount());
        });
    }

    @Test
    public void groupByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame byCountry = explorer.groupByCountry();
        DataFrame conferences = byCountry.selectBy("toUpper(Country) == 'GREECE'");
        Assertions.assertEquals(1, conferences.rowCount());
        Assertions.assertEquals("Athens", conferences.getValue("City", 0).stringValue());
    }

    @Test
    public void groupByCity()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame byCity = explorer.groupByCity();
        DataFrame conferences = byCity.selectBy("City == 'Athens'");
        Assertions.assertEquals(1, conferences.rowCount());
        Assertions.assertEquals("Greece", conferences.getValue("Country", 0).stringValue());
    }

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
    // @Test
    // public void countBySessionType()
    // {
    //     ConferenceExplorer explorer = new ConferenceExplorer(2023);
    //     Map<SessionType, Long> expected = new HashMap<>();
    //     expected.put(SessionType.TALK, 7L);
    //     expected.put(SessionType.WORKSHOP, 6L);
    //     Assertions.assertEquals(expected, explorer.countBySessionType());
    // }
}

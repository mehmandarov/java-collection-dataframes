import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        Map<Country, Set<Conference>> byCountry = explorer.groupByCountry();
        Set<Conference> conferences = byCountry.get(Country.GREECE);
        Assertions.assertEquals(1, conferences.size());
        Assertions.assertEquals("Athens", conferences.iterator().next().city());
    }

    @Test
    public void groupByCity()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<String, Set<Conference>> byCountry = explorer.groupByCity();
        Set<Conference> conferences = byCountry.get("Athens");
        Assertions.assertEquals(1, conferences.size());
        Assertions.assertEquals(Country.GREECE, conferences.iterator().next().country());
    }

    @Test
    public void sortByDaysToEvent()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        List<Conference> conferences = explorer.sortByDaysToEvent();
        Conference closestEvent = conferences.get(0);
        Assertions.assertEquals("jChampionsConf", closestEvent.eventName());
        Conference furthestEvent = conferences.get(conferences.size() - 1);
        Assertions.assertEquals("Devoxx Greece", furthestEvent.eventName());
    }

    @Test
    public void groupBySessionTypes()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<SessionType, Set<Conference>> bySessionType = explorer.groupBySessionType();
        Assertions.assertEquals(7, bySessionType.get(SessionType.TALK).size());
        Assertions.assertEquals(6, bySessionType.get(SessionType.WORKSHOP).size());
        Set<Conference> difference =
                bySessionType.get(SessionType.TALK).stream()
                        .filter(each -> !bySessionType.get(SessionType.WORKSHOP).contains(each))
                        .collect(Collectors.toSet());
        Assertions.assertEquals("jChampionsConf", difference.iterator().next().eventName());
    }

    @Test
    public void getCountries()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Set<String> flags = explorer.getCountries().stream()
                .map(Country::getFlag)
                .collect(Collectors.toSet());
        Set<String> expectedFlags = Set.of("🇬🇷", "🇵🇱", "🇺🇸", "🇩🇪", "🇷🇴", "🇸🇪", "🕸");
        Assertions.assertEquals(expectedFlags, flags);
    }
}

import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public abstract class AbstractConferenceExplorer
{
    private Set<Conference> conferences;
    private Predicate<Conference> initialFilter;

    public AbstractConferenceExplorer(Predicate<Conference> filterPredicate)
    {
        this.initialFilter = filterPredicate;
        this.loadConferencesFromCsv();
    }

    private void loadConferencesFromCsv()
    {
        CsvSchema headerSchema = CsvSchema.emptySchema().withHeader();
        URL url = AbstractConferenceExplorer.class.getClassLoader().getResource("data/conferences.csv");
        final CsvMapper mapper = new CsvMapper();
        try (
                MappingIterator<Map<String, String>> it = mapper
                        .readerForMapOf(String.class)
                        .with(headerSchema)
                        .readValues(url))
        {
            List<Map<String, String>> lists = it.readAll();
            this.conferences = lists.stream()
                    .map(this::createConference)
                    .filter(this.initialFilter())
                    .collect(Collectors.toUnmodifiableSet());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Conference createConference(Map<String, String> map)
    {
        return new Conference(
                map.get("Event Name"),
                map.get("Country"),
                map.get("City"),
                map.get("Start Date"),
                map.get("End Date"),
                map.get("Session Types"));
    }

    public Predicate<Conference> initialFilter()
    {
        return this.initialFilter;
    }

    public Set<Conference> getConferences()
    {
        return this.conferences;
    }

    public Map<Country, Set<Conference>> groupByCountry()
    {
        return this.conferences.stream()
                .collect(Collectors.groupingBy(Conference::country, Collectors.toUnmodifiableSet()));
    }

    public Map<String, Set<Conference>> groupByCity()
    {
        return this.conferences.stream()
                .collect(Collectors.groupingBy(Conference::city, Collectors.toUnmodifiableSet()));
    }

    public List<Conference> sortByDaysToEvent()
    {
        return this.conferences.stream()
                .sorted(Comparator.comparing(Conference::daysToEvent))
                .toList();
    }

    public Map<SessionType, Set<Conference>> groupBySessionType()
    {
        return Map.copyOf(this.conferences.stream()
                .flatMap(conference -> conference.sessionTypes().stream()
                        .map(sessionType -> new AbstractMap.SimpleEntry<>(sessionType, conference)))
                .collect(Collectors.groupingBy(
                        AbstractMap.SimpleEntry::getKey,
                        Collectors.mapping(AbstractMap.SimpleEntry::getValue, Collectors.toUnmodifiableSet()))));
    }

    public Set<Country> getCountries()
    {
        return this.conferences.stream()
                .map(Conference::country)
                .collect(Collectors.toUnmodifiableSet());
    }
}

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class ConferenceExplorer
{
    private static final String CSV_CONFERENCES = """
            Event Name,Country,City,Start Date,End Date,Session Types
            Devoxx Greece,Greece,Athens,2023-05-04,2023-05-06,"[talks,workshops]"
            GeeCon,Poland,Krakow,2023-04-19,2023-04-21,"[talks,workshops]"
            DevNexus,United States,Atlanta,2023-04-04,2023-04-06,"[talks,workshops]"
            JavaLand,Germany,Brühl,2023-03-21,2023-03-23,"[talks,workshops]"
            Voxxed Days Bucharest,Romania,Bucharest,2023-03-22,2023-03-24,"[talks,workshops]"
            Jfokus,Sweden,Stockholm,2023-02-06,2023-02-08,"[talks,workshops]"
            jChampionsConf,WWW,Online event,2023-01-19,2023-01-24,"[talks,]"
            """;

    private Set<Conference> conferences;

    public ConferenceExplorer()
    {
        this.loadConferencesFromCsv();
    }

    private void loadConferencesFromCsv()
    {
        List<Conference> tempConferences = new ArrayList<>();
        CsvSchema headerSchema = CsvSchema.emptySchema().withHeader();
        final CsvMapper mapper = new CsvMapper();
        try (
                MappingIterator<Map<String, String>> it = mapper
                        .readerForMapOf(String.class)
                        .with(headerSchema)
                        .readValues(CSV_CONFERENCES))
        {
            List<Map<String, String>> lists = it.readAll();
            for (Map<String, String> r : lists)
            {
                Conference conference =
                        new Conference(
                                r.get("Event Name"),
                                r.get("Country"),
                                r.get("City"),
                                r.get("Start Date"),
                                r.get("End Date"),
                                r.get("Session Types"));
                tempConferences.add(conference);
            }
            this.conferences = Set.copyOf(tempConferences);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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
                        .map(sessionType -> new SimpleEntry<>(sessionType, conference)))
                .collect(Collectors.groupingBy(
                        SimpleEntry::getKey,
                        Collectors.mapping(SimpleEntry::getValue, Collectors.toUnmodifiableSet()))));
    }

    public Set<Country> getCountries()
    {
        return this.conferences.stream()
                .map(Conference::country)
                .collect(Collectors.toUnmodifiableSet());
    }
}

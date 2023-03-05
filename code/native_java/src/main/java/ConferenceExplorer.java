import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class ConferenceExplorer
{
    private static String CSV_CONFERENCES = """
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
        try
        {
            MappingIterator<Map<String, String>> it = mapper
                    .readerForMapOf(String.class)
                    .with(headerSchema)
                    .readValues(CSV_CONFERENCES);
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
        List<Conference> list = this.conferences.stream()
                .collect(Collectors.toList());
        list.sort(Comparator.comparing(Conference::daysToEvent));
        return List.copyOf(list);
    }

    public Map<SessionType, Set<Conference>> groupBySessionType()
    {
        Map<SessionType, Set<Conference>> result = new HashMap<>();
        for (Conference conference : this.conferences)
        {
            for (SessionType sessionType : conference.sessionTypes())
            {
                result.computeIfAbsent(sessionType, st -> new HashSet<>()).add(conference);
            }
        }
        result.replaceAll((key, value) -> Set.copyOf(value));
        return Map.copyOf(result);
    }

    public Set<Country> getCountries()
    {
        return this.conferences.stream()
                .map(Conference::country)
                .collect(Collectors.toUnmodifiableSet());
    }
}

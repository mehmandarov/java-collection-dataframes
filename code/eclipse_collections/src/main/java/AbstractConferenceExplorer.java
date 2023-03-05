import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;

public abstract class AbstractConferenceExplorer
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
    private ImmutableSet<Conference> conferences;

    protected void loadConferencesFromCsv()
    {
        MutableList<Conference> tempConferences = Lists.mutable.empty();
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
            this.conferences = tempConferences.select(this.filterPredicate()).toImmutableSet();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public abstract Predicate<Conference> filterPredicate();

    public ImmutableSet<Conference> getConferences()
    {
        return this.conferences;
    }

    public ImmutableSetMultimap<Country, Conference> groupByCountry()
    {
        return this.getConferences().groupBy(Conference::country);
    }

    public ImmutableSetMultimap<String, Conference> groupByCity()
    {
        return this.getConferences().groupBy(Conference::city);
    }

    public ImmutableList<Conference> sortByDaysToEvent()
    {
        return this.getConferences().toImmutableSortedListBy(Conference::daysToEvent);
    }

    public ImmutableSetMultimap<SessionType, Conference> groupBySessionType()
    {
        return this.getConferences().groupByEach(Conference::sessionTypes);
    }

    public ImmutableSet<Country> getCountries()
    {
        return this.getConferences().collect(Conference::country);
    }
}

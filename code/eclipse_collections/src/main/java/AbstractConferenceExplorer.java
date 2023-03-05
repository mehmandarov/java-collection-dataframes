import java.io.IOException;
import java.net.URL;
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
    private ImmutableSet<Conference> conferences;
    private Predicate<Conference> filterPredicate;

    public AbstractConferenceExplorer(Predicate<Conference> filterPredicate)
    {
        this.filterPredicate = filterPredicate;
    }

    protected void loadConferencesFromCsv()
    {
        MutableList<Conference> tempConferences = Lists.mutable.empty();
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

    public Predicate<Conference> filterPredicate()
    {
        return this.filterPredicate;
    }

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

import java.io.IOException;
import java.net.URL;
import java.time.Month;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.utility.LazyIterate;

public abstract class AbstractConferenceExplorer
{
    private ImmutableSet<Conference> conferences;
    private final Predicate<Conference> initialFilter;

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
            this.conferences = LazyIterate.collect(lists, this::createConference)
                    .select(this.initialFilter())
                    .toImmutableSet();
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

    public Bag<Country> countByCountry()
    {
        return this.conferences.countBy(Conference::country);
    }

    public Bag<SessionType> countBySessionType()
    {
        return this.conferences.countByEach(Conference::sessionTypes);
    }

    public Bag<Month> countByMonth()
    {
        return this.conferences.countBy(Conference::getMonth);
    }

    public ObjectLongMap<Country> conferenceDaysByCountry()
    {
        return this.conferences.sumByLong(Conference::country, Conference::durationInDays);
    }
}

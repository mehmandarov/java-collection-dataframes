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

public class ConferenceExplorer
{
    private final Predicate<Conference> initialFilter;
    private ImmutableSet<Conference> conferences;
    private ImmutableSet<Country> countries;

    public ConferenceExplorer(int year)
    {
        this(yearPredicate(year));
    }

    public ConferenceExplorer(Predicate<Conference> initialFilter)
    {
        this.initialFilter = initialFilter;
        this.loadCountriesFromCsv();
        this.loadConferencesFromCsv();
    }

    private static Predicate<Conference> yearPredicate(int year)
    {
        return conference -> conference.startDate().getYear() == year;
    }

    private void loadConferencesFromCsv()
    {
        CsvSchema headerSchema = CsvSchema.emptySchema().withHeader();
        URL url = ConferenceExplorer.class.getClassLoader().getResource("data/conferences.csv");
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

    private void loadCountriesFromCsv()
    {
        CsvSchema headerSchema = CsvSchema.emptySchema().withHeader();
        URL url = ConferenceExplorer.class.getClassLoader().getResource("data/country_codes.csv");
        final CsvMapper mapper = new CsvMapper();
        try (
                MappingIterator<Map<String, String>> it = mapper
                        .readerForMapOf(String.class)
                        .with(headerSchema)
                        .readValues(url))
        {
            List<Map<String, String>> lists = it.readAll();
            this.countries = LazyIterate.collect(lists, this::createCountry)
                    .toImmutableSet();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Country createCountry(Map<String, String> map)
    {
        return Country.newIfAbsent(
                map.get("Country"),
                map.get("Alpha2Code")
        );
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

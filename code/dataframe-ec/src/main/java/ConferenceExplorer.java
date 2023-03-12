import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;
import io.github.vmzakharov.ecdataframe.dataset.CsvSchema;
import io.github.vmzakharov.ecdataframe.dsl.function.BuiltInFunctions;
import io.github.vmzakharov.ecdataframe.dsl.function.IntrinsicFunctionDescriptor;
import io.github.vmzakharov.ecdataframe.dsl.value.DateValue;
import io.github.vmzakharov.ecdataframe.dsl.value.LongValue;
import io.github.vmzakharov.ecdataframe.dsl.value.Value;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import io.github.vmzakharov.ecdataframe.dsl.value.VectorValue;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;

public class ConferenceExplorer
{
    private DataFrame conferences;

    public ConferenceExplorer(int year)
    {
        this.loadConferencesFromCsv("StartDate.year = " + year);
    }

    private void loadConferencesFromCsv(String initialFilter)
    {
        CsvSchema conferenceSchema = new CsvSchema()
                .separator(',');
        conferenceSchema.addColumn("EventName", ValueType.STRING);
        conferenceSchema.addColumn("Country", ValueType.STRING);
        conferenceSchema.addColumn("City", ValueType.STRING);
        conferenceSchema.addColumn("StartDate", ValueType.DATE);
        conferenceSchema.addColumn("EndDate", ValueType.DATE);
        conferenceSchema.addColumn("SessionTypes", ValueType.STRING);

        URL url = ConferenceExplorer.class.getClassLoader().getResource("data/conferences.csv");
        DataFrame dataFrame = new CsvDataSet(url.getPath(), "Conferences", conferenceSchema).loadAsDataFrame();
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptor("daysUntil")
        {
            @Override
            public Value evaluate(VectorValue parameters)
            {
                return new LongValue(ChronoUnit.DAYS.between(LocalDate.now(), ((DateValue) parameters.get(0)).dateValue()));
            }

            @Override
            public ValueType returnType(ListIterable<ValueType> parameterTypes)
            {
                return ValueType.LONG;
            }
        });
        dataFrame.attachColumn(
                dataFrame.createComputedColumn(
                        "DaysToEvent",
                        ValueType.LONG,
                        "daysUntil(StartDate)"));
        // TODO apply filter to data
        this.conferences = dataFrame;
    }

    public DataFrame getConferences()
    {
        return this.conferences;
    }

    // public DataFrame groupByCountry()
    // {
    //     return this.conferences.stream()
    //             .collect(Collectors.groupingBy(Conference::country, Collectors.toUnmodifiableSet()));
    // }
    //
    // public Map<String, Set<Conference>> groupByCity()
    // {
    //     return this.conferences.stream()
    //             .collect(Collectors.groupingBy(Conference::city, Collectors.toUnmodifiableSet()));
    // }

    public DataFrame sortByDaysToEvent()
    {
        return this.conferences.sortBy(Lists.immutable.with("DaysToEvent"));
    }

    // public Map<SessionType, Set<Conference>> groupBySessionType()
    // {
    //     return Map.copyOf(this.conferences.stream()
    //             .flatMap(conference -> conference.sessionTypes().stream()
    //                     .map(sessionType -> new SimpleEntry<>(sessionType, conference)))
    //             .collect(Collectors.groupingBy(
    //                     SimpleEntry::getKey,
    //                     Collectors.mapping(SimpleEntry::getValue, Collectors.toUnmodifiableSet()))));
    // }
    //
    // public Set<Country> getCountries()
    // {
    //     return this.conferences.stream()
    //             .map(Conference::country)
    //             .collect(Collectors.toUnmodifiableSet());
    // }
    //
    // public Map<Country, Long> countByCountry()
    // {
    //     return this.conferences.stream()
    //             .collect(Collectors.groupingBy(Conference::country, Collectors.counting()));
    // }
    //
    // public Map<SessionType, Long> countBySessionType()
    // {
    //     return this.conferences.stream()
    //             .flatMap(conference -> conference.sessionTypes().stream()
    //                     .map(sessionType -> new SimpleEntry<>(sessionType, conference)))
    //             .collect(Collectors.groupingBy(SimpleEntry::getKey, Collectors.counting()));
    // }
    //
    // public Map<Month, Long> countByMonth()
    // {
    //     return this.conferences.stream()
    //             .collect(Collectors.groupingBy(Conference::getMonth, Collectors.counting()));
    // }
    //
    // public Map<Country, Long> conferenceDaysByCountry()
    // {
    //     return this.conferences.stream().
    //             collect(Collectors.groupingBy(Conference::country, Collectors.summingLong(Conference::durationInDays)));
    // }
}

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction;
import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;
import io.github.vmzakharov.ecdataframe.dataset.CsvSchema;
import io.github.vmzakharov.ecdataframe.dsl.function.BuiltInFunctions;
import io.github.vmzakharov.ecdataframe.dsl.function.IntrinsicFunctionDescriptor;
import io.github.vmzakharov.ecdataframe.dsl.value.DateValue;
import io.github.vmzakharov.ecdataframe.dsl.value.LongValue;
import io.github.vmzakharov.ecdataframe.dsl.value.StringValue;
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
        this.loadConferencesFromCsv("yearOf(StartDate) == " + year);
    }

    private void loadConferencesFromCsv(String initialFilter)
    {
        CsvSchema conferenceSchema = new CsvSchema().separator(',');
        conferenceSchema.addColumn("EventName", ValueType.STRING);
        conferenceSchema.addColumn("Country", ValueType.STRING);
        conferenceSchema.addColumn("City", ValueType.STRING);
        conferenceSchema.addColumn("StartDate", ValueType.DATE);
        conferenceSchema.addColumn("EndDate", ValueType.DATE);
        conferenceSchema.addColumn("SessionTypes", ValueType.STRING);

        URL url = ConferenceExplorer.class.getClassLoader().getResource("data/conferences.csv");
        DataFrame dataFrame = new CsvDataSet(url.getPath(), "Conferences", conferenceSchema).loadAsDataFrame();
        ConferenceExplorer.addDaysUntilFunction();
        ConferenceExplorer.addYearFunction();
        ConferenceExplorer.addMonthFunction();
        dataFrame.attachColumn(dataFrame.createComputedColumn("DaysToEvent", ValueType.LONG, "daysUntil(StartDate)"));
        dataFrame.attachColumn(dataFrame.createComputedColumn("Month", ValueType.STRING, "monthOf(StartDate)"));
        // TODO apply filter to data
        this.conferences = dataFrame.selectBy(initialFilter);
    }

    private static void addDaysUntilFunction()
    {
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
    }

    private static void addYearFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptor("yearOf")
        {
            @Override
            public Value evaluate(VectorValue parameters)
            {
                return new LongValue(((DateValue) parameters.get(0)).dateValue().getYear());
            }

            @Override
            public ValueType returnType(ListIterable<ValueType> parameterTypes)
            {
                return ValueType.LONG;
            }
        });
    }

    private static void addMonthFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptor("monthOf")
        {
            @Override
            public Value evaluate(VectorValue parameters)
            {
                return new StringValue(((DateValue) parameters.get(0)).dateValue().getMonth().toString());
            }

            @Override
            public ValueType returnType(ListIterable<ValueType> parameterTypes)
            {
                return ValueType.STRING;
            }
        });
    }

    public DataFrame getConferences()
    {
        return this.conferences;
    }

    public DataFrame sortByDaysToEvent()
    {
        return this.conferences.sortBy(Lists.immutable.with("DaysToEvent"));
    }

    public DataFrame countByMonth()
    {
        return this.conferences.aggregateBy(Lists.immutable.with(AggregateFunction.count("Month", "MonthCount")), Lists.immutable.with("Month"));
    }

    public DataFrame countByCountry()
    {
        return this.conferences.aggregateBy(Lists.immutable.with(AggregateFunction.count("Country", "CountryCount")), Lists.immutable.with("Country"));
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
    // public Map<Country, Long> conferenceDaysByCountry()
    // {
    //     return this.conferences.stream().
    //             collect(Collectors.groupingBy(Conference::country, Collectors.summingLong(Conference::durationInDays)));
    // }
}

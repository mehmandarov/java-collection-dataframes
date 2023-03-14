import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction;
import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataframe.DfJoin;
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

import javax.xml.crypto.Data;

import static io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction.count;

public class ConferenceExplorer
{
    private DataFrame conferences;
    private DataFrame countryCodes;

    public ConferenceExplorer(int year)
    {
        this.loadConferencesFromCsv("yearOf(StartDate) == " + year);
        this.loadCountryCodesFromCsv();
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
        ConferenceExplorer.addDurationFunction();
        ConferenceExplorer.addYearFunction();
        ConferenceExplorer.addMonthFunction();
        ConferenceExplorer.addExtractSessionTypesFunction();
        dataFrame.attachColumn(dataFrame.createComputedColumn("DaysToEvent", ValueType.LONG, "daysUntil(StartDate)"));
        dataFrame.attachColumn(dataFrame.createComputedColumn("Duration", ValueType.LONG, "durationInDays(StartDate, EndDate)"));
        dataFrame.attachColumn(dataFrame.createComputedColumn("Month", ValueType.STRING, "monthOf(StartDate)"));
        // TODO apply filter to data
        this.conferences = dataFrame.selectBy(initialFilter);
    }

    private void loadCountryCodesFromCsv()
    {
        CsvSchema countryCodesSchema = new CsvSchema().separator(',');
        countryCodesSchema.addColumn("Country", ValueType.STRING);
        countryCodesSchema.addColumn("Alpha2Code", ValueType.STRING);

        URL url = ConferenceExplorer.class.getClassLoader().getResource("data/country_codes.csv");
        DataFrame countryCodesDataFrame = new CsvDataSet(url.getPath(), "CountryCodes", countryCodesSchema).loadAsDataFrame();

        ConferenceExplorer.addFlagEmojiFunction();

        this.countryCodes = countryCodesDataFrame;
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

    private static void addDurationFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptor("durationInDays")
        {
            @Override
            public Value evaluate(VectorValue parameters)
            {
                return new LongValue(ChronoUnit.DAYS.between(((DateValue) parameters.get(0)).dateValue(), ((DateValue) parameters.get(1)).dateValue().plusDays(1L)));
            }

            @Override
            public ValueType returnType(ListIterable<ValueType> parameterTypes)
            {
                return ValueType.LONG;
            }
        });
    }

    private static void addFlagEmojiFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptor("toFlagEmoji")
        {
            @Override
            public Value evaluate(VectorValue parameters)
            {
                EmojiHelper emojiHelper = new EmojiHelper();
                return new StringValue(emojiHelper.toFlagEmoji(parameters.get(0).stringValue()));
            }

            @Override
            public ValueType returnType(ListIterable<ValueType> parameterTypes)
            {
                return ValueType.STRING;
            }
        });
    }

    private static void addExtractSessionTypesFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptor("extractSessionTypes")
        {
            @Override
            public Value evaluate(VectorValue parameters)
            {
                String sessionTypes = parameters.get(0).stringValue().toLowerCase();
                String lookupSessionType = parameters.get(1).stringValue().toLowerCase();
                if (sessionTypes.contains(lookupSessionType))
                {
                    return new StringValue("1");
                }
                else
                {
                    return new StringValue("0");
                }
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

    public DataFrame conferenceDaysByCountry()
    {
        return this.conferences.sumBy(Lists.immutable.with("Duration"), Lists.immutable.with("Country"));
    }

    public DataFrame groupByCountry()
    {
        return this.conferences.sortBy(Lists.immutable.with("Country"));
    }

    public DataFrame groupByCity()
    {
        return this.conferences.sortBy(Lists.immutable.with("City"));
    }


    public DataFrame getCountries()
    {
        // TODO: Fix this workaround when a new version of DataFrame-EC gets released with 'unique()'.
        DataFrame countByCountry = this.conferences.aggregateBy(
                Lists.immutable.of(count("Country", "ConfCount")),
                Lists.immutable.of("Country")
        ).keepColumns(Lists.immutable.of("Country"));

        // Join two dataframes on 'Country' key. Add '**unknown**' if there is no hit in the countryCodes dataframe.
        countByCountry.lookup(DfJoin.to(this.countryCodes)
                .match("Country", "Country")
                .select("Alpha2Code")
                .ifAbsent("**unknown**"));
        countByCountry.sortBy(Lists.immutable.of("Country"));

        // Generate flag emojis using the two-letter country code and add them as a new column.
        countByCountry.addColumn("Flag", ValueType.STRING, "toFlagEmoji( Alpha2Code )");

        return countByCountry;
    }

    public Map<String, DataFrame> groupBySessionType()
    {
        DataFrame sessionTypePivot = this.conferences.copy("Conference Session Type Pivot");
        sessionTypePivot.addColumn("HasTalks", ValueType.STRING, "extractSessionTypes( SessionTypes, \"talks\" )");
        sessionTypePivot.addColumn("HasWorkshops", ValueType.STRING, "extractSessionTypes( SessionTypes, \"workshops\" )");

        Map<String, DataFrame> groupedBySessionTypes = new HashMap<>();
        groupedBySessionTypes.put("talks", sessionTypePivot.selectBy("HasTalks == \"1\""));
        groupedBySessionTypes.put("workshops", sessionTypePivot.selectBy("HasWorkshops == \"1\""));
        return groupedBySessionTypes;
    }


     public Map<String, Long> countBySessionType()
     {
         Map<String, DataFrame> groupedBySessionTypes = groupBySessionType();
         Map<String, Long> countBySessionTypes = new HashMap<>();
         countBySessionTypes.put("talks", (long) groupedBySessionTypes.get("talks").rowCount());
         countBySessionTypes.put("workshops", (long) groupedBySessionTypes.get("workshops").rowCount());
         return countBySessionTypes;
     }
}

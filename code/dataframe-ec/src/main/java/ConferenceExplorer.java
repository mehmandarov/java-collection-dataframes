import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction;
import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataframe.DfIndex;
import io.github.vmzakharov.ecdataframe.dataframe.DfJoin;
import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;
import io.github.vmzakharov.ecdataframe.dataset.CsvSchema;
import io.github.vmzakharov.ecdataframe.dsl.function.BuiltInFunctions;
import io.github.vmzakharov.ecdataframe.dsl.function.IntrinsicFunctionDescriptorBuilder;
import io.github.vmzakharov.ecdataframe.dsl.value.LongValue;
import io.github.vmzakharov.ecdataframe.dsl.value.StringValue;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;

public class ConferenceExplorer
{
    private DataFrame conferences;
    private DataFrame countryCodes;

    public ConferenceExplorer(int year)
    {
        this("yearOf(StartDate) == " + year);
    }

    public ConferenceExplorer(String intialFilter)
    {
        this.loadCountryCodesFromCsv();
        this.loadConferencesFromCsv(intialFilter);
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
        dataFrame.addColumn("DaysToEvent", "daysUntil(StartDate)");
        dataFrame.addColumn("Duration", "durationInDays(StartDate, EndDate)");
        dataFrame.addColumn("Month", "monthOf(StartDate)");
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
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptorBuilder("daysUntil")
                .parameterNames("date")
                .returnType(ValueType.LONG)
                .action(context -> new LongValue(ChronoUnit.DAYS.between(LocalDate.now(), context.getDate("date")))));
    }

    private static void addYearFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptorBuilder("yearOf")
                .parameterNames("date")
                .returnType(ValueType.LONG)
                .action(context -> new LongValue(context.getDate("date").getYear())));
    }

    private static void addMonthFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptorBuilder("monthOf")
                .parameterNames("date")
                .returnType(ValueType.STRING)
                .action(context -> new StringValue(context.getDate("date").getMonth().toString())));
    }

    private static void addDurationFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptorBuilder("durationInDays")
                .parameterNames("from", "to")
                .returnType(ValueType.LONG)
                .action(context -> new LongValue(ChronoUnit.DAYS.between(context.getDate("from"), context.getDate("to").plusDays(1L)))));
    }

    private static void addFlagEmojiFunction()
    {
        BuiltInFunctions.addFunctionDescriptor(new IntrinsicFunctionDescriptorBuilder("toFlagEmoji")
                .parameterNames("countryCode")
                .returnType(ValueType.STRING)
                .action(context -> new StringValue(new CountryToFlagUtil().toFlagEmoji(context.getString("countryCode")))));
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
        return this.conferences.aggregateBy(
                Lists.immutable.with(AggregateFunction.count("Month", "MonthCount")),
                Lists.immutable.with("Month"));
    }

    public DataFrame countByCountry()
    {
        return this.conferences.aggregateBy(
                Lists.immutable.with(AggregateFunction.count("Country", "CountryCount")),
                Lists.immutable.with("Country"));
    }

    public DataFrame conferenceDaysByCountry()
    {
        return this.conferences.sumBy(
                Lists.immutable.with("Duration"),
                Lists.immutable.with("Country"));
    }

    public DfIndex groupByCountry()
    {
        DfIndex index = new DfIndex(this.conferences, Lists.immutable.with("Country"));
        return index;
    }

    public DfIndex groupByCity()
    {
        DfIndex index = new DfIndex(this.conferences, Lists.immutable.with("City"));
        return index;
    }

    public DataFrame getCountries()
    {
        // Get distinct list of countries
        DataFrame distinctCountries = this.conferences.distinct(Lists.immutable.of("Country"));

        // Join two dataframes on 'Country' key. Add '**unknown**' if there is no hit in the countryCodes dataframe.
        distinctCountries.lookup(DfJoin.to(this.countryCodes)
                .match("Country", "Country")
                .select("Alpha2Code")
                .ifAbsent("**unknown**"));
        distinctCountries.sortBy(Lists.immutable.of("Country"));

        // Generate flag emojis using the two-letter country code and add them as a new column.
        distinctCountries.addColumn("Flag", "toFlagEmoji( Alpha2Code )");

        return distinctCountries;
    }

    public Map<String, DataFrame> groupBySessionType()
    {
        Map<String, DataFrame> groupedBySessionTypes = Maps.mutable.empty();
        groupedBySessionTypes.put("talks", this.conferences.selectBy("contains( SessionTypes, 'talks')"));
        groupedBySessionTypes.put("workshops", this.conferences.selectBy("contains( SessionTypes, 'workshops')"));
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

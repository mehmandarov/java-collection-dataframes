import java.util.HashMap;
import java.util.Map;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataframe.util.DataFrameCompare;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConferenceExplorerTest
{
    @Test
    public void loadConferences()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Assertions.assertEquals(7, explorer.getConferences().rowCount());
    }

    @Test
    public void sortByDaysToEvent()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame conferences = explorer.sortByDaysToEvent();
        String closestEvent = conferences.getString("EventName", 0);
        Assertions.assertEquals("jChampionsConf", closestEvent);
        String furthestEvent = conferences.getString("EventName", conferences.rowCount() - 1);
        Assertions.assertEquals("Devoxx Greece", furthestEvent);
    }

    @Test
    public void countByMonth()
    {
        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Month").addLongColumn("MonthCount")
                .addRow("JANUARY", 1L)
                .addRow("FEBRUARY", 1L)
                .addRow("MARCH", 2L)
                .addRow("APRIL", 2L)
                .addRow("MAY", 1L);

        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame countByMonth = explorer.countByMonth();
        Assertions.assertTrue(new DataFrameCompare().equalIgnoreOrder(expected, countByMonth));
    }

    @Test
    public void countByCountry()
    {
        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Country").addLongColumn("CountryCount")
                .addRow("Sweden", 1L)
                .addRow("United States", 1L)
                .addRow("Romania", 1L)
                .addRow("Germany", 1L)
                .addRow("Greece", 1L)
                .addRow("WWW", 1L)
                .addRow("Poland", 1L);

        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame countByCountry = explorer.countByCountry();
        Assertions.assertTrue(new DataFrameCompare().equalIgnoreOrder(expected, countByCountry));
    }

    @Test
    public void conferenceDaysByCountry()
    {
        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Country").addLongColumn("Duration")
                .addRow("Sweden", 3L)
                .addRow("United States", 3L)
                .addRow("Romania", 3L)
                .addRow("Germany", 3L)
                .addRow("Greece", 3L)
                .addRow("WWW", 6L)
                .addRow("Poland", 3L);

        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame sumByCountry = explorer.conferenceDaysByCountry();
        Assertions.assertTrue(new DataFrameCompare().equalIgnoreOrder(expected, sumByCountry));
    }

    @Test
    public void groupByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame byCountry = explorer.groupByCountry();
        byCountry.index("ByCountry")
                .iterateAt("Greece")
                .forEach(c -> Assertions.assertEquals("Athens", c.getString("City")));
    }

    @Test
    public void groupByCity()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame byCity = explorer.groupByCity();
        byCity.index("ByCity")
                .iterateAt("Athens")
                .forEach(c -> Assertions.assertEquals("Greece", c.getString("Country")));
    }

    @Test
    public void getCountries()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame flags = explorer.getCountries().getColumnNamed("Flag").getDataFrame();

        DataFrame expectedFlags = new DataFrame("Flag");
        expectedFlags.addColumn("Flag", ValueType.STRING);
        Map<String, String> expected = new HashMap<>();
        expected.put("SWEDEN", "🇸🇪");
        expected.put("UNITED STATES", "🇺🇸");
        expected.put("ROMANIA", "🇷🇴");
        expected.put("GERMANY", "🇩🇪");
        expected.put("GREECE", "🇬🇷");
        expected.put("WWW", "🌐");
        expected.put("POLAND", "🇵🇱");

        expected.forEach((k, v) -> {
            DataFrame result = flags.selectBy("toUpper(Country) == '" + k + "' and Flag == '" + v + "'");
            String error = "Unexpected results for Country: %s and Flag: %s.";
            Assertions.assertEquals(1, result.rowCount(), String.format(error, k, v));
        });
    }

    @Test
    public void groupBySessionTypes()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<String, DataFrame> bySessionType = explorer.groupBySessionType();

        Assertions.assertEquals(7, bySessionType.get("talks").rowCount());
        Assertions.assertEquals(6, bySessionType.get("workshops").rowCount());

        Assertions.assertEquals(1, bySessionType.get("talks").selectBy("EventName == 'jChampionsConf'").rowCount());
        Assertions.assertEquals(0, bySessionType.get("workshops").selectBy("EventName == 'jChampionsConf'").rowCount());
    }

    @Test
    public void countBySessionType()
    {
        DataFrame expected = new DataFrame("Expected")
                .addStringColumn("Session Type").addLongColumn("Count")
                .addRow("talks", 7L)
                .addRow("workshops", 6L);
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame countBySessionType = explorer.countBySessionType();
        Assertions.assertTrue(new DataFrameCompare().equalIgnoreOrder(expected, countBySessionType));
    }

    @Test
    public void output()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        String s = explorer.outputToJson(explorer.getConferences());
        Assertions.assertNotNull(s);
        System.out.println(s);
        String s2 = explorer.outputToJson(explorer.countByMonth());
        System.out.println(s2);
        String s3 = explorer.outputToJson(explorer.getCountries());
        System.out.println(s3);
        String s4 = explorer.outputToJson(explorer.conferenceDaysByCountry());
        System.out.println(s4);
        String s5 = explorer.outputToJson(explorer.countBySessionType());
        System.out.println(s5);
    }
}

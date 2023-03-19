import java.time.Month;
import java.util.HashMap;
import java.util.Map;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
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
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<Month, Long> expected = new HashMap<>();
        expected.put(Month.JANUARY, 1L);
        expected.put(Month.FEBRUARY, 1L);
        expected.put(Month.MARCH, 2L);
        expected.put(Month.APRIL, 2L);
        expected.put(Month.MAY, 1L);
        DataFrame countByMonth = explorer.countByMonth();
        expected.forEach((k, v) -> {
            DataFrame result = countByMonth.selectBy("Month == '" + k + "' and MonthCount == " + v.longValue());
            Assertions.assertEquals(1, result.rowCount());
        });
    }

    @Test
    public void countByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<String, Long> expected = new HashMap<>();
        expected.put("SWEDEN", 1L);
        expected.put("UNITED STATES", 1L);
        expected.put("ROMANIA", 1L);
        expected.put("GERMANY", 1L);
        expected.put("GREECE", 1L);
        expected.put("WWW", 1L);
        expected.put("POLAND", 1L);
        DataFrame countByMonth = explorer.countByCountry();
        expected.forEach((k, v) -> {
            DataFrame result = countByMonth.selectBy("toUpper(Country) == '" + k + "' and CountryCount == " + v.longValue());
            Assertions.assertEquals(1, result.rowCount());
        });
    }

    @Test
    public void conferenceDaysByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        Map<String, Long> expected = new HashMap<>();
        expected.put("SWEDEN", 3L);
        expected.put("UNITED STATES", 3L);
        expected.put("ROMANIA", 3L);
        expected.put("GREECE", 3L);
        expected.put("GERMANY", 3L);
        expected.put("WWW", 6L);
        expected.put("POLAND", 3L);
        DataFrame sumByCountry = explorer.conferenceDaysByCountry();
        expected.forEach((k, v) -> {
            DataFrame result = sumByCountry.selectBy("toUpper(Country) == '" + k + "' and Duration == " + v.longValue());
            Assertions.assertEquals(1, result.rowCount());
        });
    }

    @Test
    public void groupByCountry()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame byCountry = explorer.groupByCountry();
        DataFrame conferences = byCountry.selectBy("toUpper(Country) == 'GREECE'");
        Assertions.assertEquals(1, conferences.rowCount());
        Assertions.assertEquals("Athens", conferences.getString("City", 0));
    }

    @Test
    public void groupByCity()
    {
        ConferenceExplorer explorer = new ConferenceExplorer(2023);
        DataFrame byCity = explorer.groupByCity();
        DataFrame conferences = byCity.selectBy("City == 'Athens'");
        Assertions.assertEquals(1, conferences.rowCount());
        Assertions.assertEquals("Greece", conferences.getString("Country", 0));
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
         ConferenceExplorer explorer = new ConferenceExplorer(2023);
         Map<String, Long> expected = new HashMap<>();
         expected.put("talks", 7L);
         expected.put("workshops", 6L);
         Assertions.assertEquals(expected, explorer.countBySessionType());
     }
}

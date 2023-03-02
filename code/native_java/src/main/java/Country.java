import java.util.stream.Stream;

public enum Country
{
    GREECE("Greece", "🇬🇷"),
    POLAND("Poland", "🇵🇱"),
    USA("United States", "🇺🇸"),
    GERMANY("Germany", "🇩🇪"),
    ROMANIA("Romania", "🇷🇴"),
    SWEDEN("Sweden", "🇸🇪"),
    WWW("WWW", "🕸");

    private final String name;
    private final String flag;

    public static Country fromName(String name)
    {
        return Stream.of(Country.values())
                .filter(country -> name.equalsIgnoreCase(country.getName()))
                .findFirst()
                .orElse(null);
    }

    Country(String name, String flag)
    {
        this.name = name;
        this.flag = flag;
    }

    public String getName()
    {
        return name;
    }

    public String getFlag()
    {
        return flag;
    }
}

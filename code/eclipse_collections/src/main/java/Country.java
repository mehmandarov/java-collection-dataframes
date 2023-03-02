import org.eclipse.collections.impl.utility.ArrayIterate;

public enum Country
{
    GREECE("Greece", "🇬🇷"),
    POLAND("Poland", "🇵🇱"),
    USA("United States", "🇺🇸"),
    GERMANY("Germany", "🇩🇪"),
    ROMANIA("Romania", "🇷🇴"),
    SWEDEN("Sweden", "🇸🇪"),
    WWW("WWW", "🕸");

    private String name;
    private String flag;

    public static Country fromName(String name)
    {
        return ArrayIterate.detect(Country.values(), country -> name.equalsIgnoreCase(country.getName()));
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

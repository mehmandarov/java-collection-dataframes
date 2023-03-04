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

    private final String name;
    private final String flag;

    public static Country fromName(String name)
    {
        Country country = ArrayIterate.detect(Country.values(), each -> name.equalsIgnoreCase(each.getName()));
        if (country == null)
        {
            throw new NullPointerException("No country named: " + name);
        }
        return country;
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

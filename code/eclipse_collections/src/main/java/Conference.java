import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.eclipse.collections.api.list.ImmutableList;

public record Conference(
        String eventName,
        Country country,
        String city,
        LocalDate startDate,
        LocalDate endDate,
        ImmutableList<SessionType> sessionTypes)
{
    public Conference(String eventName, String country, String city, String startDate, String endDate, String sessionTypes)
    {
        this(eventName,
                Country.fromName(country),
                city,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
                SessionType.listFromString(sessionTypes));
    }

    public long durationInDays()
    {
        return ChronoUnit.DAYS.between(this.startDate, this.endDate.plusDays(1L));
    }

    public long daysToEvent()
    {
        return ChronoUnit.DAYS.between(LocalDate.now(), this.startDate);
    }

    public boolean hasWorkshops()
    {
        return this.sessionTypes.anySatisfy(SessionType::isWorkshop);
    }

    public boolean hasTalks()
    {
        return this.sessionTypes.anySatisfy(SessionType::isTalk);
    }

    public String countryFlag()
    {
        return this.country.getFlag();
    }

    public String countryName()
    {
        return this.country.getName();
    }
}

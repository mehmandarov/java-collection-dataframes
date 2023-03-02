import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.eclipse.collections.api.set.ImmutableSet;

public record Conference(
        String eventName,
        Country country,
        String city,
        LocalDate startDate,
        LocalDate endDate,
        ImmutableSet<SessionType> sessionTypes)
{
    public Conference(String eventName, String country, String city, String startDate, String endDate, String sessionTypes)
    {
        this(eventName,
                Country.fromName(country),
                city,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
                SessionType.setFromString(sessionTypes));
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
        return this.sessionTypes.contains(SessionType.WORKSHOP);
    }

    public boolean hasTalks()
    {
        return this.sessionTypes.contains(SessionType.TALK);
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

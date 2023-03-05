import org.eclipse.collections.api.block.predicate.Predicate;

public class ConferenceExplorer extends AbstractConferenceExplorer
{
    public ConferenceExplorer(int year)
    {
        this(yearPredicate(year));
    }

    public ConferenceExplorer(Predicate<Conference> initialFilter)
    {
        super(initialFilter);
        this.loadConferencesFromCsv();
    }

    private static Predicate<Conference> yearPredicate(int year)
    {
        return conference -> conference.startDate().getYear() == year;
    }
}

import org.eclipse.collections.api.block.predicate.Predicate;

public class ConferenceExplorer extends AbstractConferenceExplorer
{
    private final int year;
    public ConferenceExplorer(int year)
    {
        this.year = year;
        this.loadConferencesFromCsv();
    }

    public int getYear()
    {
        return this.year;
    }

    public Predicate<Conference> filterPredicate()
    {
        return conference -> conference.startDate().getYear() == year;
    }
}

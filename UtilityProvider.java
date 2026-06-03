package objectville;

public abstract class UtilityProvider extends Cell implements Provider<Utility> {

    public static final int CAPACITY = 100;

    protected UtilityProvider(int row, int col) { super(row, col); }

    public abstract Utility utility();
    public int capacity() { return CAPACITY; }

    @Override public Utility provides() { return utility(); }

    @Override protected void indexInto(City city) { city.add(this); }

}

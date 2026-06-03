package objectville;

public abstract class ServiceProvider extends Cell implements Provider<Service> {
    protected ServiceProvider(int row, int col) { super(row, col); }
    public abstract Service service();
    public abstract int radius();

    @Override public Service provides() { return service(); }

    @Override protected void indexInto(City city) { city.add(this); }
}

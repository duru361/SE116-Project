package objectville;

public class PoliceStation extends ServiceProvider {
    public PoliceStation(int row, int col) { super(row, col); }
    @Override public char symbol() { return 'F'; }
    @Override public Service service() { return Service.SECURITY; }
    @Override public int radius() { return 5; }
}

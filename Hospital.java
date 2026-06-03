package objectville;

public class Hospital extends ServiceProvider {
    public Hospital(int row, int col) { super(row, col); }
    @Override public char symbol() { return 'D'; }
    @Override public Service service() { return Service.HEALTH; }
    @Override public int radius() { return 3; }
}

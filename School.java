package objectville;

public class School extends ServiceProvider {
    public School(int row, int col) { super(row, col); }
    @Override public char symbol() { return 'S'; }
    @Override public Service service() { return Service.EDUCATION; }
    @Override public int radius() { return 4; }
}

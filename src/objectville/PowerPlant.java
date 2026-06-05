package objectville;

public class PowerPlant extends UtilityProvider {
    public PowerPlant(int row, int col) { super(row, col); }
    @Override public char symbol() { return 'P'; }
    @Override public Utility utility() { return Utility.ELECTRICITY; }
}

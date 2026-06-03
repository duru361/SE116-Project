package objectville;

public class WaterStation extends UtilityProvider {
    public WaterStation(int row, int col) { super(row, col); }
    @Override public char symbol() { return 'W'; }
    @Override public Utility utility() { return Utility.WATER; }
}

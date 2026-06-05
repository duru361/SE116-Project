package objectville;

public class InternetHub extends UtilityProvider {
    public InternetHub(int row, int col) { super(row, col); }
    @Override public char symbol() { return 'T'; }
    @Override public Utility utility() { return Utility.INTERNET; }
}

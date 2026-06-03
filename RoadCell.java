package objectville;

public class RoadCell extends Cell implements Connectable {
    public RoadCell(int row, int col) { super(row, col); }

    @Override public char symbol() { return 'R'; }
}

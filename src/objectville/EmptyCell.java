package objectville;

public class EmptyCell extends Cell {
    public EmptyCell(int row, int col) { super(row, col); }

    @Override public char symbol() { return 'E'; }
}

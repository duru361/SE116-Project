package objectville;

import java.util.List;

public abstract class Cell {

    protected final int row;
    protected final int col;

    protected Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public abstract char symbol();

    protected void indexInto(City city) { }

    int absorb(Utility u, int available, List<String> log) { return 0; }

    @Override
    public String toString() { return String.valueOf(symbol()); }
}

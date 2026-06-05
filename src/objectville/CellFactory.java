package objectville;

public final class CellFactory {

    private CellFactory() {}

    public static Cell create(char symbol, int row, int col) {
        switch (Character.toUpperCase(symbol)) {
            case 'H': return new Housing(row, col);
            case 'I': return new Industrial(row, col);
            case 'C': return new Commercial(row, col);
            case 'P': return new PowerPlant(row, col);
            case 'W': return new WaterStation(row, col);
            case 'T': return new InternetHub(row, col);
            case 'F': return new PoliceStation(row, col);
            case 'D': return new Hospital(row, col);
            case 'S': return new School(row, col);
            case 'R': return new RoadCell(row, col);
            case 'E': return new EmptyCell(row, col);
            default:
                throw new IllegalArgumentException(
                    "Unknown cell symbol '" + symbol + "' at row " + row + ", col " + col);
        }
    }
}

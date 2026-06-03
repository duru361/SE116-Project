package objectville;

import java.io.IOException;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar ObjectVilleGame.jar <mapFile> <ticks>");
            System.exit(1);
        }

        String mapFile = args[0];
        int ticks = 0;
        try {
            ticks = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Number of ticks must be an integer, got: " + args[1]);
            System.exit(1);
        }
        if (ticks < 0) {
            System.err.println("Number of ticks must be non-negative.");
            System.exit(1);
        }

        try {
            Cell[][] grid = MapLoader.load(mapFile);
            City city = new City(grid);
            PrintStream out = new PrintStream(System.out, true, "UTF-8");
            city.run(ticks, out);
        } catch (IOException e) {
            System.err.println("Could not read map file '" + mapFile + "': " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid map: " + e.getMessage());
            System.exit(1);
        }
    }
}

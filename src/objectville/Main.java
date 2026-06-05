package objectville;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String mapFile;
        int ticks;

        if (args.length >= 2) {
            mapFile = args[0];
            ticks = parseTicks(args[1]);
        } else {
            try (Scanner sc = new Scanner(System.in)) {
                mapFile = askMapFile(sc);
                ticks = askTicks(sc);
            }
        }

        runSimulation(mapFile, ticks);
    }

    private static int parseTicks(String s) {
        int n = 0;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.err.println("Number of ticks must be an integer, got: " + s);
            System.exit(1);
        }
        if (n < 0) {
            System.err.println("Number of ticks must be non-negative.");
            System.exit(1);
        }
        return n;
    }

    private static String askMapFile(Scanner sc) {
        while (true) {
            System.err.print("Map file name: ");
            if (!sc.hasNextLine()) {
                System.err.println("No input. Run with arguments: java -jar ObjectVilleGame.jar <mapFile> <ticks>");
                System.exit(1);
            }
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.err.println("Please type a file name.");
            } else if (new File(name).isFile()) {
                return name;
            } else {
                System.err.println("File not found: " + name
                        + "  (looking in: " + System.getProperty("user.dir") + ")");
            }
        }
    }

    private static int askTicks(Scanner sc) {
        while (true) {
            System.err.print("Number of ticks: ");
            if (!sc.hasNextLine()) {
                System.err.println("No input.");
                System.exit(1);
            }
            String t = sc.nextLine().trim();
            try {
                int n = Integer.parseInt(t);
                if (n >= 0) {
                    return n;
                }
                System.err.println("Ticks must be 0 or greater.");
            } catch (NumberFormatException e) {
                System.err.println("Please enter an integer.");
            }
        }
    }

    private static void runSimulation(String mapFile, int ticks) {
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
package objectville;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class City {

    private final Cell[][] grid;
    private final int rows;
    private final int cols;

    private final List<Zone> zones                       = new ArrayList<>();
    private final List<Housing> houses                   = new ArrayList<>();
    private final List<Industrial> industries            = new ArrayList<>();
    private final List<Commercial> commercials           = new ArrayList<>();
    private final List<UtilityProvider> utilityProviders = new ArrayList<>();
    private final List<ServiceProvider> serviceProviders = new ArrayList<>();

    private int prevPopulation = 0;
    private int prevGoods      = 0;
    private int prevLifestyle  = 0;

    private final List<String> log = new ArrayList<>();

    public City(Cell[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        index();
    }

    private void index() {
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.indexInto(this);
            }
        }
    }

    void add(Zone z)             { zones.add(z); }
    void add(Housing h)          { houses.add(h); }
    void add(Industrial i)       { industries.add(i); }
    void add(Commercial c)       { commercials.add(c); }
    void add(UtilityProvider p)  { utilityProviders.add(p); }
    void add(ServiceProvider sp) { serviceProviders.add(sp); }

    public void run(int ticks, PrintStream out) {
        for (int t = 1; t <= ticks; t++) {
            emit("Tick " + t);
            tick();
        }
        if (!log.isEmpty()) {
            out.print('\uFEFF');
            out.print(String.join("\r\n", log));
        }
        out.flush();
    }

    private void emit(String line) { log.add(line); }

    public void tick() {
        for (Zone z : zones) z.resetTickState();

        distributeServices();
        distributeUtilities();
        distributeResources();
        updateAndGenerate();
        accumulateProduction();
    }
}

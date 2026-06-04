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
private static String coord(Cell c) { return "(" + c.getRow() + "," + c.getCol() + ")"; }
    private static String name(Service s)  { return s.name().toLowerCase(); }

    private double distance(Cell a, Cell b) {
        int dr = a.getRow() - b.getRow();
        int dc = a.getCol() - b.getCol();
        return Math.sqrt((double) dr * dr + (double) dc * dc);
    }

    private void distributeServices() {
        for (ServiceProvider sp : serviceProviders) {
            Service s = sp.provides();
            for (Zone z : zones) {
                if (z.usesService(s) && distance(sp, z) <= sp.radius()) {
                    z.grantService(s);
                    emit(z.typeName() + " at " + coord(z) + " received " + name(s) + " service");
                }
            }
        }
    }

    private void distributeUtilities() {
        for (Zone z : zones) {
            z.prepareRemainingDemand(Utility.ELECTRICITY);
            z.prepareRemainingDemand(Utility.WATER);
            z.prepareRemainingDemand(Utility.INTERNET);
        }
        for (UtilityProvider p : utilityProviders) {
            bfsDistribute(p, p.provides());
        }
    }

    void bfsDistribute(UtilityProvider provider, Utility u) {
        int remaining = provider.capacity();
        boolean[][] visited = new boolean[rows][cols];
        Queue<Cell> queue = new ArrayDeque<>();

        enqueueNeighbors(provider, visited, queue);

        while (!queue.isEmpty() && remaining > 0) {
            Cell cell = queue.poll();
            remaining -= cell.absorb(u, remaining, log);
            enqueueNeighbors(cell, visited, queue);
        }
    }

    private void enqueueNeighbors(Cell from, boolean[][] visited, Queue<Cell> queue) {
        for (int[] d : DIRS) {
            int nr = from.getRow() + d[0];
            int nc = from.getCol() + d[1];
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
            if (visited[nr][nc]) continue;
            Cell n = grid[nr][nc];
            if (n instanceof Connectable) {
                visited[nr][nc] = true;
                queue.add(n);
            }
        }
    }

    private void distributeResources() {
        int numIC = industries.size() + commercials.size();
        int numC  = commercials.size();
        int numH  = houses.size();

        int popEach   = (numIC > 0) ? prevPopulation / numIC : 0;
        int goodsEach = (numC  > 0) ? prevGoods      / numC  : 0;
        int lifeEach  = (numH  > 0) ? prevLifestyle  / numH  : 0;

        for (Zone z : zones) {
            z.receivePooled(popEach, goodsEach, lifeEach, log);
        }
    }
}
 private void updateAndGenerate() {
        for (Zone z : zones) {
            int old = z.getLevel();
            z.update();
            int now = z.getLevel();

            emit(z.typeName() + " at " + coord(z) + " generated " + z.getOutput() + " " + z.resourceName());
            if (now != old) {
                String verb = (now > old) ? "levels up" : "levels down";
                emit(z.typeName() + " at " + coord(z) + " " + verb + " from " + old + " to " + now);
            }
        }
    }

    private void accumulateProduction() {
        prevPopulation = houses.stream().mapToInt(Zone::getOutput).sum();
        prevGoods      = industries.stream().mapToInt(Zone::getOutput).sum();
        prevLifestyle  = commercials.stream().mapToInt(Zone::getOutput).sum();
    }
}

package objectville;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class Zone extends Cell implements Connectable {

    public static final int MAX_LEVEL = 3;
    public static final int MIN_LEVEL = 0;

    protected int level = MIN_LEVEL;

    protected final Map<Utility, Integer> received   = new EnumMap<>(Utility.class);
    protected final Map<Service, Boolean> services   = new EnumMap<>(Service.class);
    protected int receivedPopulation = 0;
    protected int receivedGoods      = 0;
    protected int receivedLifestyle  = 0;

    protected int output = 0;
    protected int demand = 1;

    protected final Map<Utility, Integer> remainingDemand = new EnumMap<>(Utility.class);

    protected Zone(int row, int col) {
        super(row, col);
        resetTickState();
    }

    public abstract String typeName();

    public abstract String resourceName();

    public abstract boolean usesService(Service s);

    protected String event(String tail) {
        return typeName() + " at (" + row + "," + col + ") " + tail;
    }

    public abstract void receivePooled(int popEach, int goodsEach, int lifeEach, List<String> log);

    @Override protected void indexInto(City city) { city.add(this); }

    public final void resetTickState() {
        for (Utility u : Utility.values()) received.put(u, 0);
        for (Service s : Service.values()) services.put(s, false);
        receivedPopulation = 0;
        receivedGoods = 0;
        receivedLifestyle = 0;
    }

    public abstract boolean requires(Utility u);

    public void prepareRemainingDemand(Utility u) {
        remainingDemand.put(u, requires(u) ? demand : 0);
    }

    @Override int absorb(Utility u, int available, List<String> log) {
        int want = remainingDemand.getOrDefault(u, 0);
        int take = Math.min(want, available);
        if (take > 0) {
            received.merge(u, take, Integer::sum);
            remainingDemand.put(u, want - take);
            log.add(event("received " + take + " " + u.name().toLowerCase()));
        }
        return take;
    }

    public void grantService(Service s) { services.put(s, true); }
    public boolean hasService(Service s) { return services.getOrDefault(s, false); }

    public void setReceivedPopulation(int v) { receivedPopulation = v; }
    public void setReceivedGoods(int v)      { receivedGoods = v; }
    public void setReceivedLifestyle(int v)  { receivedLifestyle = v; }

    protected int computeM() {
        return Arrays.stream(Utility.values())
                .filter(u -> requires(u))
                .mapToInt(u -> received.get(u))
                .min()
                .orElse(0);
    }

    protected abstract int computeTargetLevel(int m);

    protected abstract int computeOutput(int level, int m);

    public void update() {
        int m = computeM();

        if (m == 0) {
            level = MIN_LEVEL;
        } else {
            int target = computeTargetLevel(m);
            if (target > level) level++;
            else if (target < level) level--;
        }

        output = computeOutput(level, m);
        demand = Math.max(output, 1);
    }

    public int getLevel()  { return level; }
    public int getOutput() { return output; }
    public int getDemand() { return demand; }
    public int getReceived(Utility u) { return received.getOrDefault(u, 0); }

}

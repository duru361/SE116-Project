package objectville;

import java.util.List;

public class Industrial extends Zone {

    public Industrial(int row, int col) { super(row, col); }

    @Override public char symbol() { return 'I'; }

    @Override public String typeName() { return "Industrial"; }
    @Override public String resourceName() { return "goods"; }
    @Override public boolean usesService(Service s) { return s == Service.SECURITY; }

    @Override protected void indexInto(City city) { super.indexInto(city); city.add(this); }

    @Override public void receivePooled(int popEach, int goodsEach, int lifeEach, List<String> log) {
        setReceivedPopulation(popEach);
        if (popEach > 0) log.add(event("received " + popEach + " population"));
    }

    @Override public boolean requires(Utility u) {
        return u == Utility.ELECTRICITY || u == Utility.WATER;
    }

    @Override protected int computeTargetLevel(int m) {
        boolean l1 = m >= 1;
        boolean l2 = l1 && hasService(Service.SECURITY);
        boolean l3 = l2 && receivedPopulation >= 1;
        if (l3) return 3;
        if (l2) return 2;
        if (l1) return 1;
        return 0;
    }

    @Override protected int computeOutput(int level, int m) {
        switch (level) {
            case 1: return m;
            case 2: return 2 * m;
            case 3: return 2 * m + receivedPopulation;
            default: return 0;
        }
    }
}


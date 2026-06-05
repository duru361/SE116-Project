package objectville;

import java.util.List;

public class Commercial extends Zone {

    public Commercial(int row, int col) { super(row, col); }

    @Override public char symbol() { return 'C'; }

    @Override public String typeName() { return "Commercial"; }
    @Override public String resourceName() { return "lifestyle"; }
    @Override public boolean usesService(Service s) { return s == Service.SECURITY; }

    @Override protected void indexInto(City city) { super.indexInto(city); city.add(this); }

    @Override public void receivePooled(int popEach, int goodsEach, int lifeEach, List<String> log) {
        setReceivedPopulation(popEach);
        if (popEach > 0) log.add(event("received " + popEach + " population"));
        setReceivedGoods(goodsEach);
        if (goodsEach > 0) log.add(event("received " + goodsEach + " goods"));
    }

    @Override public boolean requires(Utility u) {
        return true;
    }

    @Override protected int computeTargetLevel(int m) {
        boolean l1 = m >= 1;
        boolean l2 = l1 && hasService(Service.SECURITY);
        boolean l3 = l2 && receivedPopulation >= 1 && receivedGoods >= 1;
        if (l3) return 3;
        if (l2) return 2;
        if (l1) return 1;
        return 0;
    }

    @Override protected int computeOutput(int level, int m) {
        switch (level) {
            case 1: return m;
            case 2: return 2 * m;
            case 3: return 2 * m + Math.min(receivedPopulation, receivedGoods);
            default: return 0;
        }
    }
}

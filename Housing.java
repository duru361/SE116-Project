package objectville;

import java.util.List;

public class Housing extends Zone {

    public Housing(int row, int col) { super(row, col); }

    @Override public char symbol() { return 'H'; }

    @Override public String typeName() { return "House"; }
    @Override public String resourceName() { return "population"; }
    @Override public boolean usesService(Service s) { return true; }

    @Override protected void indexInto(City city) { super.indexInto(city); city.add(this); }

    @Override public void receivePooled(int popEach, int goodsEach, int lifeEach, List<String> log) {
        setReceivedLifestyle(lifeEach);
        if (lifeEach > 0) log.add(event("received " + lifeEach + " lifestyle"));
    }

    @Override public boolean requires(Utility u) {
        return true;
    }

    @Override protected int computeTargetLevel(int m) {
        boolean l1 = m >= 1;
        boolean l2 = l1
                && hasService(Service.SECURITY)
                && hasService(Service.HEALTH)
                && hasService(Service.EDUCATION);
        boolean l3 = l2 && receivedLifestyle >= 1;
        if (l3) return 3;
        if (l2) return 2;
        if (l1) return 1;
        return 0;
    }

public class UtilityProviders extends Cell {
    private int capacity;

    public UtilityProviders(int x, int y,int capacity) {
        super(x, y);
        this.capacity = capacity;
    }
    public int getCapacity() {
        return capacity;
    }
}

class PowerPlant extends UtilityProviders{
    public PowerPlant(int x,int y){
        super(x,y,100);
    }
}
class WaterPumpingStation extends UtilityProviders{
    public WaterPumpingStation(int x,int y){
        super(x,y,100);
    }
}
class InternetHub extends UtilityProviders{
    public InternetHub(int x, int y){
        super(x,y,100);
    }
}

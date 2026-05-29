public class Services extends Cell{
    private int radius;

    public Services(int x,int y, int radius){
        super(x ,y);
        this.radius=radius;
    }
    public int getRadius(){
        return radius;
    }
}
class PoliceStation extends Services{
    public PoliceStation(int x, int y){
        super(x,y,5);
    }
}
class Hospital extends Services{
    public Hospital(int x, int y){
        super(x,y,3);
    }
}
class School extends Services{
    public School(int x, int y){
        super(x, y, 4);
    }
}



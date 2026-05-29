public class Zone extends Cell{
    private int level;

    public Zone(int x, int y){
        super(x,y);
        level=0;
    }

    public int getLevel() {
        return level;
    }
    public void setLevel(int level){
        if(level >=0 && level<=3){
            this.level=level;
        }
    }
}
class Housing extends Zone{
    public Housing (int x, int y){
        super(x,y);
    }
}
class Industrial extends Zone{
    public Industrial(int x,int y){
        super(x,y);
    }
}
class Commercial extends Zone{
    public Commercial(int x,int y ){
        super(x,y);
    }
}

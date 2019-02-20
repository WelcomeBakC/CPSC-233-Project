public class StaticObject{
  private double x;
  private double y;
  private String name;

  public StaticObject(double x, double y, String name){
    setX(x);
    setY(y);
    this.name = name;


  }

  public void setX(double x){
    this.x = x;

  }

  public void setY(double y){
    this.y = y;

  }

  public double getX(){
    return x;

  }

  public double getY(){
    return y;

  }

  public String getName(){
    return name;
  }

  public String toString(){
    return name + " " + x + " " + y
  }
}

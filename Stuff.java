// Annie Liu, Esraa Kandil
// Stuff
// Overhead Class; other classes use methods from Stuff

import java.awt.*;
class Stuff implements Comparable{
    protected int x,y; // position for objects
    protected double boost;

    public void draw(Graphics g){ //for drawing all objects
    }
    public String getName(){
        return "";
    }

    // for comparing y values of objects; used for sorting the order of objects drawn
    @Override
    public int compareTo(Object ob){
        Stuff sto = (Stuff)ob;
        if(y==sto.y){
            return x-sto.x;
        }
        else{
            return y - sto.y;
        }
    }
	@Override
    public String toString(){
        return getName();
    }
}
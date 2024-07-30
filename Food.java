//Annie Liu, Esraa Kandil
//Food
// stores information for the food class of AnniemalResraaraunt, and will draw the food
 
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

class Food extends Stuff{
    int price, time;//how much the food costs, how much time it takes to cook
    private Image foodPic;//image of the food
    String name;//name of the food
    private Oven oven;//when food is cooking which oven it cooks on
    boolean cooked;//whether the food is cooked or not
    Customer customer;//which customer ordered the food
    String info, link;//the info for the constructor, and the link for the recipe


    public Food(String val){
        info = val;
        String[] info = val.split(",");
        price = Integer.parseInt(info[0]);
        time = Integer.parseInt(info[1]);
        foodPic = new ImageIcon("food/"+info[2]+".png").getImage();
        name = info[2];
        link = info[3];
    }

    public int getY(){
        return y;
    }

    //draws the food
    public void draw(Graphics g, int xx, int yy){
        g.drawImage(foodPic, xx+GamePanel.offset, yy, null);
    }

    public String getName(){
    	return name;
    }
    public void setOven(Oven o){
        oven = o;
    }
    public int getTime(){
        return time;
    }
    public void setCooked(){
        cooked = true;
    }
    public boolean isCooked(){
        return cooked;
    }
    public void setCustomer(Customer c){
        customer = c;
    }
    public Customer getCustomer(){
        return customer;
    }
    //returns the info needed for the food constructor
    public String clone(){
        return info;
    }
    public int getPrice(){
        return price;
    }
    public String getLink(){
        return link;
    }
}
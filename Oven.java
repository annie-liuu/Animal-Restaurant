//Annie Liu, Esraa Kandil
//Oven
//Sotres info for oven and draws it

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

class Oven extends Stuff{
    public Food curFood;//current food on the oven
	private int curFoodTimerH, curFoodTimerC, h;//food timers for cooking, one that changes and one for reference, and h is the height of the timer
    public boolean occupied;//whether the oven is occupied by a food
    public int design, price;//design of the oven and price
    private Image ovenPic;//oven image
    private String info, ovenNum;//info is string that is used in constructor, name is the name of the oven

    private ArrayList<Integer> designsOwned = new ArrayList<Integer>();//list of designs of oven owned by user

    public Oven(String val, int d){
        info = val;
        String[] info = val.split(",");
        x = Integer.parseInt(info[0]);
        y = Integer.parseInt(info[1]);
        ovenNum = info[2];
        design = d;
        ovenPic = new ImageIcon("ovens/oven"+ d +".png").getImage();
        occupied = false;//nothing occupies it when it is first made
        if(ovenNum.equals("oven1")){// the price for the starting table is 1000, the rest is 5000
            price = 1000;
        }
        else{
            price = 5000;
        }
        boost = getBoost();
    }

    //draws the oven and checks when the food is done cooking
    public void draw(Graphics g){
        g.drawImage(ovenPic,x+GamePanel.offset,y,null);
        if(occupied){//if it is cooking food
            curFoodTimerC -= 100;
            h = (curFoodTimerC * 100) / curFoodTimerH;//height of the timer (between 0 - 100);

            if (h<=0){//if timer is up
                curFood.setCooked();
                curFood.getCustomer().setState();//customer is now eating
                occupied = false;//oven no longer occupied
            }
            else{
                g.setColor(Color.GREEN);
                g.fillRect(x - 10 + GamePanel.offset, y - 10 + (100 - h), 10, h);//draws the timer
                curFood.draw(g, x + 37,y + 6);//draws the food
            }
        }
    }
    public int getDesign(){
        return design;
    }
    public int getY(){
        return y;
    }
    public int getX(){
        return x;
    }
 	public double getBoost(){//depending on the design of the oven different boosts to shorten timer
 		if (design == 1) boost = 1.0;
 		else if (design == 2) boost = 0.75;
 		else if (design == 3) boost = 0.5;
 		return boost;
 	}
    public boolean getState(){
        return occupied;
    }
    public String getName(){//returns the ovens design
    	return "oven" + design;
    }
    public void setFood(Food f){//sets ovens food and the timers
        curFood = f;
        curFoodTimerH = (int)(Math.round(f.getTime() * boost));
        curFoodTimerC = curFoodTimerH;
    }
    public Food getFood(){
        return curFood;
    }
    public void setOccupied(){
        if(!occupied) occupied = true;
        else occupied = false;
    }
    public void setDesign(int d){//when user changes its design
        design = d;
        getBoost();//gets the new boost for the design
        ovenPic = new ImageIcon("ovens/oven"+ d +".png").getImage();//changes the pic
    }
    public String clone(){//info for the constructor for making a new oven
        return info;
    }
    public String getOvenNum(){//returns the name of the oven
        return ovenNum;
    }
    public int getPrice(int d){
        return price * d;//original price multiplied by design number
    }
    public void addDesign(int i){
        designsOwned.add(i);//adds design to the owned designed
    }
    public ArrayList<Integer> getDesignsOwned(){
        return designsOwned;
    }
}
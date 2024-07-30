// Annie Liu, Esraa Kandil
// Customer
// Class Customer creates as customer, stores all information

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

class Customer extends Stuff{
    String name, info; //name, info for cloning
    ArrayList<Integer> foodsIndex = new ArrayList<Integer>(); // arraylist of indexes of foods they order
    private Image pic1L, pic1R, pic2L, pic2R, printPic, speechBubble; // frame 1 facing left, right, frame 2 facing left, right, picture currently printing, speech bubble
    Food curFood; // food they order
    int tip, ratingRequired,curPos,curDest, tableIndex, destX, destY, switchImageTimer, step, tempTime; // amount they tip, rating required for appearance, current position (index), destination(index), table position (index),destX, destY, timer for switching images, current stage, timer for pausing
    String DIR; // direction they are currently facing
    boolean tipped = false; // if customer has tipped
    boolean visit = false;
    Table table; // table binded to customer

    public ArrayList<Integer> curPath = new ArrayList<Integer>(); // current path from position to destination
    // Legend for steps:
    // moving = 0;
    // sitting = 1;
    // ordering = 2;
    // waiting = 3;
    // eating = 4;
    // leaving = 5
    // gone = 6;
    
    public Customer(String info){
        this.info = info;
        String [] stats = info.split(",");

        pic1L = new ImageIcon("customers/"+stats[0]+"1L.png").getImage();
        pic1R = new ImageIcon("customers/"+stats[0]+"1R.png").getImage();
        pic2L = new ImageIcon("customers/"+stats[0]+"2L.png").getImage();
        pic2R = new ImageIcon("customers/"+stats[0]+"2R.png").getImage();
        speechBubble = new ImageIcon("other/speechBubble.png").getImage();

        for (int i=1; i<4; i++){ // adds all foods to arraylist
            foodsIndex.add(Integer.parseInt(stats[i]));
        }
		// this is their starting position
        x = 335;
        y = 199;

        tip = Integer.parseInt(stats[4]);
        ratingRequired = Integer.parseInt(stats[5]);
        printPic = pic1R;
        curPos = 0;//index in verticies
        curDest = -1;
        name = stats[0];
        switchImageTimer = 0;
    	DIR = "R"; //begin facing right
    }
	
    public int getRating(){
        return ratingRequired;
    }
    public int getCurPos(){
        return curPos;
    }
    public int getDestination(){
        return curDest;
    }
    public void reOrder(){
        step = 2;
    }
    // switching image from one frame to another depending on direction
    public void switchImage(){
        if(printPic.equals(pic1L)){
            printPic = pic2L;
        }
        else if(printPic.equals(pic2L)){
            printPic = pic1L;
        }
        else if(printPic.equals(pic1R)){
        	printPic = pic2R;
        }
        else if(printPic.equals(pic2R)){
        	printPic = pic1R;
        }
    }
    // switches picture depending on direction
    public void switchDirection(String d){
    	if (DIR == d){
    		printPic = printPic;
    	}
    	else if (d == "L"){
    		DIR = d;
    		printPic = pic1L;
    	}
    	else if (d == "R"){
    		DIR = d;
    		printPic = pic1R;
    	}
    }
    public int getY(){
        return y;
    }
	public String getName(){
		return name;
	}
    public void draw(Graphics g){
        g.drawImage(printPic, x+GamePanel.offset, y,null);
        if(step == 2){ // if they are ordering, there is a speech bubble around them with the food they want to order
            g.drawImage(speechBubble, table.x + 91 + GamePanel.offset, table.y - 122,null);
            curFood.draw(g, table.x + 102, table.y - 117);
        }
    }
    public void setTable(Table tt, int t){
        tableIndex = t;
        table = tt;
    }
    public Table getTable(){
        return table;
    }
    public int getSwitchImageTimer(){
        return switchImageTimer;
    }
    public int getStep(){
        return step;
    }
    public void setState(){
        step += 1;
    }
    public int getTip(){
        return tip;
    }
    //sets path from one index to another, sets destination and x y values
    public void setCurPath(ArrayList<Integer> p){
        curPath = new ArrayList<Integer>(p);
        curDest = curPath.get(0);
        destX = (int)path.verticies[curDest].getX();
        destY = (int)path.verticies[curDest].getY();
    }
    //adds an index to the path
    public void addPath(ArrayList<Integer> a){
        curPath.addAll(a);
    }
    public void move(){
        if (!visit &&curPos>11 && curPos<15){
            visit = true;
            printPic = pic1R;
            tempTime = 167;
        }
        if (!tipped && curPos == 15){ //if they have not tipped yet, and they are in position, a timer starts, causing them to pause there and whatever money is owed is depositied into the tip jar
        	printPic = pic1R;
            tipped = true;
            tempTime = 167;
            pay();
        }
        if(tempTime > 0){ // timer going down for tipping
            tempTime -= 1;
        }
        switchImageTimer += 1; //timer for switching between frames
        if(x != destX && tempTime <= 0){
            if(destX < x){
            	switchDirection("L");
                x -= 1;
            }
            else{
            	switchDirection("R");
                x += 1;
            }
        }
        if(y != destY && tempTime <= 0){
            if(destY < y){
                y -= 1;
            }
            else{
                y += 1;
            }
        }
        if(x==destX && y==destY){ // if they have arrived at their temporary node,
            if(curPath.size() > 1){ // if there are still nodes left over in the path
                curPath.remove(0); // removes the current destination from path
                curPos = curDest; //position becomes destination
                curDest = curPath.get(0); //destination becomes next node
                destX = (int)path.verticies[curDest].getX();
                destY = (int)path.verticies[curDest].getY();
            }
            else{ //if they arrived at the last position in the destination
                if(step < 2) step = 1; // if they have not ordered yet, they sit and order
                if(curDest != -1) curPos = curDest; // if they are not standing in line
                curDest = -1; // they stop moving, and destination now does not exist
                printPic = pic1R; //must face right
                if (curPos==tableIndex && curFood == null) orderFood(); //if they have arrived at a table and have not begun eating, they order
            }
        }
    }
	//for creating a copy of the customer
    public String clone(){
        return info;
    }

    public void orderFood(){
        step = 2; // sets status to ordering
        Random rand = new Random();
        curFood = new Food(GamePanel.allFoods.get(foodsIndex.get(rand.nextInt(foodsIndex.size()))).clone()); // creates a copy of a random food selected from their preferences
        curFood.setCustomer(this); //binds that food to this customer, so to not mix up food when returning from the kitchen
    }
    public Food getFood(){
        return curFood;
    }
    //adds whatever money they owe to the tip jar
    public void pay(){
        GamePanel.tips.deposit((int)(curFood.getPrice() * table.getBoost()) + tip);
        return;
    }
}
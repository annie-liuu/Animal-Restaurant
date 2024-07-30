// Annie Liu, Esraa Kandil
// Table
// Class Table creates object Table...

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

class Table extends Stuff{
	private int indexVertices, xx, yy, price; //node for table, table cost
    public boolean occupied; // whether the table is occupied by a customer
    public int design, foodTimer; // the design of the table, the timer for eating
    private double boost; // how much the design of the table boosts tips
    private Image tablePic; 
    public Customer customer; //customer binded to the table
    private String info, tableNum; // string for cloning, name

    private ArrayList<Integer> designsOwned = new ArrayList<Integer>();
    

    public Table(String val, int d){
        info = val;
        String[] info = val.split(",");
        x = Integer.parseInt(info[0]);
        y = Integer.parseInt(info[1]);
        tableNum = info[3];
        // xx = x;
        // yy = y;
        indexVertices = Integer.parseInt(info[2]);
        if(tableNum.equals("table1")){ // the price for the starting table is 1000, the rest is 5000
            price = 1000;
        }
        else{
            price = 5000;
        }
        design = d;
        occupied = false; // table is not yet occupied
        tablePic = new ImageIcon("tables/table"+ d +".png").getImage();
        foodTimer = 100; //eating timer is 100
    }

    public void draw(Graphics g){
        g.drawImage(tablePic,x+GamePanel.offset,y,null);
        if(customer != null && customer.getFood() != null && customer.getFood().isCooked() && customer.getStep() == 4){ // if the table has a customer, the customer has a food, the food is cooked, and the customer is eating, 
            customer.getFood().draw(g, x + 37, y + 6); //draws the food on the table
            foodTimer -= 1; // begins the timer for eating
            if(foodTimer <= 0){ // once the timer has reached zero,
                customer.setState(); // customer goes into the next state
                occupied = false; // the table is no longer occupied and can take the next customer
                foodTimer = 100; //timer resets
            }
        }
    }
    public int getDesign(){
        return design;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    // this is how much the design of the table boosts the income
     public double getBoost(){
        if (design == 1) boost = 1;
        else if (design == 2) boost = 1.25;
        else if (design == 3) boost = 1.5;
        return boost;
    }
    public boolean getState(){
        return occupied;
    }
    public String getName(){
    	return "table"+design;
    }
    public int getIndex(){
        return indexVertices;
    }
    public void setCustomer(Customer c){
        customer = c;
    }
    public void setOccupied(){
        if(!occupied) occupied = true;
        else occupied = false;
    }
    public Customer getCustomer(){
        return customer;
    }
    // for purchasing new tables and changing table designs
    public void setDesign(int d){
        design = d;
        tablePic = new ImageIcon("tables/table"+ d +".png").getImage();
    }
    public String clone(){
        return info;
    }
    public String getTableNum(){
        return tableNum;
    }
    public int getPrice(int d){
        return price * d;
    }
    public void addDesign(int i){
        designsOwned.add(i);
    }
    // all tables that are owned and have been purchased
    public ArrayList<Integer> getDesignsOwned(){
        return designsOwned;
    }
}
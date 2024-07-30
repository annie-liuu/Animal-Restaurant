/*
* Annie Liu, Esraa Kandil
* GamePanel
*
*/

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.io.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener, MouseWheelListener{
    public static ArrayList<Stuff>drawOrder = new ArrayList<Stuff>(); // arraylist for drawing objects in perspective
    public static ArrayList<Stuff>allObjects = new ArrayList<Stuff>(); // all objects
    public static ArrayList<Table> allTables = new ArrayList<Table>(); // all tables
    public static ArrayList<Table> availTables = new ArrayList<Table>(); // owned tables
    public static ArrayList<Oven> allOvens = new ArrayList<Oven>(); // all ovens
    public static ArrayList<Oven> availOvens = new ArrayList<Oven>(); // owned ovens
    public static ArrayList<Facility> allFacilities = new ArrayList<Facility>(); // all facilities
    public static ArrayList<Facility> availFacilities = new ArrayList<Facility>();// owned facilities
    public static ArrayList<Food> allFoods = new ArrayList<Food>(); // all foods
    public static ArrayList<Customer> allCustomers = new ArrayList<Customer>(); // all customers
    public static ArrayList<Customer> availCustomers = new ArrayList<Customer>(); // unlocked customers (dependent on rating)
    public static ArrayList<Customer> curCustomers = new ArrayList<Customer>(); // customers currently in restaurant
    public static ArrayList<Customer> lineCustomers = new ArrayList<Customer>(); // customers waiting to enter the restaurant

    public static ArrayList<rect> objectRects = new ArrayList<rect>(); // rectangles for icons of facilities, ovens, and tables in menu page
    public static ArrayList<rect> foodRects = new ArrayList<rect>(); // rectangles for icons of food in menu page

    public static ArrayList<Image> htpPics = new ArrayList<Image>(); // slides for how to play

    Timer myTimer;
    boolean [] keys;
    int coins, rating; // currency (cod/fish), star rating
    int lw = 0; // loading percentage
    int musicTimer, customerTimer, loadingPicTimer; // timer for background music, timer for customer entrance frequency
    int iNum = 1;;//image number for loading screen animation
    static int offset, px, dir, menuY = 178; // variables for scroll
    boolean moveScreen, displaySettings, displayMenu, displayHTP, displayPay; // if screen is currently moving, settings are displayed, menu is displayed, how-to-play is displayed, payment page is displayed
    boolean music, soundEffects;
    String menuScreen, gameScreen, cName, cNum, cExp, cSec; // pages for menu; screens for game (loading/dining/kitchen); payment name, number, expiry date, security code
    Image gamePic, settingsPic, settingsIcon, menuRecipes, menuFacilities, codRating, loadPic, facilitiesPage, recipesPage, paymentPage, htp; // background photos
    public static Tips tips; // creates static object Tips
    SoundEffect soundtrack, tipSound, clickSound, uhOhSound, sparkle; // maintrack, other sound effects

    Rectangle menuClip = new Rectangle(99, 178, 408, 663); // clip for menu scroll

    public static path PATH = new path(); // object used for pathfinder

    // GamePanel constructor
    public GamePanel(){
        setPreferredSize(new Dimension(608,1080));
        keys = new boolean[2000];
        setFocusable(true);
        requestFocus();
        addMouseListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        myTimer = new Timer(20, this);

        gameScreen = "loading"; // game starts with loading screen
        //Strings for payment
        cName = "";
        cNum = "";
        cExp = "";
        cSec = "";

        readFile();

        myTimer.start();

        gamePic = new ImageIcon("other/gameScreen.png").getImage();
        settingsPic = new ImageIcon("other/settingsPic.png").getImage();
        settingsIcon = new ImageIcon("other/settingsIcon.png").getImage();
        menuRecipes = new ImageIcon("other/menuRecipes.png").getImage();
        menuFacilities = new ImageIcon("other/menuFacilities.png").getImage();
        codRating = new ImageIcon("other/codRating.png").getImage();
        loadPic = new ImageIcon("other/load.png").getImage();
        facilitiesPage = new ImageIcon("other/facilitiesPage.png").getImage();
        recipesPage = new ImageIcon("other/recipesPage.png").getImage();
        htp = new ImageIcon("other/htp.png").getImage();
        paymentPage = new ImageIcon("other/paymentPage.png").getImage();
        

        loadingPicTimer = 0;

        px = 304;
        offset = px - 304;

        // SoundEffects used
        soundtrack = new SoundEffect("mainSoundtrack.wav");
        clickSound = new SoundEffect("clickSound.wav");
        tipSound = new SoundEffect("tipSound.wav");
        uhOhSound = new SoundEffect("uhOhSound.wav");
        sparkle = new SoundEffect("sparkle.wav");
        soundtrack.play();
        music = true;
        soundEffects = true;

        musicTimer = 1720; // length of wav file in frames
        customerTimer = 0;

        int count = 0;

        //object names for objectRects
        String [] names= {"table1", "table2", "table3", "table4", "table5", "table6", "oven1", "oven2", "oven3", "oven4", "oven5", "oven6", "keg", "coffee", "dessert", "tips", "plant", "furnace", "goods", "window", "rack", "shelf", "cabinet", "counter", "trash", "fridge"};
        String [] foodNames = {"f1","f2","f3","f4","f5","f6","f7","f8","f9","f10","f11","f12","f13","f14","f15","f16","f17","f18","f19","f20","f21","f22","f23","f24","f25","f26","f27","f28","f29","f30"};
        //creating rectangles for facilites/recipes page
        for(int i = 45; i < 3321; i += 131){ //y values of rectangles have intervals of 131
            for(int j = 0; j < 298; j += 148){ //x values of rectangles have intervals of 148
                objectRects.add(new rect(99 + j, i, names[count], j/148 + 1));
            }
            count ++;
        }
        count = 0;
        for(int i=25; i<691; i+=95){ //y values of rectangles have intervals of 95
            for (int j = 25; j<311; j+=95){ //x values of rectangles have intervals of 95
                if (count==28){ //item #28 is in an off position, requires a different x value
                    foodRects.add(new rect(99+122, i, foodNames[count], 5));
                }
                else if (count==29){ //item #29 is in an off position, requires a different x value
                    foodRects.add(new rect(99+217, i, foodNames[count], 5));
                    break;
                }
                else{
                    foodRects.add(new rect(99+j, i, foodNames[count], 5));
                }
                count ++;
            }
        }
        for (int i = 1; i<8; i ++){ // creates and adds all the how to play pictures into the array list
            htpPics.add(new ImageIcon("other/htp"+i+".png").getImage());
            htp = htpPics.get(0);
        }
    }

    // method for creating objects through reading textfiles
    public void readFile(){
        try{
            Scanner playerInfile = 	new Scanner(new File("playerStats.txt")); //txt contains: player's current statistics - includes coins, rating, current settings (selected facilities, ovens, tables)
            Scanner tablesInfile = 	new Scanner(new File("tables.txt")); //txt contains: table's x, y, vertice value, name
            Scanner ovensInfile = 	new Scanner(new File("ovens.txt")); //txt contains: oven's x, y, vertice value, name
            Scanner facilitiesInfile = new Scanner(new File("facilities.txt")); //txt contains: all facilities types, differing x, y, cost, star rating added 
            Scanner foodInfile = new Scanner(new File("food.txt")); //txt contains: rating required, price, image name, link
            Scanner customerInfile = new Scanner(new File("customers.txt")); //txt contains: image name, foods preferred (3 of them), tip amount, rating required
            Scanner ownershipInfile = new Scanner(new File("facilitiesOwned.txt")); // txt contains: boolean value for whether or not the facility has been purchased

            coins = Integer.parseInt(playerInfile.nextLine()); // first line of playerStats.txt
            rating = Integer.parseInt(playerInfile.nextLine());// second line of playerStats.txt
            tips = new Tips(Integer.parseInt(playerInfile.nextLine())); // third line of playerStats.txt
            for (int i = 0; i<6; i++){ //creates six tables using information from the file
                Table t = new Table(tablesInfile.nextLine(),Integer.parseInt(playerInfile.nextLine())); // creates tables according to (x,y) values in file and player's current furniture layout
                allTables.add(t);
                if(t.getDesign() != 0){ // if the design is not 0 (0 meaning unowned), the table is owned and therefore added to availTables
                    availTables.add(t);
                }
                for(int k = 0; k < t.getDesign() + 1; k++){ //adds all previous versions of current table to owned
                    t.addDesign(k);
                }
                allObjects.add(t); // all tables then added to allObjects
            }
            for (int i = 0; i<6; i++){ // creates six ovens
            	Oven o = new Oven(ovensInfile.nextLine(),Integer.parseInt(playerInfile.nextLine()));
            	allOvens.add(o);
                if(o.getDesign() != 0){ //adds owned ovens to availOvens
                    availOvens.add(o);
                }
                for(int k = 0; k < o.getDesign() + 1; k++){ //adds previous versions of the oven to an arraylist of possible designs
                    o.addDesign(k);
                }
                allObjects.add(o);
            }
            for (int i = 0; i<56; i++){ //creates all 56 facilities
            	Facility f = new Facility(facilitiesInfile.nextLine());
                f.setStatus(Boolean.parseBoolean(ownershipInfile.nextLine())); // sets the status of the facility to whether it is owned
                if(f.getName().equals("tips1")){ // if the name of the facility is tips, it is owned (tips 1 is automatically given to the player)
                    f.setStatus(true);
                }
        		allFacilities.add(f);
            }
            for (int i = 0; i<14; i++){
            	String play = playerInfile.nextLine(); //this is the name of the facility that is currently set in the game
	            for (Facility f: allFacilities){ //checks all facilities
	            	if (play.equals(f.getName())){ // if the facility name matches the line in playerStats, that facility is added to own facilities
	            		availFacilities.add(f);
	            		allObjects.add(f); //adds the facility to all objects
	            	}
	            }
        	}
            for (int i = 0; i<30; i++){ //creates all 30 foods
                Food f = new Food(foodInfile.nextLine());
                allFoods.add(f);
            }
            for (int i = 0; i<20; i++){ //creates all 20 customers
                Customer c = new Customer(customerInfile.nextLine());
                allCustomers.add(c);
                if (c.getRating()<=rating) availCustomers.add(c); //if the rating required for the customer is below the player's current rating, they are an available customer
            }
        }
        catch(FileNotFoundException ex){
            System.out.println(ex);
        }
    }

    // method called when the player resets the game; all arraylists are reset and readFile() is run again
    public void clearArrayLists(){
        allTables.clear();
        allOvens.clear();
        availFacilities.clear();
        allObjects.clear();
        allFacilities.clear();
        allFoods.clear();
        allCustomers.clear();
        availCustomers.clear();
        availTables.clear();
        availOvens.clear();
        curCustomers.clear();
        lineCustomers.clear();
    }

    public void sortOrder(){
		drawOrder = new ArrayList<Stuff>(allObjects); // creates a copy of allObjects
		Collections.sort(drawOrder); //sorts all objects in order of y value
	}

    public void music(){
        musicTimer -= 1;// timer goes down as game progresses
        if(musicTimer == 0){ //once it hits zero, the soundtrack plays again and the timer is reset
            if(music) soundtrack.play();
            musicTimer = 1720;
        }
    }

    public void settings(String let){
        if(let.equals("restart")){ // if "restart" is clicked
            try{
                // rewrites all progress as 0 and resets facilities, tables, amd ovens
            	Scanner facFile = new Scanner(new File("facilities.txt"));
                PrintWriter out = new PrintWriter(new File("playerStats.txt"));

                out.println(5000);
                for(int i = 0; i < 14; i ++){
                    out.println(0);
                }
                for (int i = 0; i<14; i++){
                    String [] fac = facFile.nextLine().split(",");
                	out.println(fac[0]);
                	facFile.nextLine();
                	facFile.nextLine();
                	facFile.nextLine();
                }
                out.close();

                PrintWriter owned = new PrintWriter(new File("facilitiesOwned.txt"));

                for(int i = 0; i < 56; i++){
                    owned.println("false");
                }
                owned.close();
            }
            catch(FileNotFoundException ex){
                System.out.println(ex);
            }
            clearArrayLists(); // empties arraylists
            readFile(); //reads the newly written files
            coins = 5000; //coins at the beginning must equal 2000 
            displaySettings = false; // closes settings
            displayHTP = true;
        }
        else if(let.equals("save")){ // if "save" is clicked
            try{ //rewrites all files as curent progress
                PrintWriter out = new PrintWriter(new File("playerStats.txt"));
                out.println(coins);
                out.println(rating);
                out.println(tips.getTotal());
                for (Table t: allTables){
                    out.println(t.getDesign());
                }
                for (Oven o: allOvens){
                    out.println(o.getDesign());
                }
                for (Facility f: availFacilities){
                    out.println(f.getName());
                }
                out.close();

                PrintWriter owned = new PrintWriter(new File("facilitiesOwned.txt"));
                for(Facility f : allFacilities){
                    owned.println(f.getStatus());
                }
                owned.close();
            }
            catch(FileNotFoundException ex){
                System.out.println(ex);
            }
            
        }
        else if(let.equals("htp")){//if "how to play" is clicked
            displayHTP = true;
        }
    }
    
    //menu page and tabs; called when user wishes to switch/purchase facilities
    public void Menu(String name){
        if(name.contains("table")){ // if it is a table
            int version = Integer.parseInt(name.substring(6));
            String imageName = name.substring(0,name.length() - 1);
            boolean exists = false; // checking if table is purchased yet or not
            for(Table t : allTables){
                if(t.getTableNum().contains(imageName)){ // if we've reached the table position we want,
                    if(coins - t.getPrice(version) >= 0 && t.getDesignsOwned().contains(version - 1) || t.getDesignsOwned().contains(version)){ // if there are enough coins to purchase and the table has not yet been owned
                        for (Table ta: availTables){ // checks in avail tables for if the table exists
                            if(ta.getTableNum().contains(imageName)){
                                ta.setDesign(version);
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){ // if it does not exist, changes that table to the new design
                            for (Table ta: allTables){
                                if (ta.getTableNum().contains(imageName)){
                                    ta.setDesign(version);
                                    availTables.add(ta);
                                    allObjects.add(ta);
                                    break;
                                }
                            }
                        }
                        if(!t.getDesignsOwned().contains(version)) { // if all else, the table is purchased and that design now becomes owned
                            coins -= t.getPrice(version);
                            t.addDesign(version);
                        }
                    }
                    else{ // if the player does not have enough coins
                        if (soundEffects) uhOhSound.play();
                    }
                    break;
                }
            }
        }
        // same as table
        else if(name.contains("oven")){
            int version = Integer.parseInt(name.substring(5));
            String imageName = name.substring(0,name.length() - 1);
            boolean exists = false;
            for(Oven o : allOvens){
                if(o.getOvenNum().contains(imageName)){
                    if(coins - o.getPrice(version) >= 0 && o.getDesignsOwned().contains(version - 1) || o.getDesignsOwned().contains(version)){
                        for (Oven ov: availOvens){
                            if(ov.getOvenNum().contains(imageName)){
                                ov.setDesign(version);
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){
                            for (Oven ov: allOvens){
                                if (ov.getOvenNum().contains(imageName)){
                                    ov.setDesign(version);
                                    availOvens.add(ov);
                                    allObjects.add(ov);
                                    break;
                                }
                            }
                        }
                        if(!o.getDesignsOwned().contains(version)) {
                            coins -= o.getPrice(version);
                            o.addDesign(version);
                        }
                    }
                    else{
                        if(soundEffects) uhOhSound.play();
                    }
                    break;
                }
            }
        }
        // same for facilities
        else{
            String imageName = name.substring(0,name.length() - 1);
            for(Facility f : allFacilities){
                if(f.getName().contains(name)){
                    if(coins - f.getPrice() >= 0 || f.getStatus()){
                        for (Facility g: new ArrayList<Facility>(availFacilities)){
                            if(g.getName().contains(imageName)){
                                availFacilities.remove(g);
                                allObjects.remove(g);
                            }
                        }
                        availFacilities.add(f);
                        allObjects.add(f);
                        if(! f.getStatus()){
                            coins -= f.getPrice();
                            rating += f.getBoost();
                        } 
                        f.setStatus(true);
                    }
                    else{
                        if(soundEffects) uhOhSound.play();
                    }
                    break;
                }
            }
        }
    }

    // repeatedly checks if the current rating is above a customers required rating
    public void checkRating(){
        for (Customer c : allCustomers){
            if (c.getRating()<= rating && !availCustomers.contains(c)){ // if it is and they are not already in availCustomers. they are added
                availCustomers.add(c);
            }
        }
    }
    // returns the first unoccupied table
    public Table openTable(){
        for(Table t : availTables){
            if(t.getState() == false){
                return t;
            }
        }
        return null; // if there are no open tables, does not return anything
    }
    
    // checks for keys pressed
    public void openButtons(){
        if(keys[77]){ // if "M" is pressed, page becomes the menu
            displayMenu = true;
            menuScreen = "facilities";
        }
        if(keys[83]){ // if "S" is pressed, settings are displayed
            displaySettings = true;
        }
        if(keys[27]){ // if esc is pressed, it closes whatever is currently open
            if(displayHTP) displayHTP = false;
            else if(displaySettings) displaySettings = false;
            else if(displayMenu) displayMenu = false;
            else if(displayPay) displayPay = false;
            
        }
        if(displayMenu && keys[KeyEvent.VK_UP] && menuY < 178){ //if the menu is currently open and up key is pressed, menu scrolls up
            menuY += 30;
        }
        int y;
        if(displayMenu && menuScreen.equals("facilities")) y = -2565; // the y value for the maximum scrolling down is -2565 in facilities, and -56 in recipes
        else y = -56;
        if(displayMenu && keys[KeyEvent.VK_DOWN] && menuY > y){ //if the menu is currently open and down key is pressed, menu scrolls down
            menuY -= 30;
        }
    }

	//creates a random customer
    public void getRandCustomer(){
        Random rand = new Random();
        int r = rand.nextInt(availCustomers.size());// index of a random customer
        Customer c = new Customer(availCustomers.get(r).clone()); //clones the random customer
        Table t = openTable(); // calls next open table
        if(t != null){ // if there is an available table
            int openTable = t.getIndex(); // this is the vertice value of the open table
            if (lineCustomers.size() > 0){ // if there are customers in line
                lineCustomers.add(c); // the newly created customer goes into line
                allObjects.remove(c);
                c = lineCustomers.get(0); // instead, the customer that is first in line becomes the selected customer
                allObjects.add(c);
                c.setTable(t, openTable); // they are binded to that open table
                c.setCurPath(PATH.findPath(c.getCurPos(), openTable)); // a path is set from their current position to the value of the open table
                t.setOccupied(); // that table's status becomes occupied
                t.setCustomer(c); // that table is binded to the customer (they are now binded to each other and can be accessed from each other)
                lineCustomers.remove(0); // that customer is removed from the line
                curCustomers.add(c); // they are added to the list of customers currently in the building
            }
            else{ // if there are no customers in line, the same occurs but there are no removing/updating from the customers in line
                c.setTable(t,openTable);
                c.setCurPath(PATH.findPath(c.getCurPos(), openTable));
                t.setOccupied();
                t.setCustomer(c);
                curCustomers.add(c);
                allObjects.add(c);
            }
        }
        else{ // if there are no available ovens, 
            lineCustomers.add(c); // the customers waits in line
            allObjects.add(c); // they are added to all objects
        }
    }

    // checks for whether the information inputted from the payment screen is valid; similar to a previous assignment done in class
    public boolean cardValidity(String name, String num, String sec, String exp){
		int year, month;
    	for(int i =0; i <name.length(); i++){ // all characters in the name must be a letter or a white space
    		if(!Character.isLetter(name.charAt(i)) && !Character.isWhitespace(name.charAt(i)))return false;
    	}
        for (int i=0; i<16;i++){ // all characters in the card number must be a digit
    		if(!Character.isDigit(num.charAt(i)))return false;
    	}
    	if (sec.length()!=3 && sec.length()!=4) return false; // security code must be 3 or 4 digits
        System.out.println(3);
    	try{ //attempts to convert securtiy code to integer
    		int n = Integer.parseInt(sec);
    	}
        catch(NumberFormatException e){ 
    		return false;
    	}
    	if (exp.length()!=5) return false; //expiration date must be 5 characters
    	if (!(exp.charAt(2)+"").equals("/"))return false;
    	try{ //attempts to convert month to integer
    		month = Integer.parseInt(exp.substring(0,2));
    	}
        catch(NumberFormatException e){
    		return false;
    	}
    	try{ //attempts to convert year to integer
    		year = Integer.parseInt(exp.substring(3));
    	}catch(NumberFormatException e){
    		return false;
    	}
    	if(year<22) return false; // year must be valid
    	else if (year==22 && month<6) return false; // date must not have already passed
    	return true;
	}

    // loads a customer every ~15 seconds
    public void loadCustomer(){
        customerTimer -= 1;
        for (Table t: availTables){
            if(lineCustomers.size() > 0 && !t.getState() || customerTimer <= 0 && lineCustomers.size()<10){ // if there are customers in line and there are empty tables, or if the customer has reached 0 and there are less than 10 customers in line, a new on is created
                getRandCustomer();
                if(customerTimer <= 0) customerTimer = 487;
            }
        }
    
    }

    // takes in a customer and cooks their food
    public void cookFood(Customer c){
        c.setState();
        Food f = c.getFood();
        for(Oven o : availOvens){
            if(!o.getState()){
                f.setOven(o);
                o.setFood(f);
                o.setOccupied();
                break;
            }
            else if (availOvens.indexOf(o)==availOvens.size()-1){
                f.getCustomer().reOrder(); // if there is no available oven, the customer reorders the food until there is one.
                if(soundEffects) uhOhSound.play();
            }
        }
        if(availOvens.size() == 0){
            f.getCustomer().reOrder();
            if(soundEffects) uhOhSound.play();
        }
    }

    //takes in a link and opens it in chrome
    public void openLink(String link){
        try {
		    Process p = Runtime.getRuntime().exec(new String[] {"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe", link});
		    p.waitFor();
		} catch (Exception e) {
		    e.printStackTrace();
		}
    }

    //takes in a customer after they finish eating and gives them a path to take to leave
    public void customerLeave(Customer c){
        ArrayList<Integer> posFac = new ArrayList<Integer>();//possible facilities they could visit
        boolean k = false;//has facility been added to posFac - so they dont repeat
        boolean x = false;
        boolean d = false;
        Random rand = new Random();
        c.setState();
        for (Facility f: allFacilities){//checks and adds possible destinations for the customer
            if(f.getName().contains("keg") && f.getStatus() && !k){
                k = true;
                posFac.add(13);
            }
            if (f.getName().contains("coffee") && f.getStatus() && !x){
                x = true;
                posFac.add(12);
            }
            if (f.getName().contains("dessert") && f.getStatus() && !d){
                d = true;
                posFac.add(14);
            }
        }
        Collections.sort(posFac);
        if (posFac.size()!=0){
            int f = rand.nextInt(posFac.size());
            c.setCurPath(PATH.findPath(c.getCurPos(), posFac.get(f)));//sets path of customer to visit a facility
            c.addPath(PATH.findPath(posFac.get(f), 15));//after facility they will go to tip jar
        } 
        else{
            c.setCurPath(PATH.findPath(c.getCurPos(), 15));//if they dont have facilities unlocked they will just go to tip jar
        }
        c.addPath(PATH.findPath(15,0));//adds path to door
    }

    //moves screen left and right and changes offset of objects on screen
    public void moveScreen(){
        if(keys[KeyEvent.VK_LEFT] && offset == -608){
            moveScreen = true;
            dir = 1;
        }
        if(keys[KeyEvent.VK_RIGHT] && offset == 0){
            moveScreen = true;
            dir = -1;
        }
        if(moveScreen == true){
            px += 20 * dir;
            offset = px - 304;
        }
        if(offset < -608){
            offset = -608;
            moveScreen = false;
        }
        if(offset > 0){
            offset = 0;
            moveScreen = false;
        }
    }

    //move function for the customer
    public void move(){
        for(Customer c : new ArrayList<Customer>(curCustomers)){
            if(c.getDestination() != -1 && c.getCurPos()!=c.getDestination()) {//if the customer is not at their current destination
                if(c.getSwitchImageTimer() % 15 == 0 && c.getCurPos() != 15){
                    c.switchImage();//animation for walking
                }
                c.move();
            }
            else if (c.getStep()==5){ //if they finsihed eating
                customerLeave(c);
            }
            else if (c.getStep() == 6 && c.getCurPos() == 0){//if they left the restaurant
                curCustomers.remove(c);
                allObjects.remove(c);
            }
        } 
    } 


    //draw all our images and strings
    public void paint(Graphics g){ 
        if(gameScreen.equals("loading")){//for loading screen
            g.setColor(new Color(248, 246, 231));
            g.fillRect(0,0,608,1080);//background
            g.setColor(Color.GREEN);
            g.fillRect(200, 527, lw, 25);//loading bar
            lw += 1;//so loading bar increases
            g.drawImage(loadPic, 200,515,null);
            g.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20)); // sets font
            g.setColor(Color.BLACK);
            g.drawString(lw / 2 + "%", 340,580);//percent
            g.drawString("loading...", 220,580);
            loadingPicTimer += 1;
            if(loadingPicTimer == 15){//changes pic for animation
                if(iNum == 1){
                    iNum = 2;
                    loadingPicTimer = 0;
                } 
                else {
                    iNum = 1;
                    loadingPicTimer = 0;
                }
            }
            Image loadingPic = new ImageIcon("other/loading"+iNum+".png").getImage();
            g.drawImage(loadingPic, 200, 300, null);
            if(lw > 200){//changes game screen to game when done "loading"
                gameScreen = "game";
            }
        }
        else if(gameScreen.equals("game")){
            g.drawImage(gamePic,0 + offset,0,null);//game background
            g.drawImage(settingsIcon, 545,15,null);//settings button
            g.drawImage(codRating, 15,15,null);//coins and rating backgrounds
            g.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 20)); // sets font
            g.drawString(coins+"", 57,44);
            g.drawString(rating+"", 47,91);
            
            sortOrder();
            for (Stuff s: drawOrder){//draws all customers facilities and foods
                s.draw(g);
            }
            tips.draw(g);
            if(displaySettings){
                g.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 32)); // sets font
                g.drawImage(settingsPic, 86,212,null);//settings screen
                g.setColor(Color.BLACK);
                g.drawString("B A C K", 240, 650);
                g.drawString("RESTART GAME", 180, 560);
                g.drawString("S A V E", 240, 475);
                g.drawString("HOW TO PLAY", 185, 385);
                
            }
            if(displayHTP){
                g.drawImage(htp, 88,217,null);
            }
            if(displayPay){
                g.setFont(new Font ("Arial Rounded MT Bold", Font.PLAIN, 28));
                g.setColor(Color.BLACK);
                g.drawImage(paymentPage,0,0,null);//draws background
                g.drawString(cName+"",39,365);//draws user input
                g.drawString(cNum+"",39,436);
                g.drawString(cExp+"",39,511);
                g.drawString(cSec+"",306,511);

            }
            if(displayMenu){
                if(menuScreen.equals("facilities")){
                    g.drawImage(menuFacilities, 50,80,null);
                    g.setClip(menuClip);//sets clip for the screen so it only draws in part of it
                    g.drawImage(facilitiesPage, 99,menuY,null);
                    Point mouse = MouseInfo.getPointerInfo().getLocation();
                    Point offsetM = getLocationOnScreen();
                    for(rect r : objectRects){
                        if (mouse.x-offsetM.x >= r.getX() && mouse.x-offsetM.x < r.getX() + r.getWidth()){//if they hover over something it will highlight
                            if(mouse.y-offsetM.y >= r.getY() + menuY && mouse.y-offsetM.y < r.getY() + r.getHeight() + menuY){
                                r.highlight(g);
                            }
                        }
                        for(Facility f: allFacilities){//will draw the price for each faciltity
                            if(r.getName().equals(f.getName())){
                                if(!f.getStatus()){
                                    g.setColor(new Color(0,0,0,127));
                                    g.drawString(f.getPrice()+"", r.getX(), r.getY() + r.getHeight() + menuY);
                                    break;
                                }
                            }
                        }
                        for(Table t: allTables){//will draw the price for each table
                            if(r.getItem().equals(t.getTableNum())){
                                if(!t.getDesignsOwned().contains(r.getVersion())){
                                    g.setColor(new Color(0,0,0,127));
                                    g.drawString(t.getPrice(r.getVersion())+"", r.getX(), r.getY() + r.getHeight() + menuY);
                                    break;
                                }
                            }
                        }
                        for(Oven o: allOvens){//will draw the price for each oven
                            if(r.getItem().equals(o.getOvenNum())){
                                if(!o.getDesignsOwned().contains(r.getVersion())){
                                    g.setColor(new Color(0,0,0,127));
                                    g.drawString(o.getPrice(r.getVersion())+"", r.getX(), r.getY() + r.getHeight() + menuY);
                                    break;
                                }
                            }
                        }
                    }
                }
                if(menuScreen.equals("recipes")){
                    g.drawImage(menuRecipes, 55,80,null);
                    g.setClip(menuClip);
                    g.drawImage(recipesPage,99,menuY,null);
                    Point mouse = MouseInfo.getPointerInfo().getLocation();
                    Point offsetM = getLocationOnScreen();
                    for (rect r: foodRects){//highlights rect that mouse is hovering over
                        if (mouse.x-offsetM.x >= r.getX() && mouse.x-offsetM.x < r.getX() + r.getWidth()){
                            if(mouse.y-offsetM.y >= r.getY() + menuY && mouse.y-offsetM.y < r.getY() + r.getHeight() + menuY){
                                r.highlight(g); 
                            }
                        }
                    }
                }
            }
        }
    }
    

    @Override
    public void actionPerformed(ActionEvent e){
        repaint();
        if(gameScreen.equals("game")){
            moveScreen();
            move();
            openButtons();
            checkRating();
            loadCustomer();
            music();
        }
    }

    @Override
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();
        keys[code] = true;
    }
    @Override
    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode();
        keys[code] = false;
    }
    @Override
    public void keyTyped(KeyEvent e){}

    //checks for mous being clicked
    @Override
	public void mouseClicked(MouseEvent e){
        Point mouse = MouseInfo.getPointerInfo().getLocation();//get mouse location on screen
        Point offsetM = getLocationOnScreen();//mouse offset
        if (mouse.x-offsetM.x >= 545 && mouse.x-offsetM.x < 595){//if they click on settings icon
            if(mouse.y-offsetM.y >= 15 && mouse.y-offsetM.y < 65){
                if(soundEffects) clickSound.play();
                displaySettings = true;//display settings
            }
        }
        if (mouse.x-offsetM.x>=169 && mouse.x-offsetM.x < 216){//if they click on more coins icon
        	if (mouse.y-offsetM.y >= 15 && mouse.y-offsetM.y < 61){
        		if(soundEffects) clickSound.play();
        		displayPay = true;//display pay screen
        	}
        }
        if (displayHTP){
            if (htpPics.indexOf(htp)==6){
                displayHTP = false;
                htp = htpPics.get(0);
            }
            else{
                htp = htpPics.get(htpPics.indexOf(htp)+1);
            }
        }
        if (displayPay){//if pay screen
        	if (mouse.y-offsetM.y>=7 && mouse.y-offsetM.y<80){//if they click on the x button
        		if (mouse.x-offsetM.x>=7 && mouse.x-offsetM.x<77){
        			if(soundEffects) clickSound.play();
        			displayPay = false;//pay screen is false and exits screen
        		}
        	}
            if (mouse.y-offsetM.y>=333 && mouse.y-offsetM.y<369){//if they click on the Card Name
        		if (mouse.x-offsetM.x>=39 && mouse.x-offsetM.x<572){
        			if(soundEffects) clickSound.play();
                    cName = JOptionPane.showInputDialog("Name on Card:");//dialog box opens
        		}
        	}
            if (mouse.y-offsetM.y>=404 && mouse.y-offsetM.y<440){//if they click on card number
        		if (mouse.x-offsetM.x>=39 && mouse.x-offsetM.x<572){
        			if(soundEffects) clickSound.play();
                    cNum = JOptionPane.showInputDialog("Card Number:");//dialog box opens
        		}
        	}
            if (mouse.y-offsetM.y>=479 && mouse.y-offsetM.y<515){//if they click on card expiary date
        		if (mouse.x-offsetM.x>=33 && mouse.x-offsetM.x<299){
        			if(soundEffects) clickSound.play();
                    cExp = JOptionPane.showInputDialog("Expiry Date:");//dialog box opens
        		}
        	}
            if (mouse.y-offsetM.y>=479 && mouse.y-offsetM.y<515){//if they click on card security number
        		if (mouse.x-offsetM.x>=306 && mouse.x-offsetM.x<572){
        			if(soundEffects) clickSound.play();
                    cSec = JOptionPane.showInputDialog("Security Number:");//dialog box opens
        		}
        	}
		    if (mouse.y-offsetM.y>=679 && mouse.y-offsetM.y<679+54){//if they click pay
        		if (mouse.x-offsetM.x>=38 && mouse.x-offsetM.x<38+534){
        			if(soundEffects) clickSound.play();
        			if (!cardValidity(cName,cNum,cSec,cExp)){//checks if they entered valid information
                        if(soundEffects) uhOhSound.play();
                        displayPay = false;//closes pay screen
                    }
                    else{
                        Random rand = new Random();
                        if (soundEffects) sparkle.play();
                        coins += rand.nextInt(0,10000);//gives them a random amount of coins
                        displayPay = false;//closes pay screen
                    }
        		}
        	}
        }
        if(displaySettings){
            if (mouse.x-offsetM.x >= 166 && mouse.x-offsetM.x < 441){//if they click on the back button
                if(mouse.y-offsetM.y >= 612 && mouse.y-offsetM.y < 662){
                    if(soundEffects) clickSound.play();
                    displaySettings = false;//closes settings
                }
            }
            if (mouse.x-offsetM.x >= 166 && mouse.x-offsetM.x < 441){//if they click on restart
                if(mouse.y-offsetM.y >= 527 && mouse.y-offsetM.y < 577){
                    if(soundEffects) clickSound.play();
                    settings("restart");//calls settings method to restart game
                }
            }
            if (mouse.x-offsetM.x >= 166 && mouse.x-offsetM.x < 441){//if they click on save
                if(mouse.y-offsetM.y >= 437 && mouse.y-offsetM.y < 487){
                    if(soundEffects) clickSound.play();
                    settings("save");//calls setting method to save game
                }
            }
            if (mouse.x-offsetM.x >= 166 && mouse.x-offsetM.x < 441){//if they click how to play
                if(mouse.y-offsetM.y >= 347 && mouse.y-offsetM.y < 397){
                    if(soundEffects) clickSound.play();
                    settings("htp");//how to play
                    displaySettings = false;
                }
            }
            if (mouse.x-offsetM.x >= 214 && mouse.x-offsetM.x < 289){//if they click on the music button
                if(mouse.y-offsetM.y >= 284 && mouse.y-offsetM.y < 323){
                    if(soundEffects) clickSound.play();
                    if(music){//if music is playing it stops it
                        music = false;
                        soundtrack.stop();
                    }
                    else{//if not it starts the music
                        music = true;
                        soundtrack.play();
                    }

                }
            }
            if (mouse.x-offsetM.x >= 384 && mouse.x-offsetM.x < 459){//if they click on the sound effects button
                if(mouse.y-offsetM.y >= 284 && mouse.y-offsetM.y < 323){
                    if(soundEffects){//if sound effects are playing they no longer do
                        clickSound.play();
                        soundEffects = false;
                    }
                    else{//if they dont play, then they do now
                        soundEffects = true;
                    }
                }
            }

        }
        
        if(displayMenu){
            if (mouse.y-offsetM.y >= 904 && mouse.y-offsetM.y < 954){//if they click on the x button closes menu page
                if(mouse.x-offsetM.x >= 274 && mouse.x-offsetM.x < 324){
                    if(soundEffects) clickSound.play();
                    displayMenu = false;
                }
            }
            else if(menuScreen.equals("facilities")){
                if (mouse.x-offsetM.x >= 229 && mouse.x-offsetM.x < 384){//if they click on recpies page changes to recipes
                    if(mouse.y-offsetM.y >= 86 && mouse.y-offsetM.y < 134){
                        if(soundEffects) clickSound.play();
                        menuY = 178;
                        menuScreen = "recipes";
                    }
                }
                for(rect r: objectRects){//if they click on a facility will call menu with the name of facility as parameter
                    if (mouse.x-offsetM.x >= r.getX() && mouse.x-offsetM.x < r.getX() + r.getWidth()){
                        if(mouse.y-offsetM.y >= r.getY() + menuY && mouse.y-offsetM.y < r.getY() + r.getHeight() + menuY){
                            if(soundEffects) clickSound.play();
                            menuY = 178;
                            Menu(r.getName());
                        }
                    }
                }
            }
            else if(menuScreen.equals("recipes")){
                if (mouse.x-offsetM.x >= 59 && mouse.x-offsetM.x < 214){//if they click on facilities, switches to facilities page
                    if(mouse.y-offsetM.y >= 86 && mouse.y-offsetM.y < 134){
                        if(soundEffects) clickSound.play();
                        menuScreen = "facilities";
                    }
                }
                for (rect r: foodRects){//if they click on a food it opens link of that food
                    if (mouse.x-offsetM.x >= r.getX() && mouse.x-offsetM.x < r.getX() + r.getWidth()){
                        if(mouse.y-offsetM.y >= r.getY() + menuY && mouse.y-offsetM.y < r.getY() + r.getHeight() + menuY){
                            openLink(allFoods.get(foodRects.indexOf(r)).getLink());
                        }
                    }
                }
            }
        }
        for(Table t : allTables){//availTables){
            if(t.getState() && t.customer.getStep() == 2){
                if(mouse.x - offsetM.x >= t.x + 91 + offset && mouse.x - offsetM.x < t.x + 91 + offset + 70){
                    if(mouse.y - offsetM.y >= t.y - 122 && mouse.y - offsetM.y < t.y - 122 + 70){
                        if(soundEffects) clickSound.play();
                        cookFood(t.getCustomer());
                    }
                }
            }
        }
        if (mouse.x-offsetM.x >= 225 && mouse.x-offsetM.x < 318){//if they click on the tip jar
            if(mouse.y-offsetM.y >= 272 && mouse.y-offsetM.y < 400){
                if (tips.getTotal()>0) tipSound.play();
                coins += tips.withdraw();//adds to their coins
            }
        }
        
    }

    //scrolling with mouse
    @Override
    public void mouseWheelMoved(MouseWheelEvent e){
        if(displayMenu){
            int notches = e.getWheelRotation();
            int y;
            if(displayMenu && menuScreen.equals("facilities")) y = -2565;
            else y = -56;
            if (notches < 0){
                if (menuY < 178) menuY += 30;
            }
            else{
                if (menuY > y) menuY -= 30;
            }
        }
    }
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void mousePressed(MouseEvent e){}
	@Override
	public void mouseReleased(MouseEvent e){}
}
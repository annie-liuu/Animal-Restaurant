// Annie Liu, Esraa Kandil
// Facility
// Stores info about each faciltiy and draws the facility

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

class Facility extends Stuff{
    public int design, price;//facility design, price of the facility
    private int boost;//rating boost of the facility
    private Image facilityPic;//image for the facility
    private String imageName;//name of the facility
    private boolean owned;//wheter the user owns the facility or not

    public Facility(String facInfo){
        String[] fac = facInfo.split(","); // split so we can have the first element
        imageName = fac[0];
        // name = fac[0].substring(0,fac[0].length()-1);
        x = Integer.parseInt(fac[1]);
        y = Integer.parseInt(fac[2]);
        price = Integer.parseInt(fac[3]);
        design = Integer.parseInt("" + imageName.charAt(imageName.length()-1));
        facilityPic = new ImageIcon("facilities/"+imageName+".png").getImage();
        boost = Integer.parseInt(fac[4]);
    }
	public int getY(){
		return y;
	}
    public void draw(Graphics g){//draws the image
        g.drawImage(facilityPic, x + GamePanel.offset, y, null);
    }
    public int getDesign(){
    	return design;
    }
    public String getName(){
    	return imageName;
    }
    public int getPrice(){
        return price;
    }
    public boolean getStatus(){
        return owned;
    }
    public void setStatus(boolean b){//sets whether user owns facility
        owned = b;
    }
    public int getBoost(){
        return boost;
    }
}

//Annie Liu, Esraa Kandil
//rect
//stores info for the rects of the facilities and recipes on the menu pages

import javax.swing.*;
import java.awt.*;

class rect {
    public int x, y, width, height, version;//x and y coords, width and height of rect, and the version of the faciltiy
    public String name, item;//name of the facility or food including version number, item is without version number

    public rect(int xx, int yy, String n, int version){
        x = xx;
        y = yy;
        width = 111;
        height = 86; 
        name = n+version;
        item = n;
        this.version = version;
        if(version == 5){//if version is 5 then it is a food (they dont have versions)
            width = 70;//have diff widths and heights
            height = 70;
        }
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public int getVersion(){
        return version;
    }
    public String getName(){
        return name;
    }
    public String getItem(){
        return item;
    }

    //if mouse hovers over the rect it will higlight(outline) the rect
    public void highlight(Graphics g){
        g.setColor(new Color(193,181,134));
        g.drawRoundRect(x,y + GamePanel.menuY,width,height,13,13);
        g.drawRoundRect(x-1,y-1 + GamePanel.menuY,width+2,height+2,13,13);
        g.drawRoundRect(x-2,y-2 + GamePanel.menuY,width+4,height+4,13,13);
    }
}

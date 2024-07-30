// Annie Liu, Esraa Kandil
// path
// Class path for creating a path from node to node, taken from Floyd Warshal Algorithim

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.*;

class path {
    int n = 16; // there are 15 different locations the customers may go to 
    // points for all nodes
    public static Point [] verticies = {new Point(335,199), new Point(335,358),new Point(108,494),new Point(179,494),new Point(255,494),new Point(329,494),new Point
        (406,494),new Point(108,664),new Point(179,664),new Point(255,664),new Point(329,664),new Point(406,664), new Point(332,795), new Point(179, 917),
         new Point(129,333), new Point(256,320)};

    // connects nodes
    public Point [] edges = {new Point(0,1),new Point(1,2),new Point(1,3),new Point(1,4),new Point(1,5),new Point(1,6),new Point(2,3),new Point(3,4),new Point(4,5),new Point(5,6),new Point
        (3,8),new Point(5,10),new Point(7,8),new Point(8,9),new Point(9,10),new Point(10,11), new Point(8,13), new Point(10, 12), new Point(3,14), new Point(3,15), new Point(14,15), new Point(1,15)};
 
    public ArrayList<Integer> p = new ArrayList<Integer>(); // arraylist for shortest path from position to destination
    int dist[][] = new int[n][n]; // distances between the points
    ArrayList path[][] = new ArrayList[n][n];// path between the points


    public path(){
        for(int i = 0; i < n; i ++){
            for(int j = 0; j < n; j ++){
                dist[i][j] = 9999;//biggest distance possible
            }
        }
        for (Point p : edges){
            int d = distance(verticies[(int)p.getX()],verticies[(int)p.getY()]);//distance between those points
            dist[(int)p.getX()][(int)p.getY()] = d;//adds them to that index in distance
            dist[(int)p.getY()][(int)p.getX()] = d;
        }
        for(int i = 0; i < n; i ++){
            dist[i][i] = 0;//if from one point to the same point distance is 0
        }
        for(int k = 0; k < n; k ++){//possible paths between points
            for(int i = 0; i < n; i ++){
                for(int j = 0; j < n; j++){
                    if(dist[i][j] > dist[i][k] + dist[k][j]){
                        dist[i][j] = dist[i][k] + dist[k][j];
                        ArrayList<Integer> add = new ArrayList<Integer>();//arraylist for indexes
                        if(path[i][k] != null) add.addAll(path[i][k]);
                        add.add(k);
                        if(path[k][j] != null)add.addAll(path[k][j]);
                        path[i][j] = add;
                    }
                }
            }
        }
    }

    //returns the distance from 2 points
    public int distance(Point p1, Point p2){
        return (int)p1.distance(p2);
    }

    //takes in 2 indexes of coodinates in verticies and finds path between them
    public ArrayList findPath(int indexStart, int indexEnd){
        p.clear();//clears old path
        int v1 = indexStart;//starting position
        int v2 = indexEnd;//ending position
        if(path[v1][v2] != null) p.addAll(path[v1][v2]);//adds the indexes in verticies to get there
        p.add(v2);//adds the last position to path
        return p;
    }

}
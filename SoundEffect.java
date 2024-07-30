// Annie Liu, Esraa Kandil
// SoundEffect
// Taken from code that was uploaded on Edsby. 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

class SoundEffect{
        private Clip c;
        public SoundEffect(String filename){
            setClip(filename);
        }
        public void setClip(String filename){
            try{
                File f = new File(filename);
                c = AudioSystem.getClip();
                c.open(AudioSystem.getAudioInputStream(f));
            } 
            catch(Exception e){ System.out.println("error"); }
        }
        public void play(){
            c.setFramePosition(0);
            c.start();
        }
        public void stop(){
            c.stop();
        }

}
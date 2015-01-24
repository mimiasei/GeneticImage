package com.msg.geneticimage.gfx;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class FieldSlider {
	
	SliderFrame frame;
	
	public FieldSlider() {
	      EventQueue.invokeLater(new Runnable()
	         {
	            public void run()
	            {
	               frame = new SliderFrame();
	               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	               frame.setVisible(true);
	            }
	         });
	      long timer = System.currentTimeMillis();
	      long now = timer;
	      while (now - timer < 3000)
	    	  now = System.currentTimeMillis();
	      System.out.println("NBR_POLYGON_COUNT: " + frame.get("NBR_POLYGON_COUNT"));
	}
	
   public static void main(String[] args){
	   new FieldSlider();
   }
}

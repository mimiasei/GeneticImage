package com.msg.geneticimage.gfx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.msg.geneticimage.interfaces.Cons;

public class SliderFrame extends JFrame {
	
	private static final long serialVersionUID = -7065580822073520969L;
	private static final List<String> FIELD_TYPES = Arrays.asList("CHANCE", "NBR", "FACTOR");
	private static final int FONT_SIZE = 10;

	private JPanel sliderPanel;
	private List<Field> fields;
	private Hashtable<String, Integer> fieldTable;
	private Hashtable<String, JTextField> textFieldTable;
		   
	public SliderFrame() {
		
		setTitle("Value sliders");
		
		sliderPanel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setVgap(0);
		sliderPanel.setLayout(layout);
		  
		fields = Arrays.asList(Cons.class.getDeclaredFields());
		fieldTable = new Hashtable<String, Integer>();
		textFieldTable = new Hashtable<String, JTextField>();
		
		setPreferredSize(new Dimension(620, fields.size() * (FONT_SIZE << 1)));
				
		double dbl = 0.0;
		int intr = 0;
		DoubleJSlider[] sliders = new DoubleJSlider[fields.size()];
		boolean isDouble = false;
		for (int i = 0; i < fields.size(); i++) {
			
			if(fields.get(i).getType().isPrimitive() && 
					FIELD_TYPES.contains(fields.get(i).getName().
							substring(0, fields.get(i).getName().indexOf("_")))) {
				
				sliders[i] = new DoubleJSlider(0, 100, 100);
				try {
					if(fields.get(i).getType().toString().equals("double")) {
						isDouble = true;
						if(fields.get(i).getDouble(dbl) < 0.01) {
							sliders[i].setMinMax(0, 20);
							sliders[i].setScale(1000);
						}		
						sliders[i].setValue((int)(fields.get(i).getDouble(dbl) * 100));
						fieldTable.put(fields.get(i).getName(), sliders[i].getValue());
					} else if(fields.get(i).getType().toString().equals("int")) {
						isDouble = false;
						int val = fields.get(i).getInt(intr);
						sliders[i].setMinMax(0, val << (val > 100 ? 1 : 2));
						sliders[i].setValue(fields.get(i).getInt(intr));
						fieldTable.put(fields.get(i).getName(), sliders[i].getValue());
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			    
				ValueSlider valSlider = new ValueSlider(sliders[i], isDouble);
				sliderPanel.add(valSlider.getPanel(fields.get(i).getName()));
				textFieldTable.put(fields.get(i).getName(), valSlider.getTextField());
			}
		}
		add(sliderPanel, BorderLayout.CENTER);
		pack();
	}

	/**
	 * Returns value of given field as a double.
	 * @param field
	 * @return value
	 */
	public double get(String field) {
		return Double.parseDouble(textFieldTable.get(field).getText());
	}

   class ValueSlider implements ActionListener {
	   
	   JTextField textField = new JTextField(4);
	   DoubleJSlider s;
	   double dblValue = 0;
	   private boolean isDouble = false;
	   
	  ChangeListener listener = new ChangeListener() {
	  		public void stateChanged(ChangeEvent event) {
		        if (!s.getValueIsAdjusting())
		          setTextField();
	  		}
	  };
	  
	  KeyAdapter keyListener = new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent ke) {
              String typed = textField.getText();
              s.setValue(0);
              if(!typed.matches("\\d+(\\.\\d*)?")) {
                  return;
              }
              double value = Double.parseDouble(typed);
              if(isDouble)
            	  value *= s.scale;
              s.setValue((int)value);
          }
      };
      
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton c = (JButton)e.getSource();
			String field = c.getName();
			int value = fieldTable.get(field);
            s.setValue(value);
            double outValue = Double.parseDouble(textField.getText());
            System.out.println("value reset to: " + outValue);
		}
	  
	   public ValueSlider(DoubleJSlider s, boolean isDouble) {
		   this.s = s;
		   this.isDouble = isDouble;
		   setTextField();
	   }
	   
	   public void setTextField() {
		   if(isDouble)
			   textField.setText("" + s.getScaledValue());
		   else
			   textField.setText("" + s.getValue());
	   }
	   
	   public JTextField getTextField() {
		   return textField;
	   }
	   
	   public JPanel getPanel(String description) {
		  textField.addKeyListener(keyListener);
	      s.addChangeListener(listener);
	      JButton button = new JButton("Reset");
	      button.setName(description);
	      button.addActionListener(this);
	      JPanel panel = new JPanel();
	      JPanel panel2 = new JPanel();
	      JLabel label = new JLabel(description);
	      panel.setLayout(new GridLayout());
	      panel2.setLayout(new GridLayout());
	      Font f = textField.getFont();
	      Font f2 = new Font(f.getFontName(), f.getStyle(), FONT_SIZE); 
	      textField.setFont(f2);
	      s.setFont(f2);
	      label.setFont(f2);
	      panel.add(label);
	      panel.add(s);
	      panel2.add(textField);
	      textField.invalidate();
	      panel2.add(button);
	      s.setValue(s.getValue()-1);
	      s.setValue(s.getValue()+1);
	      panel.add(panel2);
	      return panel;
	   }
   }
	   
   class DoubleJSlider extends JSlider {

	    private static final long serialVersionUID = -3561306427612537364L;	    
		int scale;
		int value;

	    public DoubleJSlider(int min, int max, int scale) {
	        super(min, max, 0);
	        super.setPaintTicks(false);
	        this.scale = scale;
	    }
	    
	    public void setMinMax(int min, int max) {
	    	super.setMinimum(min);
	    	super.setMaximum(max);
		}
	    
	    public void setScale(int scale) {
	    	this.scale = scale;	
		}

		public void setValue(int value) {
	    	this.value = value;
	    	super.setValue(this.value);
	    }

	    public double getScaledValue() {
	        return ((double)super.getValue()) / this.scale;
	    }
	}
}

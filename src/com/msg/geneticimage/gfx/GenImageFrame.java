package com.msg.geneticimage.gfx;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GenImageFrame 	extends JFrame implements Runnable {

	private static final long serialVersionUID = -1697695334188842593L;
	private JPanel panel;
	private boolean started = false;
	private boolean plotData = false;
	
	public GenImageFrame() {
		setTitle("Genetic Image GUI");
		panel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		panel.setLayout(layout);
//		setPreferredSize(new Dimension(300, 200));
		
		JButton button = new JButton("Start");
		button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	started = !started;
            }
        });
		
		final JCheckBox cbPlotData = new JCheckBox("Show graph of new children data");
		cbPlotData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	plotData = cbPlotData.isSelected();
            }
        });
		
		/* Add all elements to panel. */
		panel.add(button);
		panel.add(cbPlotData);
		
		/* Add panel to frame. */
		add(panel);
		pack();
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isPlotData() {
		return plotData;
	}

	@Override
	public void run() {
		/* Set frame visible. */
		setVisible(true);
	}

}

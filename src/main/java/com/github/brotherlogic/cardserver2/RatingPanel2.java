package com.github.brotherlogic.cardserver2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class RatingPanel2 extends JPanel {

	public RatingPanel2(final ProcessRating2 processor) {
		super(new GridLayout(5, 1));

		for (int i = 1; i < 6; i++) {
			JPanel panel = new JPanel(new BorderLayout());
			JLabel label = new JLabel("" + i, JLabel.CENTER);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			final int index = i;
			System.out.println("Adding listener");
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("CLICKED HERE");
					processor.processRating(index);
				}
			});
			panel.add(label);
			this.add(panel);
		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 480);
	}
}

interface ProcessRating2 {
	void processRating(int rating);
}
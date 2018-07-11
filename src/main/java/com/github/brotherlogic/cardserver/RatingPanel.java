package com.github.brotherlogic.cardserver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class RatingPanel extends JPanel {

	public RatingPanel(final ProcessRating processor) {
		super(new GridLayout(5, 1));

		for (int i = 1; i < 6; i++) {
			JPanel panel = new JPanel(new BorderLayout());
			JLabel label = new JLabel("" + i, JLabel.CENTER);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			final int index = i;
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
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

interface ProcessRating {
	void processRating(int rating);
}

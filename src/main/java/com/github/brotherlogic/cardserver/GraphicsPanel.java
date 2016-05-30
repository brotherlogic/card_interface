package com.github.brotherlogic.cardserver;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class GraphicsPanel extends JPanel {

	Image img;

	public GraphicsPanel(Image image) {
		img = image;
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	@Override
	public Dimension getPreferredSize() {
		return getParent().getSize();
	}

}

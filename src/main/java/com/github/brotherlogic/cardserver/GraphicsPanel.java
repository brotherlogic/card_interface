package com.github.brotherlogic.cardserver;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class GraphicsPanel extends JPanel {

	Image img, scaledImg;

	public GraphicsPanel(Image image) {
		System.out.println("CREATED GP");
		img = image;
	}

	public void setImage(Image i) {
		img = i;
		scaledImg = null;
	}

	@Override
	public void paint(Graphics g) {
		long sTime = System.currentTimeMillis();
		int imgHeight = img.getHeight(null);
		int imgWidth = img.getWidth(null);

		double scaleFactor = (imgHeight + 0.0) / this.getHeight();

		int scaledHeight = (int) (Math.ceil(imgHeight / scaleFactor));
		int scaledWidth = (int) (Math.ceil(imgWidth / scaleFactor));

		if (scaledImg == null) {
			System.out.println("SCALED = " + scaledHeight + "," + this.getHeight());
			scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
			System.out.println("DRAWING: " + scaledImg.getHeight(null) + "," + scaledImg.getWidth(null));
			System.out.println("SCALED in " + ((System.currentTimeMillis()) - sTime) + "ms");
		}
		g.drawImage(scaledImg, (this.getWidth() - scaledWidth) / 2, 0, null);

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(480, 480);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
}

package com.github.brotherlogic.cardserver2;

import java.awt.Dimension;

import javax.swing.JFrame;

public class CardInterface2 extends JFrame {

	View view;

	public CardInterface2(Controller c) {
		view = new View(c);
		view.setPreferredSize(new Dimension(800, 480));
		this.add(view);
	}

	public ModelListener getListener() {
		return view;
	}

	public void display() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.revalidate();
		this.setVisible(true);
	}

}

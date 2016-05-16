package com.github.brotherlogic.cardserver;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import card.CardOuterClass.Card;

public class CardInterface extends JFrame {
	
	JPanel mainPanel;
	
	public CardInterface()
	{
		mainPanel = new JPanel();
		this.add(mainPanel);
	}
	
	public void showCard(Card card){
		JLabel label = new JLabel(card.getText());
		mainPanel.removeAll();
		mainPanel.add(label);
		
		this.revalidate();
	}
		
    public static void main(String[] args) {
    	CardInterface mine = new CardInterface();
    	mine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mine.setSize(500,500);
    	mine.setLocationRelativeTo(null);
    	mine.setVisible(true);
    	
    	Card c = Card.newBuilder().setText("Testing").build();
    	mine.showCard(c);
    }
}

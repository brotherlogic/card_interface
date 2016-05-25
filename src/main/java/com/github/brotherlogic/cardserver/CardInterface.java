package com.github.brotherlogic.cardserver;


import java.util.List;

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
    	
    	CardReader reader = new RPCCardReader("10.0.1.17", 50051);
    	
    	List<Card> cards = reader.readCards();
    	
    	if (cards.size() > 0)
    		mine.showCard(reader.readCards().get(0));
    	else
    		mine.showCard(Card.newBuilder().setText("No Cards To Show").build());
    }
}

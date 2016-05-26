package com.github.brotherlogic.cardserver;


import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import card.CardOuterClass.Card;

public class CardInterface extends JFrame {
	
	JPanel mainPanel;
	static boolean first = true;
	
	public CardInterface()
	{
		mainPanel = new JPanel();
		this.add(mainPanel);
	}
	
	public void showCard(Card card){
		JLabel label = new JLabel(card.getText());
		mainPanel.removeAll();
		mainPanel.invalidate();
		mainPanel.add(label);
		
		mainPanel.revalidate();
		mainPanel.repaint();
	}
		
    public static void main(String[] args) {
    	final CardInterface mine = new CardInterface();
    	mine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mine.setSize(500,500);
    	mine.setLocationRelativeTo(null);
    	mine.setVisible(true);
    	
    	CardReader reader = new RPCCardReader("10.0.1.17", 50051);
    	
    	reader.readCardsBackground(new CardsReturned(){
    		public void processCards(List<Card> cards){
    	    	if (!first && cards.size() > 0)
    	    		mine.showCard(cards.get(0));
    	    	else
    	    		mine.showCard(Card.newBuilder().setText("No Cards To Show").build());
    	    	first = false;
    	    }
    	});	
    }
}

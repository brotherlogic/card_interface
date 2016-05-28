package com.github.brotherlogic.cardserver;


import java.awt.Desktop;
import java.net.URI;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import card.CardOuterClass.Card;
import card.CardOuterClass.Card.Action;

public class CardInterface extends JFrame {
	
	JPanel mainPanel;
	static boolean refresh = true;
	
	public CardInterface()
	{
		mainPanel = new JPanel();
		this.add(mainPanel);
	}
	
	public void showCard(Card card){
		
		if (card.getAction() == Action.VISITURL)
		{
			//Bring up a browser
			if (Desktop.isDesktopSupported()){
				try{
					Desktop.getDesktop().browse(new URI(card.getText()));
					refresh = false;
				} catch (Exception e){
					JLabel label = new JLabel(e.getMessage());
					mainPanel.removeAll();
					mainPanel.invalidate();
					mainPanel.add(label);
					
					mainPanel.revalidate();
					mainPanel.repaint();
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Unable to bring up browser");
			}
		}
		else {
		JLabel label = new JLabel(card.getText());
		mainPanel.removeAll();
		mainPanel.invalidate();
		mainPanel.add(label);
		
		mainPanel.revalidate();
		mainPanel.repaint();
		}
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
    			System.out.println(cards);
    	    	if (cards.size() > 0)
    	    		mine.showCard(cards.get(0));
    	    	else
    	    		mine.showCard(Card.newBuilder().setText("No Cards To Show").build());
    	    }
    	});	
    }
}

package com.github.brotherlogic.cardserver;

import java.util.List;

import card.CardOuterClass.Card;

public abstract class CardReader {

	// Update every 5 seconds
	private final int WAIT_TIME = 1000 * 5;

	public abstract List<Card> readCards(Card.Channel channel);

	public void readCardsBackground(CardsReturned callback, Card.Channel channel) {
		while (true) {
			if (CardInterface.refresh)
				callback.processCards(readCards(channel));
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

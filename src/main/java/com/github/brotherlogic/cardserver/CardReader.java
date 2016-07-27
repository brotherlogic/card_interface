package com.github.brotherlogic.cardserver;

import java.util.List;

import card.CardOuterClass.Card;

public abstract class CardReader {

	// Update every 60 seconds
	private final int WAIT_TIME = 1000 * 60;

	public abstract List<Card> readCards();

	public void readCardsBackground(CardsReturned callback) {
		while (true) {
			if (CardInterface.refresh)
				callback.processCards(readCards());
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

package com.github.brotherlogic.cardserver2;

import java.util.LinkedList;
import java.util.List;

import card.CardOuterClass.Card;
import card.CardOuterClass.Card.Action;

/**
 * Model holds the state of the world
 * 
 * @author simon
 *
 */
public class Model {
	private CardInterfaceServer2 s;
	private Card currentCard;
	private List<ModelListener> listeners = new LinkedList<ModelListener>();

	public Model(CardInterfaceServer2 server) {
		s = server;
	}

	public void NewCard(Card nCard) {
		// Is this actually a new card?
		if ((nCard == null && currentCard != null) || currentCard == null
				|| !currentCard.getText().equals(nCard.getText())) {
			System.err.println("Read new Card!");

			currentCard = nCard;

			for (ModelListener listener : listeners) {
				listener.newCard(currentCard);
			}
		}
	}

	public void AddListener(ModelListener l) {
		if (l != null)
			listeners.add(l);
	}

	public void ReleaseCard() {
		if (currentCard.getAction() == Action.DISMISS) {
			CardProxy.Overwrite(currentCard.getResult(), currentCard.getHash(), s.getHost("cardserver"),
					s.getPort("cardserver"));
			NewCard(null);
		}
	}

	public void ScoreCard(int score) {
		if (currentCard.getAction() == Action.RATE) {
			Card r = Card.newBuilder().mergeFrom(currentCard.getResult()).addActionMetadata("" + score).build();
			CardProxy.Overwrite(r, currentCard.getHash(), s.getHost("cardserver"), s.getPort("cardserver"));
			NewCard(null);
		}
	}
}

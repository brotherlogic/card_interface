package com.github.brotherlogic.cardserver;

import java.util.List;

import card.CardOuterClass.Card;

public interface CardsReturned {
	void processCards(List<Card> cards);
}

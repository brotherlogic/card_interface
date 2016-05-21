package com.github.brotherlogic.cardserver;

import java.util.List;

import card.CardOuterClass.Card;

public abstract class CardReader {

	public abstract List<Card> readCards();
	
}

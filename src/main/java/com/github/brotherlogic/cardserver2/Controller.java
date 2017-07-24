package com.github.brotherlogic.cardserver2;

public class Controller implements ProcessRating2 {

	Model m;

	public Controller(Model mod) {
		m = mod;
	}

	@Override
	public void processRating(int rating) {
		m.ScoreCard(rating);
	}

	public void dismiss() {
		m.ReleaseCard();
	}
}

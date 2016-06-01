package com.github.brotherlogic.cardserver;

import java.util.concurrent.TimeUnit;

import card.CardOuterClass.Card;
import card.CardOuterClass.CardList;
import card.CardServiceGrpc;
import card.CardServiceGrpc.CardServiceBlockingStub;
import card.CardServiceGrpc.CardServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CardWriter {

	public void writeCard(Card card) {
		System.out.println("WRITING " + card + " to " + CardInterface.host + ":" + CardInterface.port);

		ManagedChannel channel = ManagedChannelBuilder.forAddress(CardInterface.host, CardInterface.port)
				.usePlaintext(true).build();
		CardServiceBlockingStub blockingStub = CardServiceGrpc.newBlockingStub(channel);
		CardList list = CardList.newBuilder().addCards(card).build();
		blockingStub.addCards(list);
		try {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

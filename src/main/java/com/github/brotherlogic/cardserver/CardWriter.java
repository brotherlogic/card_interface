package com.github.brotherlogic.cardserver;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

import card.CardOuterClass.Card;
import card.CardOuterClass.CardList;
import card.CardServiceGrpc;
import card.CardServiceGrpc.CardServiceBlockingStub;

public class CardWriter {

	public void writeCard(Card card) {
		ManagedChannel channel = ManagedChannelBuilder
				.forAddress("localhost", 50051).usePlaintext(true).build();
		CardServiceBlockingStub blockingStub = CardServiceGrpc
				.newBlockingStub(channel);
		CardList list = CardList.newBuilder().addCards(card).build();
		blockingStub.addCards(list);
		try {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

package com.github.brotherlogic.cardserver;

import java.util.concurrent.TimeUnit;

import card.CardOuterClass.Card;
import card.CardOuterClass.CardList;
import card.CardServiceGrpc;
import card.CardServiceGrpc.CardServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CardWriter {

	CardInterfaceServer server;

	public CardWriter(CardInterfaceServer server) {
		this.server = server;
	}

	public void writeCard(Card card) {
		System.out.println("Writing card: " + card);
		ManagedChannel channel = ManagedChannelBuilder
				.forAddress(server.getHost("cardserver"), server.getPort("cardserver")).usePlaintext(true).build();
		System.out.println("WRITTEN TO " + server.getHost("cardserver"));
		CardServiceBlockingStub blockingStub = CardServiceGrpc.newBlockingStub(channel);
		CardList list = CardList.newBuilder().addCards(card).build();
		blockingStub.addCards(list);
		System.out.println("Added command");
		try {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("WRITTEN CARDS");
	}
}

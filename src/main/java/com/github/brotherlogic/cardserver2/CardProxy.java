package com.github.brotherlogic.cardserver2;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import card.CardOuterClass.Card;
import card.CardOuterClass.CardList;
import card.CardOuterClass.DeleteRequest;
import card.CardOuterClass.Empty;
import card.CardServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CardProxy {

	public static void Overwrite(Card c, String hash, String host, int port) {
		ManagedChannel client = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
		CardServiceGrpc.CardServiceBlockingStub blockingStub = CardServiceGrpc.newBlockingStub(client);

		try {
			blockingStub.deleteCards(DeleteRequest.newBuilder().setHash(hash).build());
			blockingStub.addCards(CardList.newBuilder().addCards(c).build());
			client.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Card Read(Card.Channel channel, String host, int port) {
		List<Card> cards = new LinkedList<Card>();

		System.out.println("READING FROM " + host + " and " + port);
		if (port > 0 && host != null) {
			ManagedChannel client = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
			CardServiceGrpc.CardServiceBlockingStub blockingStub = CardServiceGrpc.newBlockingStub(client);

			try {
				CardList list = blockingStub.getCards(Empty.getDefaultInstance());

				for (Card card : list.getCardsList()) {
					if (channel == null || card.getChannel().equals(channel))
						cards.add(card);
				}
				client.shutdown().awaitTermination(5, TimeUnit.SECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("READ: " + cards);
		}
		if (cards.size() > 0) {
			return cards.get(0);
		} else {
			return null;
		}
	}

}

package com.github.brotherlogic.cardserver;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import card.CardOuterClass.Card;
import card.CardOuterClass.CardList;
import card.CardOuterClass.Empty;
import card.CardServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class RPCCardReader extends CardReader {

	private String host;
	private int port;

	private ManagedChannel channel;
	private CardServiceGrpc.CardServiceBlockingStub blockingStub;

	public RPCCardReader(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public List<Card> readCards(Card.Channel rChan) {
		List<Card> cards = new LinkedList<Card>();

		System.out.println("READING FROM " + host + " and " + port);
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
		blockingStub = CardServiceGrpc.newBlockingStub(channel);

		try {
			CardList list = blockingStub.getCards(Empty.getDefaultInstance());

			for (Card card : list.getCardsList()) {
				if (rChan == null || card.getChannel().equals(rChan))
					cards.add(card);
			}
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("READ: " + cards);
		return cards;
	}
}

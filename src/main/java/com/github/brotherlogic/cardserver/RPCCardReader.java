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

	private CardInterfaceServer server;
	private ManagedChannel channel;
	private CardServiceGrpc.CardServiceBlockingStub blockingStub;

	public RPCCardReader(CardInterfaceServer s) {
		server = s;
	}

	@Override
	public List<Card> readCards(Card.Channel rChan) {
		List<Card> cards = new LinkedList<Card>();

		String host = server.getHost("cardserver");
		int port = server.getPort("cardserver");
		System.out.println("READING FROM " + host + " and " + port);
		if (port > 0) {
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
		}
		return cards;
	}
}

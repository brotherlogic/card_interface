package com.github.brotherlogic.cardserver;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.github.brotherlogic.javaserver.JavaServer;

import card.CardOuterClass.Card;
import io.grpc.BindableService;

public class CardInterfaceServer extends JavaServer {

	CardInterface mainDisplay = new CardInterface(this);
	Card.Channel mainChan;

	public CardInterfaceServer(Card.Channel chan) {
		mainChan = chan;
	}

	@Override
	public String getServerName() {
		return "CardInterface";
	}

	@Override
	public List<BindableService> getServices() {
		return new LinkedList<BindableService>();
	}

	private void displayScreen() {
		mainDisplay.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainDisplay.pack();
		mainDisplay.setLocationRelativeTo(null);
		mainDisplay.revalidate();
		mainDisplay.setVisible(true);
	}

	private void showCard(final Card c) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainDisplay.showCard(c);
			}
		});
	}

	@Override
	public void localServe() {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				displayScreen();
			}
		});

		CardReader reader = new RPCCardReader(getHost("cardserver"), getPort("cardserver"));

		reader.readCardsBackground(new CardsReturned() {
			@Override
			public void processCards(List<Card> cards) {
				Log(cards.size());

				if (cards.size() > 0) {
					for (int i = 0; i < cards.size(); i++) {
						System.out.println("HERE " + i + " => " + cards.get(i));
					}
					showCard(cards.get(0));
				} else
					showCard(Card.newBuilder().setText("No Cards To Show (" + getHost() + ":" + getPort() + ")")
							.build());
			}
		}, mainChan);

	}

	public static void main(String[] args) throws Exception {
		Option optionHost = OptionBuilder.withLongOpt("server").hasArg().withDescription("Hostname of server")
				.create("s");
		Option optionChannel = OptionBuilder.withLongOpt("channel").hasArg().withDescription("Channel").create("c");
		Options options = new Options();
		options.addOption(optionHost);
		options.addOption(optionChannel);
		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);

		String rServer = "192.168.68.34";
		System.out.println("ARGS = " + Arrays.toString(args));
		if (line.hasOption("server"))
			rServer = line.getOptionValue("s");

		Card.Channel channel = null;
		if (line.hasOption("channel")) {
			if (line.getOptionValue("c").equals("issues")) {
				channel = Card.Channel.ISSUES;
			} else if (line.getOptionValue("c").equals("music")) {
				channel = Card.Channel.MUSIC;
			}
		}

		CardInterfaceServer server = new CardInterfaceServer(channel);
		server.Serve(rServer);
	}

}

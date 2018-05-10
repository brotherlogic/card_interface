package com.github.brotherlogic.cardserver;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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
		super.setTime(10,20);
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
		final double r = Math.random();
		System.out.println(new Date() + " OUTER (" + r + ") : " + c);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				System.out.println(new Date() + " INNER (" + r + "): " + c);
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

		CardReader reader = new RPCCardReader(this);

		reader.readCardsBackground(new CardsReturned() {
			@Override
			public void processCards(List<Card> cards) {
				if (cards.size() > 0) {
					for (int i = 0; i < cards.size(); i++) {
						System.out.println("HERE " + i + " => " + cards.get(i));
					}
					System.out.println("SHOWING THIS CARD: " + cards.get(0));
					showCard(cards.get(0));
				} else
					showCard(Card.newBuilder().setText("No Cards To Show (" + getHost() + ":" + getPort() + ")")
							.build());
			}
		}, mainChan);

	}

	public static void main(String[] args) throws Exception {
		// Read the resources and print to stdout
		try {
			Properties p = new Properties();
			p.load((CardInterfaceServer.class.getResourceAsStream("properties.txt")));
			System.out.println(p.getProperty("version"));
			System.out.println(p.getProperty("build.date"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Option optionHost = OptionBuilder.withLongOpt("server").hasArg().withDescription("Hostname of server")
				.create("s");
		Option optionChannel = OptionBuilder.withLongOpt("channel").hasArg().withDescription("Channel").create("c");
		Options options = new Options();
		options.addOption(optionHost);
		options.addOption(optionChannel);
		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);

		String rServer = "192.168.68.64";
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

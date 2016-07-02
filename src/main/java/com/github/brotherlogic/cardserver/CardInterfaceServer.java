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

	CardInterface mainDisplay = new CardInterface();

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
		mainDisplay.setSize(800, 480);
		mainDisplay.setLocationRelativeTo(null);
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

		System.out.println("PORT = " + getPort("cardserver"));
		CardReader reader = new RPCCardReader(getHost("cardserver"), getPort("cardserver"));

		reader.readCardsBackground(new CardsReturned() {
			@Override
			public void processCards(List<Card> cards) {
				System.out.println("Seen " + cards);

				if (cards.size() > 0)
					showCard(cards.get(0));
				else
					showCard(Card.newBuilder().setText("No Cards To Show (" + getHost() + ":" + getPort() + ")")
							.build());
			}
		});

	}

	public static void main(String[] args) throws Exception {
		Option optionHost = OptionBuilder.withLongOpt("host").hasArg().withDescription("Hostname of server")
				.create("h");
		Option optionPort = OptionBuilder.withLongOpt("port").hasArg().withDescription("Port number of server")
				.create("p");
		Options options = new Options();
		options.addOption(optionHost);
		options.addOption(optionPort);
		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);

		String host = "10.0.1.17";
		System.out.println("ARGS = " + Arrays.toString(args));
		if (line.hasOption("host"))
			host = line.getOptionValue("h");
		int port = 50051;
		if (line.hasOption("port"))
			port = Integer.parseInt(line.getOptionValue("p"));

		CardInterfaceServer server = new CardInterfaceServer();
		System.out.println("SERVING " + host + " and " + port);
		server.Serve(host, port);
	}

}

package com.github.brotherlogic.cardserver;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import card.CardOuterClass.Card;
import card.CardOuterClass.DeleteRequest;
import card.CardServiceGrpc;

public class CardInterface extends JFrame {

	JPanel mainPanel;
	static boolean refresh = true;
	static String host;
	static int port;

	public CardInterface(String serverHost, int serverPort) {
		host = serverHost;
		port = serverPort;

		mainPanel = new JPanel();
		this.add(mainPanel);
	}

	public void deleteCard(String hash) {
		ManagedChannel channel = ManagedChannelBuilder
				.forAddress("localhost", 50051).usePlaintext(true).build();
		CardServiceGrpc.CardServiceBlockingStub blockingStub = CardServiceGrpc
				.newBlockingStub(channel);

		try {
			DeleteRequest req = DeleteRequest.newBuilder().setHash(hash)
					.build();
			blockingStub.deleteCards(req);
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showCard(Card card) {

		if (card.getAction() == Card.Action.VISITURL) {

			// Delete the card from the server
			deleteCard(card.getHash());

			// Bring up a browser
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI(card.getText()));
					refresh = false;

					PortListener listener = new PortListener(8090);
					String response = listener.listen();

					Card strCard = Card.newBuilder().setText(response)
							.setHash("instagramauthresp").build();
					new CardWriter().writeCard(strCard);

					refresh = true;
				} catch (Exception e) {
					JLabel label = new JLabel(e.getMessage());
					mainPanel.removeAll();
					mainPanel.invalidate();
					mainPanel.add(label);

					mainPanel.revalidate();
					mainPanel.repaint();
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Unable to bring up browser");
			}
		} else {
			JLabel label = new JLabel(card.getText());
			mainPanel.removeAll();
			mainPanel.invalidate();
			mainPanel.add(label);

			mainPanel.revalidate();
			mainPanel.repaint();
		}
	}

	public static void main(String[] args) throws Exception {
		Option optionHost = OptionBuilder.withLongOpt("host").hasArg()
				.withDescription("Hostname of server").create("h");
		Option optionPort = OptionBuilder.withLongOpt("port").hasArg()
				.withDescription("Port number of server").create("p");
		Options options = new Options();
		options.addOption(optionHost);
		options.addOption(optionPort);
		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);

		final CardInterface mine = new CardInterface(line.getOptionValue("h"),
				Integer.parseInt(line.getOptionValue("p")));
		mine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mine.setSize(500, 500);
		mine.setLocationRelativeTo(null);
		mine.setVisible(true);

		CardReader reader = new RPCCardReader("localhost", 50051);

		reader.readCardsBackground(new CardsReturned() {
			@Override
			public void processCards(List<Card> cards) {
				if (cards.size() > 0)
					mine.showCard(cards.get(0));
				else
					mine.showCard(Card.newBuilder().setText("No Cards To Show")
							.build());
			}
		});
	}
}

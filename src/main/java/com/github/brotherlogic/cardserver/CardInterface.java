package com.github.brotherlogic.cardserver;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import card.CardOuterClass.Card;
import card.CardOuterClass.Card.Action;
import card.CardOuterClass.DeleteRequest;
import card.CardServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CardInterface extends JFrame {

	JPanel mainPanel;
	Insets insets;
	static boolean refresh = true;
	private CardInterfaceServer server;

	public CardInterface(CardInterfaceServer server) {
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setPreferredSize(new Dimension(800, 480));
		insets = mainPanel.getInsets();
		this.add(mainPanel);
		this.server = server;
	}

	public void deleteCard(String hash) {
		ManagedChannel channel = ManagedChannelBuilder
				.forAddress(server.getHost("cardserver"), server.getPort("cardserver")).usePlaintext(true).build();
		CardServiceGrpc.CardServiceBlockingStub blockingStub = CardServiceGrpc.newBlockingStub(channel);

		try {
			DeleteRequest req = DeleteRequest.newBuilder().setHash(hash).build();
			blockingStub.deleteCards(req);
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);

			showCard(Card.newBuilder().setText("Waiting for card..").build());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private GraphicsPanel showCardImage(final Card card) {
		if (card.getImage().length() > 0) {
			mainPanel.removeAll();
			mainPanel.invalidate();

			try {
				Image img = ImageIO.read(new URL(card.getImage()));
				GraphicsPanel panel = new GraphicsPanel(img);
				mainPanel.add(panel);
				return panel;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public void showCard(final Card card) {

		System.out.println("CARD = " + card);
		server.Log("Showing Card: " + card.getText());

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

					Card strCard = Card.newBuilder().setText(response).setHash("instagramauthresp").build();
					new CardWriter(server).writeCard(strCard);

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
				JOptionPane.showMessageDialog(null, "Unable to bring up browser");
			}
		} else if (card.getAction() == Card.Action.DISMISS) {
			GraphicsPanel panel = showCardImage(card);
			if (panel != null) {
				panel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						deleteCard(card.getHash());
						Card toWrite = card.getResult();
						new CardWriter(server).writeCard(toWrite);
					}
				});
				panel.setBounds(400 - 240, 0, 480, 480);
				mainPanel.add(panel);
				mainPanel.invalidate();
				mainPanel.revalidate();
				mainPanel.repaint();
			} else {
				JLabel label = new JLabel(card.getText(), JLabel.CENTER);
				label.setBounds(0, 0, 800, 480);
				mainPanel.removeAll();
				mainPanel.add(label);
				mainPanel.invalidate();
				mainPanel.revalidate();
				mainPanel.repaint();
				System.out.println("SHOWING " + label.getText());
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						deleteCard(card.getHash());
					}
				});
			}
		} else if (card.getAction() == Card.Action.RATE) {
			System.out.println("RATE = " + card);
			// Display the image if it has one
			if (card.getImage().length() > 0) {
				mainPanel.removeAll();
				mainPanel.invalidate();

				try {
					Image img = ImageIO.read(new URL(card.getImage()));
					GraphicsPanel panel = new GraphicsPanel(img);
					RatingPanel rPanel = new RatingPanel(new ProcessRating() {
						@Override
						public void processRating(int rating) {
							Card toWrite = Card.newBuilder().mergeFrom(card.getResult()).addActionMetadata("" + rating)
									.build();
							new CardWriter(server).writeCard(toWrite);
							deleteCard(card.getHash());
						}
					});
					mainPanel.add(panel);
					panel.setBounds(400 - 240, 0, 480, 480);
					mainPanel.add(rPanel);
					rPanel.setBounds(800 - 100, 0, 100, 480);
					System.out.println("Showing");

				} catch (Exception e) {
					JLabel label = new JLabel(e.getLocalizedMessage());
					mainPanel.add(label);
				}
			} else {
				JLabel label = new JLabel(card.getText(), JLabel.CENTER);
				mainPanel.removeAll();
				mainPanel.invalidate();
				RatingPanel rPanel = new RatingPanel(new ProcessRating() {
					@Override
					public void processRating(int rating) {
						Card toWrite = Card.newBuilder().mergeFrom(card.getResult()).addActionMetadata("" + rating)
								.build();
						new CardWriter(server).writeCard(toWrite);
						deleteCard(card.getHash());
					}
				});
				mainPanel.add(label, BorderLayout.NORTH);
				mainPanel.add(rPanel, BorderLayout.EAST);
			}
		} else {
			if (card.getImage().length() > 0) {
				mainPanel.removeAll();
				mainPanel.invalidate();

				try {
					Image img = ImageIO.read(new URL(card.getImage()));
					GraphicsPanel panel = new GraphicsPanel(img);
					mainPanel.add(panel);

					panel.addMouseListener(new MouseAdapter() {

						@Override
						public void mouseClicked(MouseEvent e) {

							deleteCard(card.getHash());

							// Add a like card
							Card c = Card.newBuilder().setText(card.getText()).setAction(Action.RATE)
									.addActionMetadata("1").build();
							new CardWriter(server).writeCard(c);
						}
					});
				} catch (Exception e) {
					JLabel label = new JLabel(e.getLocalizedMessage());
					mainPanel.add(label);
				}
			} else {
				System.out.println("Showing the text: " + card.getText());
				JLabel label = new JLabel(card.getText(), JLabel.CENTER);
				label.setBounds(0, 0, 800, 480);
				mainPanel.removeAll();
				mainPanel.invalidate();
				mainPanel.add(label, BorderLayout.NORTH);

			}
		}

		mainPanel.revalidate();
		mainPanel.repaint();
	}
}

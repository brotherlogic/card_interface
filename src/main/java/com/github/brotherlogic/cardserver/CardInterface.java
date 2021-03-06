package com.github.brotherlogic.cardserver;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

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
	GraphicsPanel panel = new GraphicsPanel(null);

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
				if (!card.getImage().equals(currentImage)) {
					Image img = ImageIO.read(new URL(card.getImage()));
					panel.setImage(img);
					currentImage = card.getImage();
				}
				mainPanel.add(panel);
				return panel;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	String currentImage = "";

	public void showCard(final Card card) {
		Thread.dumpStack();

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

				for (MouseListener m : panel.getListeners(MouseListener.class)) {
					panel.removeMouseListener(m);
				}

				MouseListener m = new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						deleteCard(card.getHash());
						Card toWrite = card.getResult();
						new CardWriter(server).writeCard(toWrite);
					}
				};

				panel.addMouseListener(m);
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
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						deleteCard(card.getHash());
						Card toWrite = card.getResult();
						new CardWriter(server).writeCard(toWrite);
					}
				});
			}
		} else if (card.getAction() == Card.Action.RATE) {
			// Display the image if it has one
			if (card.getImage().length() > 0) {
				mainPanel.removeAll();
				mainPanel.invalidate();

				try {
					if (!card.getImage().equals(currentImage)) {
						Image img = ImageIO.read(new URL(card.getImage()));
						panel.setImage(img);
						currentImage = card.getImage();
					}
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

				} catch (Exception e) {
					JLabel label = new JLabel(e.getLocalizedMessage());
					mainPanel.add(label);
				}
			} else {
				JLabel label = new JLabel(card.getText(), JLabel.CENTER);
				label.setBounds(400 - 240, 0, 480, 480);
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
				rPanel.setBounds(800 - 100, 0, 100, 480);
				mainPanel.add(label);
				mainPanel.add(rPanel);
			}
		} else {
			if (card.getImage().length() > 0) {
				mainPanel.removeAll();
				mainPanel.invalidate();

				try {
					if (!card.getImage().equals(currentImage)) {
						Image img = ImageIO.read(new URL(card.getImage()));
						panel.setImage(img);
						currentImage = card.getImage();
					}
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
				JTextPane label = new JTextPane();
				label.setEditable(false);
				label.setText(card.getText());
				label.setBounds(0, 0, 800, 480);

				StyledDocument doc = label.getStyledDocument();
				SimpleAttributeSet center = new SimpleAttributeSet();
				StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
				doc.setParagraphAttributes(0, doc.getLength(), center, false);

				mainPanel.removeAll();
				mainPanel.invalidate();
				mainPanel.add(label, BorderLayout.NORTH);

			}
		}

		mainPanel.revalidate();
		mainPanel.repaint();
	}
}

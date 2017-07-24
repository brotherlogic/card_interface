package com.github.brotherlogic.cardserver2;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.github.brotherlogic.cardserver.GraphicsPanel;

import card.CardOuterClass.Card;
import card.CardOuterClass.Card.Action;

public class View extends JPanel implements ModelListener {

	GraphicsPanel gp;
	RatingPanel2 rp;
	JTextPane text;
	Controller c;

	public View(final Controller c) {
		gp = new GraphicsPanel(null);
		rp = new RatingPanel2(c);
		text = new JTextPane();
		text.setEditable(false);
		text.setBounds(0, 0, 800, 480);

		StyledDocument doc = text.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				c.dismiss();
			}
		});
		this.c = c;

		this.setLayout(new BorderLayout());

		gp.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				c.dismiss();
			}

		});
	}

	@Override
	public void newCard(final Card card) {

		System.err.println("NEW CARD: " + card);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (card == null) {
					removeAll();
				} else {

					boolean hasImage = false;
					boolean needsRating = false;
					if (card.getImage().length() > 0) {
						try {
							gp.setImage(ImageIO.read(new URL(card.getImage())));
							hasImage = true;
						} catch (IOException e) {
							System.err.println("Unable to read URL: " + card.getImage());
						}
					}
					text.setText(card.getText());

					needsRating = (card.getAction() == Action.RATE);

					// Build the display
					removeAll();
					if (hasImage) {
						add(gp, BorderLayout.CENTER);
					} else {
						add(text, BorderLayout.CENTER);
					}

					if (needsRating) {
						add(rp, BorderLayout.EAST);
					}
				}

				// Force a redraw
				System.err.println("Invalidating");
				revalidate();
				repaint();
			}
		});
	}
}

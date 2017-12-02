package com.github.brotherlogic.cardserver2;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.brotherlogic.javaserver.JavaServer;

import card.CardOuterClass.Card;
import io.grpc.BindableService;

public class CardInterfaceServer2 extends JavaServer {

	Model m;
	Card.Channel channel;
	CardInterface2 i;

	public CardInterfaceServer2(Card.Channel channel) {
		this.channel = channel;

		m = new Model(this);
		Controller c = new Controller(m);
		i = new CardInterface2(c);
		m.AddListener(i.getListener());
	}

	@Override
	public String getServerName() {
		return "CardInterface2";
	}

	@Override
	public List<BindableService> getServices() {
		return new LinkedList<BindableService>();
	}

	@Override
	public void localServe() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				i.display();
			}
		});

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000 * 5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Card c = CardProxy.Read(channel, getHost("cardserver"), getPort("cardserver"));
					m.NewCard(c);
				}
			}
		});
		t.start();
	}

	public static void main(String[] args) throws ParseException {
		Option optionHost = OptionBuilder.withLongOpt("server").hasArg().withDescription("Hostname of server")
				.create("s");
		Option optionChannel = OptionBuilder.withLongOpt("channel").hasArg().withDescription("Channel").create("c");
		Options options = new Options();
		options.addOption(optionHost);
		options.addOption(optionChannel);
		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);

		String rServer = "noserver";
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

		if (rServer.equals("noserver")) {
			System.err.println("No server specified!");
			System.exit(1);
		}

		CardInterfaceServer2 server = new CardInterfaceServer2(channel);
		server.Serve(rServer);
	}

}

package com.github.brotherlogic.cardserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PortListener {

	private int portNumber;

	public PortListener(int number) {
		portNumber = number;
	}

	public String listen() throws Exception {
		String retString = "";

		ServerSocket sock = new ServerSocket(portNumber);
		Socket client = sock.accept();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		boolean read = false;
		for (String line = br.readLine(); line != null && !read; line = br
				.readLine())
			if (line.startsWith("GET")) {
				retString += line + "\n";
				read = true;
			} else {
				System.out.println("SKIPPING: " + line);
			}
		sock.close();
		client.close();

		return retString;
	}

	public static void main(String[] args) throws Exception {
		PortListener pl = new PortListener(8090);
		System.out.println(pl.listen());
	}
}

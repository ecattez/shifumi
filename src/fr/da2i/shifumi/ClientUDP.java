package fr.da2i.shifumi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import fr.da2i.shifumi.Message.Status;

public class ClientUDP {
	
	private final static int _dgLength = 50;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	
	public ClientUDP() throws IOException {
		dgSocket = new DatagramSocket();
	}

	private Message receive() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return Message.from(new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength()));
	}
	
	private void send(Message m, InetAddress address, int port) throws IOException {
		byte[] buffer = m.toString().getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		dgSocket.send(dgPacket);
	}

	public static void main(String[] args) throws IOException {
		ClientUDP client = new ClientUDP();
		String host = args[0];
		Integer port = Integer.parseInt(args[1]);
		Scanner sc = new Scanner(System.in);
		Message msgReceived;
		String action;
		
		System.out.println("Username :");
		String username = sc.next();
		
		do {
			// Le client tente de rejoindre une partie
			client.send(new Message(Status.JOIN, username), InetAddress.getByName(host), port);
			msgReceived = client.receive();
		} while (msgReceived.getStatus() == Status.WAIT);
		
		// Le serveur est prêt à recevoir l'action du client
		if (msgReceived.getStatus() == Status.READY) {
			while (true) {
				// On boucle tant que l'action n'est pas correcte
				do {					
					System.out.println("Action : (ROCK | PAPER | SCISSORS | QUIT)");
					action = sc.next().toUpperCase();
				} while (!action.equalsIgnoreCase("QUIT") && (!Action.isAction(action) || Action.isReset(action)));
				
				if (action.equals("QUIT")) {
					client.send(new Message(Status.QUIT, username, action), InetAddress.getByName(host), port);
					System.out.println(client.receive());
					System.exit(0);
				}
				
				// On attend que le serveur ait traité la demande du second joueur
				System.out.println("En attente du joueur suivant");
				
				do {
					client.send(new Message(Status.DO, username, action), InetAddress.getByName(host), port);
					msgReceived = client.receive();
					
					if (msgReceived.getStatus() == Status.ERROR) {
						System.out.println("Erreur: " + msgReceived.getData());
						System.exit(1);
					}
					
				} while (!(msgReceived.getStatus() == Status.END_ROUND || msgReceived.getStatus() == Status.END_GAME));
				
				if (msgReceived.getStatus() == Status.END_GAME) {
					System.out.println("Résultat de la partie: " +  msgReceived.getData());
					break;
				}
				
				if (msgReceived.getStatus() == Status.END_ROUND) {
					System.out.println("Résultat de la manche:  " + msgReceived.getData());
					client.send(new Message(Status.DO, username, "RESET"), InetAddress.getByName(host), port);
					client.receive();
				}
			}
			
		}
		sc.close();
	}
}

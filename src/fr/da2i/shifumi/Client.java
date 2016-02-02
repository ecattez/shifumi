package fr.da2i.shifumi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import fr.da2i.shifumi.Message.Status;

public class Client {
	
	private final static int _dgLength = 50;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	
	public Client() throws IOException {
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
		Client client = new Client();
		String host = args[0];
		Integer port = Integer.parseInt(args[1]);
		Scanner sc = new Scanner(System.in);
		Message msgReceived;
		String action;
		
		System.out.println("Username :");
		String username = sc.next();
		
		//Le client tente de rejoindre une partie
		client.send(new Message(Status.JOIN, username), InetAddress.getByName(host), port);
		msgReceived = client.receive();
		
		System.out.println(msgReceived);
		
		if (msgReceived.getStatus() == Status.WAIT) {
			System.out.println("En attente d'autres joueurs ...");
		}
		//Si on reçoit comme premier message READY
		else if (msgReceived.getStatus() == Status.READY) {
			//Tant qu'on ne recoit pas l'ordre de s'arrêter, on boucle
			while (!(msgReceived.getStatus() == Status.END_GAME || msgReceived.getStatus() == Status.STOP)) {
				//On reçoit une erreur
				if (msgReceived.getStatus() == Status.ERROR) {
					System.out.println("Une erreur s'est produite: " + msgReceived.getData());
				}
				//On reçoit un Wait, on considère que le joueur suivant n'a pas fini son action
				if (msgReceived.getStatus() == Status.WAIT) {
					System.out.println("En attente du joueur suivant");
				}
				//Si tout se passe bien, on joue
				//else {
					if (msgReceived.getStatus() == Status.END_ROUND) {
						System.out.println("Fin de la manche, Vainqueur: " + msgReceived.getData());
					}
					System.out.println("Action : (ROCK | PAPER | SCISSORS)");
					action = sc.next();
					client.send(new Message(Status.DO, username, action), InetAddress.getByName(host), port);
				//}
				msgReceived = client.receive();
			}
			System.out.println("Fin de la partie, Vainqueur :" + msgReceived.getData());
		}
		sc.close();
		//client.send(message.toString(), InetAddress.getByName(host), port);
	}
}

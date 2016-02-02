package fr.da2i.shifumi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import fr.da2i.shifumi.Message.Status;

public class ServeurUDP {
	
	private final static int _dgLength = 50;
	
	private List<String> players;
	
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	
	public ServeurUDP(int _udpPort) throws IOException {
		dgSocket = new DatagramSocket(_udpPort);
		players = new ArrayList<>();
	}

	private void go() throws IOException {
		Message toSend = new Message();
		InetAddress address;
		int port;
		while (true) {
			// Attente de réception d'un datagramme
			String received = receive();
			// 	Récupération de l'adresse et du port du client
			address = dgPacket.getAddress();
			port = dgPacket.getPort();
			
			Message msg = Message.from(received);
			Status status = msg.getStatus();
			
			if (status == Status.UNDEF) {
				toSend.setStatus(Status.ERROR);
				toSend.setData("Bad request");
			}
			else {
				if (!players.contains(msg.getData())) {
					toSend.setStatus(Status.ERROR);
					toSend.setData("Not in game");
				}
				else if (status == Status.JOIN) {
					
				}
				else if (status == Status.QUIT) {
					
				}
				else if (status == Status.DO) {
					
				}
			}
			
			send(msg, address, port);
		}
	}

	private String receive() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
	}
	
	private void send(Message msg, InetAddress address, int port) throws IOException {
		send(msg.toString(), address, port);
	}
	
	private void send(String msg, InetAddress address, int port) throws IOException {
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		dgSocket.send(dgPacket);
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		ServeurUDP serveur = new ServeurUDP(Integer.parseInt(args[0]));
		serveur.go();
	}

}

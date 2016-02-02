package fr.da2i.shifumi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.da2i.shifumi.Message.Status;

public class ServeurUDP {
	
	private final static int _dgLength = 50;
	
	private List<String> players;
	private Map<String, Action> actions;
	private int round;
	private int roundMax;
	private int playerMax;
	private int winnerIdx;
	
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	
	public ServeurUDP(int _udpPort) throws IOException {
		dgSocket = new DatagramSocket(_udpPort);
		players = new ArrayList<>();
		actions = new HashMap<>();
		round = 1;
		roundMax = 3;
		playerMax = 2;
		winnerIdx = -1;
	}

	private void go() throws IOException {
		
		InetAddress address;
		int port;
		while (true) {
			// Réception du message
			Message msg = receive();
			// 	Récupération de l'adresse et du port du client
			address = dgPacket.getAddress();
			port = dgPacket.getPort();
			send(buildMessageFrom(msg), address, port);
		}
	}
	
	private Message buildMessageFrom(Message msg) throws IOException {
		Message toSend = new Message();
		Status status = msg.getStatus();
		String data = msg.getData();
		String option = msg.getOption();
		
		if (status == Status.UNDEF) {
			toSend.setStatus(Status.ERROR);
			toSend.setData("Bad request");
		}
		else if (round > roundMax || winnerIdx >= 0) {
			toSend.setStatus(Status.END_GAME);
			if (winnerIdx >= 0 && winnerIdx < players.size()) {
				toSend.setData(players.get(winnerIdx));
			}
		}
		else if (status == Status.JOIN) {
			if (players.size() == playerMax) {
				if (players.contains(data)) {
					toSend.setStatus(Status.READY);
					toSend.setData("Let's play a game");
				}
				else {
					toSend.setStatus(Status.ERROR);
					toSend.setData("Server is full");
				}
			}
			else if (players.size() == playerMax - 1) {
				players.add(data);
				toSend.setStatus(Status.READY);
				toSend.setData("Let's play a game");
			}
			else {
				players.add(data);
				toSend.setStatus(Status.WAIT);
				toSend.setData("Waiting for one more player");
			}
		}
		else if (!players.contains(data)) {
			toSend.setStatus(Status.ERROR);
			toSend.setData("Not in game");
		}
		else if (status == Status.QUIT) {
			int looser = players.indexOf(data);
			winnerIdx = (looser == 0) ? 1 : 0;
			toSend.setStatus(Status.END_GAME);
			toSend.setData(players.get(winnerIdx));
		}
		else if (status == Status.DO) {
			if (actions.containsKey(data)) {
				toSend.setStatus(Status.WAIT);
				toSend.setData("Waiting for other player action");
			}
			else if (option.equals("ROCK") || option.equals("PAPER") || option.equals("SCISSORS")) {
				Action action = Action.valueOf(option);
				actions.put(data, action);
				if (actions.size() == playerMax) {
					for (Map.Entry<String, Action> entry : actions.entrySet()) {
						String otherName = entry.getKey();
						if (!otherName.equals(data)) {
							Action otherAction = entry.getValue();
							if (action.winAgainst(otherAction)) {
								toSend.setData(data);
							}
							else if (otherAction.winAgainst(action)) {
								toSend.setData(otherName);
							}
							if (action != otherAction) {
								round++;
							}
							break;
						}
					}
					actions.clear();
					if (round > roundMax) {
						toSend.setStatus(Status.END_GAME);
					}
					else {
						toSend.setStatus(Status.END_ROUND);
					}
				}
				else {
					toSend.setStatus(Status.WAIT);
					toSend.setData("Waiting for one more player");
				}
			}
			else {
				toSend.setStatus(Status.ERROR);
				toSend.setData("Bad request");
			}
		}
		return toSend;
	}

	private Message receive() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return Message.from(new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength()));
	}
	
	private void send(Message msg, InetAddress address, int port) throws IOException {
		System.out.println(msg);
		byte[] buffer = msg.toString().getBytes();
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

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
	private Map<String, Integer> scores;
	private int round;
	private int roundMax;
	private int playerMax;
	
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	
	private Status currentStatus;
	
	public ServeurUDP(int _udpPort) throws IOException {
		dgSocket = new DatagramSocket(_udpPort);
		players = new ArrayList<>();
		actions = new HashMap<>();
		scores = new HashMap<>();
		round = 1;
		roundMax = 3;
		playerMax = 2;
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
	
	/* Mauvaise requête */
	private Message badRequest(Message toSend) {
		toSend.setStatus(Status.ERROR);
		toSend.setData("Bad request");
		return toSend;
	}
	
	/* Le serveur est complet */
	private Message serverFull(Message toSend) {
		toSend.setStatus(Status.ERROR);
		toSend.setData("Server is full");
		return toSend;
	}
	
	/* Le client n'est pas en jeu */
	private Message notInGame(Message toSend) {
		toSend.setStatus(Status.ERROR);
		toSend.setData("not in game");
		return toSend;
	}
	
	/* Le serveur est prêt pour le jeu */
	private Message ready(Message toSend) {
		toSend.setStatus(Status.READY);
		toSend.setData("I'm ready");
		return toSend;
	}
	
	/* Le serveur attend des demandes */
	private Message wait(Message toSend) {
		toSend.setStatus(Status.WAIT);
		toSend.setData("I'm waiting");
		return toSend;
	}
	
	/* Attente en fin du round */
	private Message waitRound(Message toSend) {
		currentStatus = Status.END_ROUND;
		toSend.setStatus(Status.END_ROUND);
		toSend.setData(scores.toString());
		return toSend;
	}
	
	/* Round suivant */
	private Message nextRound(Message toSend) {
		currentStatus = Status.READY;
		toSend.setStatus(Status.READY);
		toSend.setData("Next round");
		return toSend;
	}
	
	/* Fin du jeu */
	private Message endGame(Message toSend) {
		currentStatus = Status.END_GAME;
		toSend.setStatus(Status.END_GAME);
		toSend.setData(scores.toString());
		return toSend;
	}
	
	/* Construction du message à renvoyer au client */
	private Message buildMessageFrom(Message msg) throws IOException {
		Message toSend = new Message();
		Status status = msg.getStatus();
		String data = msg.getData();
		String option = msg.getOption();
		
		// Le message est incorrect
		if (status == Status.UNDEF) {
			return badRequest(toSend);
		}
		
		// La partie est finie
		if (round > roundMax || currentStatus == Status.END_GAME) {
			return endGame(toSend);
		}
		
		// Le client tente de joindre la partie
		if (status == Status.JOIN) {
			if (players.size() == playerMax) {
				return (players.contains(data)) ? ready(toSend) : serverFull(toSend);
			}
			if (!players.contains(data)) {
				players.add(data);
				scores.put(data, 0);
				return (players.size() == playerMax) ? ready(toSend) : wait(toSend);
			}
			return wait(toSend);
		}
		
		// Les prochaines étapes nécessitent que le client est déjà joint la partie au préalable
		if (!players.contains(data)) {
			return notInGame(toSend);
		}
		
		// Le client abandonne la partie
		if (status == Status.QUIT) {
			return endGame(toSend);
		}
		
		// Le client effectue une action
		if (status == Status.DO) {
			if (!Action.isAction(option)) {
				return badRequest(toSend);
			}
			if (currentStatus == Status.END_ROUND) {
				actions.remove(data);
				return (actions.size() == 0) ? nextRound(toSend) : waitRound(toSend);
			}
			
			Action action;
			if (actions.containsKey(data)) {
				action = actions.get(data);
			}
			else if (!Action.isReset(option)) {
				action = Action.valueOf(option);
				actions.put(data, action);
				if (actions.size() == playerMax) {
					for (Map.Entry<String, Action> entry : actions.entrySet()) {
						String otherName = entry.getKey();
						if (!otherName.equals(data)) {
							Action otherAction = entry.getValue();
							if (action.winAgainst(otherAction)) {
								scores.put(data, scores.get(data) + 1);
								toSend.setData(data);
							}
							else if (otherAction.winAgainst(action)) {
								scores.put(otherName, scores.get(otherName) + 1);
								toSend.setData(otherName);
							}
							if (action != otherAction) {
								round++;
							}
							break;
						}
					}
					return (round > roundMax) ? endGame(toSend) : waitRound(toSend);
				}
			}
		}		
		return wait(toSend);
	}

	private Message receive() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return Message.from(new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength()));
	}
	
	private void send(Message msg, InetAddress address, int port) throws IOException {
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

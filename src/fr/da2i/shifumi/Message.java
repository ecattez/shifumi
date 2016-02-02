package fr.da2i.shifumi;

public class Message {
	
	public enum Status {
		JOIN, QUIT, DO, WAIT, READY, END_ROUND, END_GAME, STOP, ERROR;
	}
	
	private String status;
	private String data;
	private String option;
	
	public Message(Status status, String data, String option) {
		this(status.toString(), data, option);
	}
	
	public Message(Status status, String data) {
		this(status, data, null);
	}
	
	public Message(String status, String data, String option) {
		this.status = status;
		this.data = data;
		this.option = option;
	}
	
	public Message(String status, String data) {
		this(status, data, null);
	}
	
	public Message() {}
	
	@Override
	public String toString() {
		String str = status;
		if (data != null) {
			str += ":" + data;
		}
		if (option != null) {
			str += ":" + option;
		}
		return str;
	}

}

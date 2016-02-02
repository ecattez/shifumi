package fr.da2i.shifumi;

public class Message {
	
	private String status;
	private String data;
	private String option;
	
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

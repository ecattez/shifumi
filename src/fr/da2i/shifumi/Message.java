package fr.da2i.shifumi;

public class Message {
	
	public enum Status {
		UNDEF, JOIN, QUIT, DO, WAIT, READY, END_ROUND, END_GAME, STOP, ERROR;
	}
	
	private Status status;
	private String data;
	private String option;
	
	public Message(Status status, String data, String option) {
		this.status = status;
		this.data = data;
		this.option = option;
	}
	
	public Message(Status status, String data) {
		this(status, data, null);
	}
	
	public Message() {}
	
	public Status getStatus() {
		if (status == null) {
			status = Status.UNDEF;
		}
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	@Override
	public String toString() {
		String str = getStatus().toString();
		if (data != null) {
			str += ":" + data;
		}
		if (option != null) {
			str += ":" + option;
		}
		return str;
	}
	
	public static Message from(String str) {
		Message msg = new Message();
		if (str.contains(":")) {
			String[] sp = str.split(":");
			try {
				if (sp.length >= 1) {
					msg.setStatus(Status.valueOf(sp[0]));
				}
				if (sp.length >= 2) {
					msg.setData(sp[1]);
				}
				if (sp.length == 3) {
					msg.setOption(sp[2]);
				}
			} catch (Exception e) {}
		}
		return msg;
	}

}

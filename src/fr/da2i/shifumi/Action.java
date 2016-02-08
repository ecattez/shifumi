package fr.da2i.shifumi;

public enum Action {
	
	ROCK {
		@Override
		public boolean winAgainst(Action a) {
			return a == SCISSORS;
		}
	},
	PAPER {
		@Override
		public boolean winAgainst(Action a) {
			return a == ROCK;
		}
	},
	SCISSORS {
		@Override
		public boolean winAgainst(Action a) {
			return a == PAPER;
		}
	},
	RESET {
		@Override
		public boolean winAgainst(Action a) {
			return false;
		}
	};
	
	public abstract boolean winAgainst(Action a);
	
	public static boolean isAction(String str) {
		try {
			Action.valueOf(str.toUpperCase());
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	public static boolean isReset(String str) {
		return str.toUpperCase().equals("RESET");
	}

}

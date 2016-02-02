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
	};
	
	public abstract boolean winAgainst(Action a);

}

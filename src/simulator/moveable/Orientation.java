package simulator.moveable;

public enum Orientation {
	NORTH {
		@Override
		public Orientation reverse() {
			return SOUTH;
		}
	}, 
	EAST {
		@Override
		public Orientation reverse() {
			return WEST;
		}
	}, 
	SOUTH {
		@Override
		public Orientation reverse() {
			return NORTH;
		}
	}, WEST {
		@Override
		public Orientation reverse() {
			return EAST;
		}
	};
	
	public abstract Orientation reverse();
}
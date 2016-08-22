package simulator.intersection;

import java.awt.Color;

public enum IntersectionStatus {
	GO {
		@Override
		public boolean navigable() {
			return true;
		}

		@Override
		public  IntersectionStatus nextStatus() {
			return CAUTION;
		}

		@Override
		public Color color() {
			return Color.GREEN;
		}
	}, 
	CAUTION {
		@Override
		public boolean navigable() {
			return true;
		}

		@Override
		public IntersectionStatus nextStatus() {
			return STOP;
		}

		@Override
		public Color color() {
			return Color.YELLOW;
		}
	}, 
	STOP {
		@Override
		public boolean navigable() {
			return false;
		}

		@Override
		public IntersectionStatus nextStatus() {
			return GO;
		}

		@Override
		public Color color() {
			return Color.RED;
		}
	};
	
	public abstract boolean navigable();
	public abstract IntersectionStatus nextStatus();
	public abstract Color color();
}
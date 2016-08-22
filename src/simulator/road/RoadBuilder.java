package simulator.road;

import java.util.ArrayList;
import java.util.Random;
import simulator.intersection.Intersection;
import simulator.moveable.Orientation;

public enum RoadBuilder {
	BUILDER {
		@Override
		public RoadBuilder addIntersection(Intersection intersection) {
			if (intersection == null)
				throw new NullPointerException ("Cannot add a null intersection");
			
			_listOfIntersections.add(intersection);
			//System.out.println("Added : " +   intersection);
			//System.out.println("Size ==" + _listOfIntersections.size());
			return this;
		}
		
		private void reset() {
			_listOfIntersections.clear();;
			_orientation = null;
			_segmentLength = 0;
			_scaleLength = 0;
		}

		@Override
		public Road build() {
			RoadObj newRoad;
			
			if (_orientation !=  null) {
				
				if (_segmentLength == 0) {
					this.setLength(MIN_LENGTH, MAX_LENGTH);
				} 
				
				if (_scaleLength == 0)
					_scaleLength = _segmentLength;
				
				newRoad = new RoadObj(_orientation, _segmentLength, _scaleLength);
				
				for (Intersection i: _listOfIntersections){
					newRoad.addIntersection(i);
				}
					 
			} else {
				throw new IllegalStateException ("Cannot build an incomplete road");
			}
			
			this.reset();
			
			return newRoad;
		}

		@Override
		public RoadBuilder setOrientation(Orientation o) {
			_orientation = o;
			return this;
		}

		@Override
		public RoadBuilder setLength(double min, double max) {
			if (min<=0 || min > max)
				throw new IllegalArgumentException ();
			
			_segmentLength = min + (max-min) * (new Random()).nextDouble();
			return this;
		}

		@Override
		public RoadBuilder setScaleLength(double length) {
			_scaleLength = length;
			return this;
		}
	};
	
	private static ArrayList<Intersection> _listOfIntersections = new ArrayList<>();
	private static Orientation _orientation;
	private static double _segmentLength;
	private static double _scaleLength;
	
	/* default seeding constant */
	public static final double MIN_LENGTH = 200;
	public static final double MAX_LENGTH = 500;

	public abstract RoadBuilder setOrientation (Orientation o);
	public abstract RoadBuilder addIntersection (Intersection intersection);
	public abstract Road build();
	public abstract RoadBuilder setLength(double min, double max);
	public abstract RoadBuilder setScaleLength (double length);
}
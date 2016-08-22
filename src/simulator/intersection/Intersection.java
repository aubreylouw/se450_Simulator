package simulator.intersection;

import simulator.agent.TimeAgent;
import simulator.moveable.Moveable;
import simulator.moveable.Orientation;
import simulator.road.Road;

public interface Intersection extends TimeAgent {
	public IntersectionStatus statusAlongOrientation (Orientation orientation);
	public double frontPositionAlongOrientation (Orientation orientation);
	public double rearPositionAlongOrientation (Orientation orientation);
	public void intersectAlongOrientation (Road road, double startingPosition);
	public void intersectAlongOrientation (Moveable mobile);
	public double lengthAlongOrientation (Orientation orientation);
}
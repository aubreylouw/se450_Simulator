package simulator.road;

import java.util.Iterator;

import simulator.agent.TimeAgent;
import simulator.intersection.Intersection;
import simulator.moveable.Moveable;
import simulator.moveable.Orientation;

public interface Road extends TimeAgent {
	public double getLength();
	public Orientation orientation();
	public void addMoveable (Moveable mobile);
	public double getClosestOccupiedPosition (Moveable mobile);
	public Iterator<? extends Intersection> getRemainingIntersectionsIterator (Moveable mobile);
	public Iterator<? extends Intersection> getAllIntersectionsIterator();
	public Iterator<Moveable> getAllMoveablesIterator();
}
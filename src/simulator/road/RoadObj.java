package simulator.road;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import simulator.intersection.Intersection;
import simulator.moveable.Moveable;
import simulator.moveable.Orientation;

/*
 * A road is an agent of timeserver. It has a source that emits moveables, 
 * and a sink that recycles old moveables. 
 * 
 * Moveables are managed as FIFO objects. New moveables may not be added to the
 * road proper until there is room enough on the road. 
 *  
 * The road can be intersected by zero, one, or more intersections. 
 */
public class RoadObj implements Road {
	/*  
	 * Attributes to manage the granularity of motion
	 * 
	 */
	// the ideal road length to which this road should scale
	private final double _scaleFactor;
	private final double DEFAULT_TEMPO = 1;
	private final double _segmentLength;
	private final Orientation _orientation;
	private final LinkedList<Intersection> _intersections;
	private final LinkedList<Moveable> _currentMoveables;
	
	RoadObj (Orientation orientation, double segmentLength, double scaleLength) {
		this._intersections = new LinkedList<>();
		this._currentMoveables = new LinkedList<>();
		this._orientation = orientation;
		this._segmentLength = segmentLength;
		this._scaleFactor = this._segmentLength / scaleLength;
	}
	
	/*
	 * for testing
	 */
	public double getLength() {
		
		double totalLength = this._segmentLength;
		
		for (Intersection i : this._intersections) {
			totalLength += i.rearPositionAlongOrientation(this._orientation) - i.frontPositionAlongOrientation(this._orientation);
			totalLength += this._segmentLength;
		}
			
		return totalLength;
	}
	
	public Orientation orientation() {
		return this._orientation;
	}
	
	/*
	 * External clients can only add new moveables to the new list. When the road 
	 * is updated by its observable, it will update all current moveables, 
	 * and then evaluate new moveables for membership to current
	 * 
	 * @invariant mobile not null
	 */
 	public void addMoveable (Moveable mobile) {
 		if (mobile == null)
			throw new NullPointerException ("Mobile argument cannot be null");
		this._currentMoveables.add(mobile);
	}
	
 	/*
 	 * During road construction, an external client can add intersections.
 	 * 
 	 * @return the position at which the intersection was added
 	 * 
 	 * @invariant an intersection can only be added once
 	 * @invariant intersection cannot be null
 	 * @precondition once moveables have been emitted to the road, no new intersections can be added
 	 */
	void addIntersection (Intersection intersection) {
		if (intersection == null)
			throw new NullPointerException ("Intersection argument cannot be null");
		if  (!this._currentMoveables.isEmpty())
			throw new IllegalStateException("Road already initialized  - cannot add more intersections");
		if (this._intersections.contains(intersection))
			throw new IllegalArgumentException("One intersection cannot span a road more than once");
		
		double startingPosition = this.getLength();
		//System.out.println("Road has current length = " + startingPosition);
		
		//System.out.print("Road adding: " +  intersection);
		//System.out.println(" @ : " +  startingPosition);
		
		intersection.intersectAlongOrientation(this, startingPosition);
		
		this._intersections.add(intersection);
	}

	/*
	 * Processes moveables in the current collection; once processed through the road,
	 * they are recycled to the old collection. New moveables that can enter the road are 
	 * pushed to the new collection.
	 * 
	 * @see simulator.agent.Agent#run()
	 */
	@Override
	public void run (double tempoFactor) {		
		
		double speedFactor = tempoFactor*DEFAULT_TEMPO * this._scaleFactor; 
		//System.out.println("Road running...");
		
		// no need to continue if no moveables are queued up for processing
		if (this._currentMoveables.isEmpty()) return;
		
		// remove moveables that have moved off the road; starting with head
		BigDecimal roadLength = BigDecimal.valueOf(this.getLength());
		Iterator<Moveable> iter = this._currentMoveables.iterator();
		while (iter.hasNext()) {
			Moveable m = iter.next();
			if (m.currentFrontPosition().compareTo(roadLength) >= 0) {
				iter.remove();
			}
		}		
		
		
		// process the existing moveables; since only the current list of moveables 
		// are used for determining whether (and how far) a moveable can move,
		// this is essentially a list of moveables on the road right now
		for (Moveable m : this._currentMoveables) {
			//System.out.println("MOVING CURR:: " + m.toString());
			m.move(speedFactor);
			//System.out.println(m);
		}
	}
	
	/*
	 * Calculates the closest occupied position in the car's traveling orientation.
	 * 
	 * @invariant mobile is not null
	 * @invariant mobile is a member of current or new mobile collection
	 */
	public double getClosestOccupiedPosition(Moveable mobile) {
		if (mobile == null)
			throw new NullPointerException ("Mobile argument cannot be null");
		if (!this._currentMoveables.contains(mobile))
			throw new IllegalArgumentException ("Mobile argument is invalid");
		
		//System.out.println("        ROAD ASKED FOR POSITION - START:: "+mobile);
		
		// initialize variables for tracking computation
		double closestOccupiedPosition = -1;
		
		// the current moveable's front-most position
		//double currentMoveablePosition = mobile.currentFrontPosition().doubleValue();
		
		if (this._currentMoveables.contains(mobile)) {
			closestOccupiedPosition = this.getLength() * 2;
			for (Moveable m: this._currentMoveables) {
				if (mobile.equals(m)) {
	//				System.out.println("            mobile located @ index " + this._currentMoveables.indexOf(m) + 
	//						" ...previous @ " + closestOccupiedPosition);
					break;
				}
				
				closestOccupiedPosition = m.currentRearPosition().doubleValue();
				if (closestOccupiedPosition <= 0) closestOccupiedPosition = 0;
			}
		} else {
			// should not be reached
			System.out.println("problem in closest occupied!");
		}
		
		//System.out.println("        ROAD ASKED FOR POSITION - END:: "+ closestOccupiedPosition);
		
		return closestOccupiedPosition;
	}

	public Iterator<Intersection> getRemainingIntersectionsIterator (Moveable mobile) {
		ArrayList<Intersection> remainingIntersections = new ArrayList<>();
		for (Intersection e: this._intersections) {
			if (e.rearPositionAlongOrientation(mobile.currentOrientation()) >= mobile.currentFrontPosition().doubleValue())
				remainingIntersections.add(e);
		}
		
		return remainingIntersections.isEmpty() ? Collections.emptyIterator() :
			remainingIntersections.iterator();
	}

	@Override
	public Iterator<Intersection> getAllIntersectionsIterator() {
		ArrayList<Intersection> allIntersections = new ArrayList<>();
		allIntersections.addAll(this._intersections);
		
		return allIntersections.isEmpty() ? Collections.emptyIterator() :
			allIntersections.iterator();
	}

	@Override
	public Iterator<Moveable> getAllMoveablesIterator() {
		LinkedList<Moveable> moveables = new LinkedList<>();
		moveables.addAll(this._currentMoveables);
		
		return moveables.isEmpty() ? Collections.emptyIterator() :
			moveables.iterator();
	}
	
	public String toString() {
		StringBuilder thisRoad =  new StringBuilder();
		thisRoad.append("ROAD: length: ").append(this.getLength()).append(" ");
		thisRoad.append("orientation: ").append(this._orientation).append(" ");
		
		if (!this._intersections.isEmpty()) thisRoad.append(" intersected @");
		
		for (Intersection i: this._intersections) {
			thisRoad.append(i.frontPositionAlongOrientation(this._orientation)).append(" ");
		}
		
		thisRoad.append("\n");
		
		return thisRoad.toString();
	}

	@Override
	public String state() {
		StringBuilder thisRoad = new StringBuilder().append(this.toString());
		for (Moveable m: this._currentMoveables) {
			thisRoad.append("CAR: ").append(m).append("\n");
		}
			
		return thisRoad.toString();
	}
}
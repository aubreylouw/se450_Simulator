package simulator.moveable;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Random;

import simulator.intersection.Intersection;
import simulator.intersection.IntersectionStatus;
import simulator.road.Road;

final class Car implements Moveable {
	/*
	 * immutable members
	 */
	/** @invariant greater than 0 */
	private final BigDecimal _maxVelocity;
	/** @invariant greater than 0*/
	private final BigDecimal _brakeDistance;
	/** @invariant greater than 0*/
	private final BigDecimal _stopDistance;
	/** @invariant greater than 0 */
	private final BigDecimal _length;
	/** @invariant not null */
	private final Color _color;
	/* @invariant not null */
	private final Road _road;
	/* enumeration */
	private final Orientation _orientation;
	/* random generation */
	private final Random _random = new Random();
	
	/*
	 * mutable members
	 */
	/** @invariant not negative */
	private double _rearPosition;
	/** @invariant not negative */
	private double _frontPosition;
	private MoveableStatus _status;
	
	/*
	 * Constructs a car object with a specific Maximum Velocity, Brake Distance,
	 * Stop Distance, Length, Color, Orientation. Each car starts life on one road.
	 * 
	 */
	protected Car (BigDecimal maxVelocity, BigDecimal brakeDistance, BigDecimal stopDistance, BigDecimal length, 
			Road road) {
		
		// check that constructor arguments with object references are not null
		if (road == null)
			throw new NullPointerException ("Car's Road argument cannnot be null");
		
		this._maxVelocity = maxVelocity;
		this._brakeDistance = brakeDistance;
		this._stopDistance = stopDistance;
		this._length =  length;
		this._rearPosition = 0;
		this._frontPosition = 0;
		this._orientation = road.orientation();
		this._road = road;
		
		// color generation @http://stackoverflow.com/questions/4246351/creating-random-colour-in-java
		float hue = this._random.nextFloat();
		float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
		float luminance = 1.0f; //1.0 for brighter, 0.0 for black
		Color randomColor = Color.getHSBColor(hue, saturation, luminance);
		
		// do not create cars with the same color as lights
		while (randomColor.equals(Color.RED) ||
				randomColor.equals(Color.YELLOW) ||
				randomColor.equals(Color.GREEN) ) {
			hue = this._random.nextFloat();
			randomColor = Color.getHSBColor(hue, saturation, luminance);
		}
		
		this._color = Color.getHSBColor(hue, saturation, luminance);
		
		this._status = MoveableStatus.STOP;		
	}

	@Override
	public BigDecimal currentRearPosition() {
		return BigDecimal.valueOf(this._rearPosition);
	}
	
	@Override
	public BigDecimal currentFrontPosition() {
		return BigDecimal.valueOf(this._frontPosition);
	}

	@Override
	public BigDecimal maxVelocity() {
		return this._maxVelocity;
	}

	@Override
	public BigDecimal brakeDistance() {
		return this._brakeDistance;
	}

	@Override
	public BigDecimal stopDistance() {
		return this._stopDistance;
	}
	
	@Override
	public BigDecimal length() {
		return this._length;
	}
	
	@Override
	public Color color() {
		return this._color;
	}
	
	public BigDecimal computeStatus(BigDecimal occupiedPosition) {
		return BigDecimal.ZERO;
	}
	
	/*
	 * Prompts the moveable to attempt forward motion. Since a moveable 
	 * can only move if there are no obstacles in its path, it asks the road
	 * for the furthest forward position that is occupied.
	 * 
	 * @see simulator.moveable.Moveable#move()
	 */
	public MoveableStatus move(double speedGovernor) {
		BigDecimal governedMaxSpeed = this._maxVelocity.multiply(BigDecimal.valueOf(speedGovernor));
		//System.out.println("********************************");
		//System.out.println("    Move() started :" + this);
		//System.out.println("Road report: max v = " + this._maxVelocity + " governed = " + governedMaxSpeed);
		
		// ask the road for the position of the next obstacle (could be anything)
		BigDecimal nextOccupiedPosition = BigDecimal.valueOf(this._road.getClosestOccupiedPosition(this));
		BigDecimal frontPosition = BigDecimal.valueOf(this._frontPosition);
		
		// distance between nose of car and the next occupied position
		BigDecimal unoccupiedDistance = nextOccupiedPosition.subtract(frontPosition);
		/*
		System.out.println("Road report: "
				+ "length = " + this._road.getLength()
				+ " occcupied = " + nextOccupiedPosition + " free space = " + unoccupiedDistance);
		*/
		
		// the computed best forward jump based on an evaluation of obstacles
		// pessimistic default
		BigDecimal bestAvailableFreePosition = frontPosition;
		
		// if the next obstacle is within stopping range, avoid a collision by not moving forward
		boolean stopToAvoidCollision = unoccupiedDistance.compareTo(this._stopDistance) <= 0;
		//System.out.println("Road report: stop = " + this._stopDistance + " ct? " +
		//		unoccupiedDistance.compareTo(this._stopDistance));
		
		// if the next obstacle is within braking range, slow down
		boolean slowDownToAvoidCollision = unoccupiedDistance.compareTo(this._brakeDistance) <=0;
		//System.out.println("Road report: brake = " + this._brakeDistance + " ct? " +
		//		unoccupiedDistance.compareTo(this._brakeDistance));
		
		// if the next obstacle is greater than max velocity, the car can accelerate
		boolean canAccelerateToMaxVelocity = unoccupiedDistance.compareTo(governedMaxSpeed) > 0;
		//System.out.println("Road report: acc = " + governedMaxSpeed + " ct? " +
		//		unoccupiedDistance.compareTo(governedMaxSpeed));
		
		if (stopToAvoidCollision) {
			//System.out.println("                Stopping @ " + bestAvailableFreePosition);
			return MoveableStatus.STOP;
		} else if (canAccelerateToMaxVelocity) {
			bestAvailableFreePosition = bestAvailableFreePosition.add(governedMaxSpeed);
			//System.out.println("                Accelerating @ " + bestAvailableFreePosition);
			this._status = MoveableStatus.ACCELERATE;
		} else if (slowDownToAvoidCollision) {
			bestAvailableFreePosition = bestAvailableFreePosition.add(governedMaxSpeed.min(unoccupiedDistance.divide(BigDecimal.valueOf(2))));
			//System.out.println("                Braking @ " + bestAvailableFreePosition);
			this._status = MoveableStatus.BRAKE;
		} else {
			bestAvailableFreePosition = bestAvailableFreePosition.add(governedMaxSpeed.min(unoccupiedDistance.divide(BigDecimal.valueOf(2))));
			//System.out.println("                Slowing @ " + bestAvailableFreePosition);
			this._status = MoveableStatus.SLOWDOWN;
		}
		
		// probe the intersections forward of the front position to determine if they are navigable
		Iterator<? extends Intersection> sortedIter = this._road.getRemainingIntersectionsIterator(this);
		while (sortedIter.hasNext()) {
			Intersection currIntersection = sortedIter.next();
			
			BigDecimal intersectionFrontPosition = BigDecimal.valueOf(currIntersection.frontPositionAlongOrientation(this.currentOrientation()));
			BigDecimal intersectionRearPosition = BigDecimal.valueOf(currIntersection.rearPositionAlongOrientation(this._orientation));
			
			IntersectionStatus intersectionLightColor = currIntersection.statusAlongOrientation(this.currentOrientation());
			boolean intersectionNextObstacle = intersectionFrontPosition.compareTo(bestAvailableFreePosition) < 0;
			
			boolean intersectionBehindCar = intersectionFrontPosition.compareTo(this.currentFrontPosition()) <= 0; 
			
			
			//System.out.println("Road report: {" + intersectionFrontPosition + " , " + intersectionRearPosition + "} " + 
			//		" light is " + intersectionLightColor + " best pos. @ " + bestAvailableFreePosition + " i.n.o? " + 
			//		intersectionNextObstacle + " i.behind.c? "+ intersectionBehindCar);
			
			
			// we are in or past the intersection; stop probing
			if (intersectionBehindCar) break;
			
			// intersection is not the next obstacle; stop probing
			if (!intersectionNextObstacle) break;
			
			// determine correct response based on intersection's status
			//System.out.println("Light is " + intersectionLightColor);
			switch (intersectionLightColor) { 
				case STOP: {
					if (bestAvailableFreePosition.compareTo(intersectionFrontPosition) > 0) {
						this._status = MoveableStatus.STOP;
					}
					bestAvailableFreePosition = bestAvailableFreePosition.min(intersectionFrontPosition);
					break;
				}
				case CAUTION: {
					boolean intersectionWithinBrakingDistance = 
							intersectionFrontPosition.subtract(frontPosition).compareTo(this._brakeDistance) <=0;
					//System.out.println("                    Light = CAUTION @ " + intersectionFrontPosition + " ct? " +
					//		intersectionFrontPosition.subtract(frontPosition).compareTo(this._brakeDistance));
					if (intersectionWithinBrakingDistance) {
						bestAvailableFreePosition = bestAvailableFreePosition.min(intersectionFrontPosition);
						//System.out.println("                    Stopped at " + intersectionFrontPosition);
						this._status = MoveableStatus.BRAKE;
						break;
					} else { 
						//fall through
						//System.out.println("                    Accelerated through " + intersectionFrontPosition);
					}
				}
				case GO: {
					// test if the car will be stuck in the intersection
					//System.out.println("                    Light = GREEN");
					
					if (bestAvailableFreePosition.compareTo(intersectionRearPosition) <= 0) {
						currIntersection.intersectAlongOrientation(this);
						//System.out.println("                    Caught in intersection before " + intersectionRearPosition);
					}
				}
			}
		}

		this.moveForwardToPosition(bestAvailableFreePosition.subtract(frontPosition).doubleValue());		
		//System.out.println("Move() finished :" + this);
		
		return this._status;
	}
	
	public String toString() {
		StringBuilder thisCar = new StringBuilder();
		thisCar.append("Color : ").append(this.color()).append(" ");
		thisCar.append("Status : ").append(this._status).append(" ");
		thisCar.append("Speed : ").append(this._maxVelocity).append(" ");
		thisCar.append("F pos. : ").append(this._frontPosition).append(" ");
		thisCar.append("R pos. : ").append(this._rearPosition).append(" ");
		thisCar.append("Lgth : ").append(this._length).append(" ");
		thisCar.append("B/d. : ").append(this._brakeDistance).append(" ");
		thisCar.append("S/d. : ").append(this._stopDistance).append(" ");
		
		return thisCar.toString();
	}
	
	/*
	 * Moves the car forward by a set increment.
	 */
	protected void moveForwardToPosition (double position) {
		this._frontPosition = this._frontPosition + position;
		this._rearPosition = this._frontPosition -  this._length.doubleValue();
	}

	@Override
	public Orientation currentOrientation() {
		return this._orientation;
	}
}
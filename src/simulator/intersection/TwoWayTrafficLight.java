package simulator.intersection;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import simulator.moveable.Moveable;
import simulator.moveable.Orientation;
import simulator.road.Road;

/*
 * A controller for a two way intersection governed by traffic lights along
 * both orientations (NS and EW).
 * 
 * Each traffic light 
 */
public class TwoWayTrafficLight implements Intersection {

	private final double _length; 
	private final double _yellowDuration;
	private final double _greenDuration;
	private final double _redDuration;
	private final LinkedList<Light> _lightQueue = new LinkedList<> ();
	private final EnumMap<Orientation, Light> _lights = 
			new EnumMap<>(Orientation.class);
	private final EnumMap<Orientation, ArrayList<Moveable>> _mobiles = 
			new EnumMap<>(Orientation.class);
	
	// instantiation parameters
	private final Random _random = new Random();
	public static final double MIN_LENGTH = 10;
	public static final double MAX_LENGTH = 15;
	public static final double MIN_GO_DURATION = 30;
	public static final double MAX_GO_DURATION = 180;
	public static final double MIN_CAUTION_DURATION = 4;
	public static final double MAX_CAUTION_DURATION = 5;
	
	class Light  {
		
		private IntersectionStatus _status;
		private double _timer;
		private final double _rearPosition;
		private final double _frontPosition;
		
		Light (double frontPosition, double rearPosition,IntersectionStatus startingStatus) {
			this._frontPosition = frontPosition;
			this._rearPosition = rearPosition;
			this._status = startingStatus;
			this.initTimers();
		}
		
		private void initTimers() {
			switch (this._status) {
				case GO: {
					this._timer = TwoWayTrafficLight.this._greenDuration;
					break;
				}
				case CAUTION: {
					this._timer = TwoWayTrafficLight.this._yellowDuration;
					break;
				}
				case STOP: {
					this._timer = TwoWayTrafficLight.this._redDuration;
					break;
				}
			}
		}
		
		void update(double tempoFactor) {
			
			this._timer -= tempoFactor;
	//		System.out.println("LIGHT == " + this + " is "+ this._status + " @" + this._timer + " @pos " +  this.frontPosition());
			
			if (this._timer < 1) {
				this._status = this._status.nextStatus();
				this.initTimers();
			}
		}
		
		IntersectionStatus status() {
			return this._status;
		}
	
		double frontPosition() {
			return this._frontPosition;
		}
		
		double rearPosition() {
			return this._rearPosition;
		}

	}
	
	public TwoWayTrafficLight() {
		this._length = MIN_LENGTH  + (MAX_LENGTH - MIN_LENGTH) * this._random.nextDouble();
		this._yellowDuration = MIN_CAUTION_DURATION  + (MAX_CAUTION_DURATION - MIN_CAUTION_DURATION) * this._random.nextDouble();
		this._greenDuration = MIN_GO_DURATION  + (MAX_GO_DURATION - MIN_GO_DURATION) * this._random.nextDouble();
		this._redDuration = this._greenDuration + this._yellowDuration;
	}
	
	/* 
	 * for testing
	 */
	public TwoWayTrafficLight (double length, double greenDuration, double yellowDuration) {
		if (length <0 || greenDuration < 0 || yellowDuration <0)
			throw new IllegalArgumentException ();
		
		if (length == 0) 
			this._length = MIN_LENGTH  + (MAX_LENGTH - MIN_LENGTH) * this._random.nextDouble();
		else this._length = length;
		
		if (greenDuration == 0)
			this._greenDuration = MIN_GO_DURATION  + (MAX_GO_DURATION - MIN_GO_DURATION) * this._random.nextDouble();
		else this._greenDuration = greenDuration;
		
		if (this._yellowDuration == 0)
			this._yellowDuration = MIN_CAUTION_DURATION  + (MAX_CAUTION_DURATION - MIN_CAUTION_DURATION) * this._random.nextDouble();
		else this._yellowDuration = yellowDuration;
		
		this._redDuration = this._greenDuration + this._yellowDuration;
	}
	
	/*
	 * for testing
	 */
	protected double IntersectionStatusDuration (IntersectionStatus status) {
		double duration = 0;
		
		switch (status) {
			case GO: duration = this._greenDuration; break;
			case CAUTION: duration = this._yellowDuration; break;
			case STOP: duration = this._redDuration; break;
		}
		return duration;
	}
	
	/*
	 * for testing
	 */
	protected int numberOfLights() {
		return this._lights.keySet().size();
	}
	
	/*
	 * for testing
	 */
	protected double timerAlongOrientation (Orientation o) {
		return this._lights.get(o)._timer;
	}
	
	/*
	 * for testing
	 */
	protected void addTrafficLight(double frontPosition, double rearPosition, Orientation orientation) {
		if (this._lights.size() == 2) 
			throw new IllegalStateException ("Cannot add more than two lights.");
		if (this._lights.containsKey(orientation))
			throw new IllegalStateException ("Cannot add more than 1 traffic lights for the same intersection");
		
		if (frontPosition < 0 || rearPosition < 0)
			throw new IllegalArgumentException ("Positional arguments must be gt 0");
		
		if (orientation == null)
			throw new NullPointerException ("Light orienatation must be non-null");
		
		IntersectionStatus startingStatus;
	
		//System.out.println(this._lights.size());
		if (this._lights.isEmpty())
			startingStatus = IntersectionStatus.GO;
		else startingStatus = IntersectionStatus.STOP;
		
		Light light = new Light(frontPosition, rearPosition, startingStatus);
		this._lightQueue.add(light);
		this._lights.put(orientation, light);
		this._mobiles.put(orientation, new ArrayList<Moveable>());
	}

	@Override
	public double frontPositionAlongOrientation(Orientation orientation) {
		return this._lights.get(orientation).frontPosition();
	}

	@Override
	public double rearPositionAlongOrientation(Orientation orientation) {
		return this._lights.get(orientation).rearPosition();
	}

	@Override
	public void run( double tempoFactor) {
		for (Orientation o : this._lights.keySet()) {
			//System.out.println("====================");
			this._lights.get(o).update(tempoFactor);
			this.drainIntersection(o);
			//System.out.print("LIGHT @ " + this.frontPositionAlongOrientation(o));
			//System.out.println(" == " + this.statusAlongOrientation(o));
			//System.out.println("====================");
		}
		
	}
	
	private void drainIntersection (Orientation o) {
		Iterator<Moveable> moveableIter = this._mobiles.get(o).iterator();
		while (moveableIter.hasNext()) {
			Moveable m = moveableIter.next();
			if (m.currentFrontPosition().doubleValue() > this.rearPositionAlongOrientation(m.currentOrientation()))
				moveableIter.remove();
		}
	}
	
	@Override
	public IntersectionStatus statusAlongOrientation (Orientation o) {
		
		IntersectionStatus lightStatus = IntersectionStatus.STOP;
		
		if (this._lights.get(o) != null) {
			IntersectionStatus otherLightStatus;
			
			this.drainIntersection(o);
			if (this._mobiles.get(o).isEmpty()) {
				lightStatus = this._lights.get(o)._status;
				
				// check other light - if not switched over, report stopped
				for (Orientation o2: this._lights.keySet()) {
					if (! o2.equals(o2)) {
						otherLightStatus = this._lights.get(o2)._status;
						if (otherLightStatus.navigable() && lightStatus.navigable())
							lightStatus = IntersectionStatus.STOP;
							System.out.println ("Syncing lights to STOP");
					}
				}
			} else System.out.println ("Intersection occupied");	
		} else System.out.println("Oops!");
		
		return lightStatus;
	}

	@Override
	public void intersectAlongOrientation(Road road, double startingPosition) {
		double frontPosition = startingPosition;
		double rearPosition = frontPosition + this._length; 
		
		this.addTrafficLight(frontPosition, rearPosition, road.orientation());
	}

	@Override
	public void intersectAlongOrientation(Moveable mobile) {
		if (mobile == null)
			throw new NullPointerException ("Mobile argument must be not-null");
		Orientation o = mobile.currentOrientation();
		
		if (mobile.currentFrontPosition().doubleValue() <= this.rearPositionAlongOrientation(o) &&
				mobile.currentRearPosition().doubleValue() >= this.frontPositionAlongOrientation(o)) {
			System.out.println("STUCK: " + mobile);
			this._mobiles.get(o).add(mobile);	
		}
	}

	@Override
	public double lengthAlongOrientation(Orientation orientation) {
		return this._length;
	}
	
	@Override
	public String state() {
		StringBuilder thisState = new StringBuilder();
		thisState.append("INTERSECTION: ");
		for (Orientation o : this._lights.keySet()) {
			if (this._lights.get(o) != null) {
				Light l =this._lights.get(o); 
				thisState.append(" Light orientation: ").append(o);
				thisState.append(" { ").append(l.frontPosition()).append(" ").append(l.rearPosition()).append(" } ");
				thisState.append(" status: ").append(l.status());
			}
		}
		return thisState.toString();
	}
}
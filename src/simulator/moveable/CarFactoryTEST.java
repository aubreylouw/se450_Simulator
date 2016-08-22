package simulator.moveable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import junit.framework.TestCase;
import org.junit.Assert;
import simulator.intersection.Intersection;
import simulator.intersection.IntersectionStatus;
import simulator.road.Road;
import simulator.road.RoadBuilder;

public class CarFactoryTEST extends TestCase {
	
	CarFactory cf = CarFactory.RANDOM;
	RoadBuilder rf = RoadBuilder.BUILDER;
	Orientation o = Orientation.SOUTH;
	MockRoad mr = new MockRoad();
	
	class MockIntersection implements Intersection {

		IntersectionStatus _status;
		double _front;
		double _rear;
		
		@Override
		public IntersectionStatus statusAlongOrientation(Orientation orientation) {
			// TODO Auto-generated method stub
			return this._status;
		}

		@Override
		public double frontPositionAlongOrientation(Orientation orientation) {
			// TODO Auto-generated method stub
			return this._front;
		}

		@Override
		public double rearPositionAlongOrientation(Orientation orientation) {
			// TODO Auto-generated method stub
			return this._rear;
		}

		@Override
		public void intersectAlongOrientation(Road road, double frontPosition) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void intersectAlongOrientation(Moveable mobile) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public double lengthAlongOrientation(Orientation orientation) {
			// TODO Auto-generated method stub
			return this._rear-this._front;
		}

		@Override
		public void run(double tempoFactor) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String state() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	class MockRoad implements Road {

		double _length;
		double _occupiedPosition;
		ArrayList<MockIntersection> _inters = new ArrayList<>();
		
		@Override
		public double getLength() {
			// TODO Auto-generated method stub
			return _length;
		}

		@Override
		public Orientation orientation() {
			// TODO Auto-generated method stub
			return o;
		}

		@Override
		public void addMoveable(Moveable mobile) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public double getClosestOccupiedPosition(
				Moveable mobile) {
			return _occupiedPosition;
		}

		@Override
		public Iterator<MockIntersection> getRemainingIntersectionsIterator(
				Moveable mobile) {
			// TODO Auto-generated method stub
			return _inters.listIterator();
		}

		@Override
		public Iterator<MockIntersection> getAllIntersectionsIterator() {
			// TODO Auto-generated method stub
			return Collections.emptyListIterator();
		}

		@Override
		public Iterator<Moveable> getAllMoveablesIterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void run(double tempoFactor) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String state() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public CarFactoryTEST(String name) {
		super(name);
	}
	
	public void testRandomConstructorAndAttributes() {
		
		// build a new car
		Car c = cf.newCar(mr);
		
		// verify the orientation is correct
		Assert.assertEquals(c.currentOrientation(),o);
		
		// verify a color was assigned
		Assert.assertNotNull(c.color());
		
		// verify length is within specification
		Assert.assertTrue(c.length().doubleValue()>= 5 && c.length().doubleValue()<=10);
		
		// verify stop distance is within specification
		Assert.assertTrue(c.stopDistance().doubleValue()>= 0.5 && c.stopDistance().doubleValue()<=5);
		
		// verify brake distance is within specification
		Assert.assertTrue(c.brakeDistance().doubleValue()>= 9 && c.brakeDistance().doubleValue()<=10);
		
		// verify max velocity is within specification
		Assert.assertTrue(c.maxVelocity().doubleValue()>= 10 && c.maxVelocity().doubleValue()<=30);
		
		// verify initial position is zero
		double position = c.currentFrontPosition().doubleValue();
		Assert.assertTrue(position==0);
		Assert.assertTrue(c.currentRearPosition().doubleValue()==position);
		
		// verify factory gives new objects
		Assert.assertNotEquals(cf.newCar(rf.setOrientation(o).build()), cf.newCar(rf.setOrientation(o).build()));
		Assert.assertNotSame(cf.newCar(rf.setOrientation(o).build()), cf.newCar(rf.setOrientation(o).build()));
	}
	
	public void testCustomConstructorAndAttributes() {
		
		// get a custom factory
		CarFactory cf = CarFactory.CUSTOM;
		
		/*
		 * TEST ONE: VERIFY BASIC CUSTOM CAR GIVES A RANDOM ONE
		 */
		
		// build a new car
		Car c = cf.newCar(mr);
		
		// verify the orientation is correct
		Assert.assertEquals(c.currentOrientation(),o);
		
		// verify a color was assigned
		Assert.assertNotNull(c.color());
		
		// verify length is within specification
		Assert.assertTrue(c.length().doubleValue()>= 5 && c.length().doubleValue()<=10);
		
		// verify stop distance is within specification
		Assert.assertTrue(c.stopDistance().doubleValue()>= 0.5 && c.stopDistance().doubleValue()<=5);
		
		// verify brake distance is within specification
		Assert.assertTrue(c.brakeDistance().doubleValue()>= 9 && c.brakeDistance().doubleValue()<=10);
		
		// verify max velocity is within specification
		Assert.assertTrue(c.maxVelocity().doubleValue()>= 10 && c.maxVelocity().doubleValue()<=30);
		
		// verify initial position is zero
		double position = c.currentFrontPosition().doubleValue();
		Assert.assertTrue(position==0);
		Assert.assertTrue(c.currentRearPosition().doubleValue()==position);
		
		// verify factory gives new objects
		Assert.assertNotEquals(cf.newCar(rf.setOrientation(o).build()), cf.newCar(rf.setOrientation(o).build()));
		Assert.assertNotSame(cf.newCar(rf.setOrientation(o).build()), cf.newCar(rf.setOrientation(o).build()));
		
		/*
		 * TEST TWO: VERIFY WE CAN ALTER KEY ATTRIBUTES
		 */
		
		// a specific length, a length range, and illegal arguments
		CarFactory.setLength(200, 200);
		c = cf.newCar(mr);
		Assert.assertTrue(c.length().doubleValue()>= 200 && c.length().doubleValue()<=200);
		
		CarFactory.setLength(0.1, 20);
		c = cf.newCar(mr);
		Assert.assertTrue(c.length().doubleValue()>= 0.1 && c.length().doubleValue()<=20);
		
		try {
			CarFactory.setLength(0, 20);
			Assert.fail();
		} catch (IllegalArgumentException e) {}
		
		try {
			CarFactory.setLength(5.5, 5);
			Assert.fail();
		} catch (IllegalArgumentException e) {}

		// a specific velocity, a velocity range, and illegal arguments
		CarFactory.setMaxVelocity(200, 200);
		c = cf.newCar(mr);
		Assert.assertTrue(c.maxVelocity().doubleValue()>= 200 && c.maxVelocity().doubleValue()<=200);
		
		CarFactory.setMaxVelocity(100, 200);
		c = cf.newCar(mr);
		Assert.assertTrue(c.maxVelocity().doubleValue()>= 100 && c.maxVelocity().doubleValue()<=200);
		
		try {
			CarFactory.setMaxVelocity(0, 5);
			Assert.fail();
		} catch (IllegalArgumentException e) {}
		
		try {
			CarFactory.setMaxVelocity(6, 5);
			Assert.fail();
		} catch (IllegalArgumentException e) {}
			
		// a specific brake distance, a distance range, and illegal arguments
		CarFactory.setBrakeDistance(200, 200);
		c = cf.newCar(mr);
		Assert.assertTrue(c.brakeDistance().doubleValue()>= 200 && c.brakeDistance().doubleValue()<=200);
		
		CarFactory.setBrakeDistance(100, 200);
		c = cf.newCar(mr);
		Assert.assertTrue(c.brakeDistance().doubleValue()>= 100 && c.brakeDistance().doubleValue()<=200);
		
		try {
			CarFactory.setBrakeDistance(0, 5);
			Assert.fail();
		} catch (IllegalArgumentException e) {}
		
		try {
			CarFactory.setBrakeDistance(6, 5);
			Assert.fail();
		} catch (IllegalArgumentException e) {}

		// a specific stop distance, a distance range, and illegal arguments
		CarFactory.setStopDistance(10, 10);
		c = cf.newCar(mr);
		Assert.assertTrue(c.stopDistance().doubleValue()>= 10 && c.stopDistance().doubleValue()<=10);
		
		CarFactory.setStopDistance(10, 20);
		c = cf.newCar(mr);
		Assert.assertTrue(c.stopDistance().doubleValue()>= 10 && c.stopDistance().doubleValue()<=20);
		
		try {
			CarFactory.setStopDistance(0, 5);
			Assert.fail();
		} catch (IllegalArgumentException e) {}
		
		try {
			CarFactory.setStopDistance(6, 5);
			Assert.fail();
		} catch (IllegalArgumentException e) {}
		
		//  verify illegal combinations of stop and brake are caught
		try {
			CarFactory.setStopDistance(100,100 );
			CarFactory.setBrakeDistance(50, 50);
			c = cf.newCar(mr);
			Assert.fail();
		} catch (IllegalStateException e) {}
	}
	
	
	public void testMovingCar() {
		// build a new car
		Car c = cf.newCar(mr);
		
		// verify we can manually move the car forward to the correct position
		c.moveForwardToPosition(10);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() == 10);
		Assert.assertTrue(c.currentRearPosition().doubleValue() + c.length().doubleValue() == 10);
	}

	public void testMovingCarAtMaxVelocity() {
		// build a car on a mock road for testing
		Car c = cf.newCar(mr);
		mr._occupiedPosition = 80;
		mr._length = 100;
				
		// move the car on an unoccupied road with no speed governor
		BigDecimal originalPosition = c.currentFrontPosition();
		Assert.assertEquals(c.move(1), MoveableStatus.ACCELERATE);
		Assert.assertTrue(c.currentFrontPosition().compareTo(originalPosition) >= 1);
		Assert.assertTrue(c.currentFrontPosition().equals(c.maxVelocity()));
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
	
	}
	
	public void testMovingCarAtReducedVelocity() {
		// build a car on a mock road for testing
		Car c = cf.newCar(mr);
		mr._occupiedPosition = 80;
		mr._length = 100;
		// move the car on an unoccupied road with a 50% reduction speed governor
		double speedLimit = 0.5;
		BigDecimal originalPosition = c.currentFrontPosition();
		c.move(speedLimit);
		Assert.assertEquals(c.move(1), MoveableStatus.ACCELERATE);
		Assert.assertTrue(c.currentFrontPosition().compareTo(originalPosition) >= 1);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
	}
	
	public void testMovingCarSlowsDown() {
		// build a car on a mock road for testing
		Car c = cf.newCar(mr);
		mr._occupiedPosition = c.maxVelocity().doubleValue();
		mr._length = 100;
		MoveableStatus test = MoveableStatus.SLOWDOWN;
		
		// test that car moves to forward position < max velocity when forward position == max velocity
		BigDecimal originalPosition = c.currentFrontPosition();
		MoveableStatus actual = c.move(1);
		Assert.assertEquals(test, actual);
		Assert.assertTrue(c.currentFrontPosition().compareTo(originalPosition) >= 1);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
	}
	
	public void testMovingCarBrakes() {
		// build a car on a mock road for testing
		Car c = cf.newCar(mr);
		mr._occupiedPosition = c.brakeDistance().doubleValue();
		mr._length = 100;
		
		// test that car brakes if forward position w/in brake distance
		BigDecimal originalPosition = c.currentFrontPosition();
		Assert.assertEquals(c.move(1), MoveableStatus.BRAKE);
		Assert.assertTrue(c.currentFrontPosition().compareTo(originalPosition) >= 1);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
	}
	
	public void testMovingCarStops() {
		// build a car on a mock road for testing
		Car c = cf.newCar(mr);
		mr._occupiedPosition = c.stopDistance().doubleValue();
		mr._length = 100;
		MoveableStatus test = MoveableStatus.STOP;
		
		// test that car stops if forward position w/in stop distance
		MoveableStatus actual = c.move(1);
		Assert.assertEquals(test, actual);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
		
		// add an intersection just beyond unoccupied position
		mr._occupiedPosition = c.maxVelocity().doubleValue()*.99;
		MockIntersection i = new MockIntersection();
		i._front = c.maxVelocity().doubleValue();
		i._rear = i._front + 15;
		i._status = IntersectionStatus.GO;
		mr._inters.clear();
		mr._inters.add(i);
		
		// confirm that the car does not change status based on intersection
		c = cf.newCar(mr);
		actual = c.move(1);
		Assert.assertNotEquals(test, MoveableStatus.ACCELERATE);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < i._front);
	}
	
	public void testMovingCarStopsAtRedIntersection() {
		System.out.println("TESTING CAR STOPS AT RED INTERSECTION");
		
		// build a car on a mock road for testing
		Car c = cf.newCar(mr);
		mr._occupiedPosition = 80;
		mr._length = 100;		
		
		// build a mock stopped intersection
		MockIntersection i = new MockIntersection();
		i._front = c.maxVelocity().doubleValue()*.9;
		i._rear = i._front + 15;
		i._status = IntersectionStatus.STOP;
		mr._inters.add(i);
		
		MoveableStatus test = MoveableStatus.STOP;
		MoveableStatus actual = c.move(1);
		System.out.println(i._front);
		System.out.println(c);
		Assert.assertEquals(test, actual);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() <= i._front);
		mr._inters.clear();
	}

	public void testMovingCarEntersYellowIntersection() {
		
		System.out.println("TESTING CAR ENTERS YELLOW INTERSECTION -- START");
		
		// build a car on a mock road for testing
		
		Car c = cf.newCar(mr);
		mr._occupiedPosition = 80;
		mr._length = 100;		
		
		// build a mock stopped intersection
		MockIntersection i = new MockIntersection();
		i._front = c.maxVelocity().doubleValue()*.9;
		i._rear = i._front + 15;
		i._status = IntersectionStatus.CAUTION;
		mr._inters.add(i);
		
		MoveableStatus test = MoveableStatus.ACCELERATE;
		MoveableStatus actual = c.move(1);
		System.out.println("TESTING CAR ENTERS YELLOW INTERSECTION -- END");
		Assert.assertEquals(test, actual);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() >= i._front);
		mr._inters.clear();
	}
	
	public void testMovingCarStopsAtYellowIntersectionWithinBrakingDistance() {
		
		System.out.println("TESTING CAR STOPS AT YELLOW INTERSECTION W/IN BRAKE DISTANCE -- START");
		
		// build a car on a mock road for testing
		Car c = cf.newCar(mr);
		mr._occupiedPosition = 80;
		mr._length = 100;		
		
		// build a mock stopped intersection
		MockIntersection i = new MockIntersection();
		i._front = c.brakeDistance().doubleValue();
		i._rear = i._front + 15;
		i._status = IntersectionStatus.CAUTION;
		mr._inters.add(i);
		
		MoveableStatus test = MoveableStatus.BRAKE;
		MoveableStatus actual = c.move(1);
		System.out.println("TESTING CAR STOPS AT YELLOW INTERSECTION W/IN BRAKE DISTANCE -- END");
		Assert.assertEquals(test, actual);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() <= i._front);
		mr._inters.clear();
	}	

	public void testMovingCarAcceleratesThroughGreenIntersection() {
		System.out.println("TESTING CAR GOES THROUGH GREEN INTERSECTION -- START");
		
		// build a car on a mock road for testing
		Car c = cf.newCar(mr);
		mr._occupiedPosition = 80;
		mr._length = 100;		
		
		// build a mock stopped intersection
		MockIntersection i = new MockIntersection();
		i._front = c.brakeDistance().doubleValue();
		i._rear = i._front + 1;
		i._status = IntersectionStatus.GO;
		mr._inters.clear();
		mr._inters.add(i);
		
		MoveableStatus test = MoveableStatus.ACCELERATE;
		MoveableStatus actual = c.move(1);
		System.out.println("TESTING CAR GOES THROUGH GREEN INTERSECTION -- END");
		Assert.assertEquals(test, actual);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() < mr._occupiedPosition);
		Assert.assertTrue(c.currentFrontPosition().doubleValue() >= i._rear);
		mr._inters.clear();
	}
	
	public void debugTextPre(Car c, MoveableStatus s) {
		
		System.out.println("******************************** " + s);
		System.out.println("Car starts @ " + c.currentFrontPosition() + " speed = " + c.maxVelocity() +
				" stop = " + c.stopDistance() + " brake = " + c.brakeDistance());
	}
	
	public void debugTextPost(Car c, MoveableStatus s) {
		System.out.println("Car moves to @ " + c.currentFrontPosition() + " speed = " + c.maxVelocity());
		System.out.println("******************************** ");
	}
}
package simulator.intersection;

import org.junit.Assert;
import simulator.moveable.Orientation;
import junit.framework.TestCase;

public class TwoWayTrafficLightTEST extends TestCase {
	
	public TwoWayTrafficLightTEST(String name) {
		super(name);
	}
	
	public void testConstructorAndAttributes() {
		TwoWayTrafficLight tl1 = new TwoWayTrafficLight();
		
		// add two lights
		tl1.addTrafficLight(50, 55, Orientation.SOUTH);
		tl1.addTrafficLight(30, 35, Orientation.WEST);
		Assert.assertEquals(tl1.numberOfLights(),2);
		
		// test that we cannot add more than 2 lights
		try {
			tl1.addTrafficLight(50, 55, Orientation.NORTH);
			Assert.fail();
		} catch (IllegalStateException e) {};
		
		// verify light one was setup correctly
		Assert.assertTrue(tl1.frontPositionAlongOrientation(Orientation.SOUTH)==50);
		Assert.assertTrue(tl1.rearPositionAlongOrientation(Orientation.SOUTH)==55);
		Assert.assertEquals(tl1.statusAlongOrientation(Orientation.SOUTH), IntersectionStatus.GO);
		 	 
		// verify light two was setup correctly
		Assert.assertTrue(tl1.frontPositionAlongOrientation(Orientation.WEST)==30);
		Assert.assertTrue(tl1.rearPositionAlongOrientation(Orientation.WEST)==35);
		Assert.assertEquals(tl1.statusAlongOrientation(Orientation.WEST), IntersectionStatus.STOP);
		
		// get reference data for light durations
		double go = tl1.IntersectionStatusDuration(IntersectionStatus.GO);
		double stop = tl1.IntersectionStatusDuration(IntersectionStatus.STOP);
				 
		// test that initialization of timers is correct
		Assert.assertTrue(tl1.timerAlongOrientation(Orientation.SOUTH) == go);
		Assert.assertTrue(tl1.timerAlongOrientation(Orientation.WEST) == stop);

	 }
	
	public void testAgentRunSimpleDurations(int x) {
		TwoWayTrafficLight tl2 = new TwoWayTrafficLight(0, 30, 5);
		
		// setup test with two lights
		tl2.addTrafficLight(50, 55, Orientation.NORTH);
		tl2.addTrafficLight(30, 35, Orientation.EAST);
		Assert.assertEquals(tl2.numberOfLights(),2);
		
		System.out.println("*********************************************************");
		System.out.println("Init: " + tl2.timerAlongOrientation(Orientation.NORTH));
		System.out.println("Init: " + tl2.timerAlongOrientation(Orientation.EAST));
		
		// get reference data for light durations
		double go = tl2.IntersectionStatusDuration(IntersectionStatus.GO);
		double caution = tl2.IntersectionStatusDuration(IntersectionStatus.CAUTION);
		double stop = tl2.IntersectionStatusDuration(IntersectionStatus.STOP);
		
		// test that initialization of timers is correct
		Assert.assertTrue(tl2.timerAlongOrientation(Orientation.NORTH) == go);
		Assert.assertTrue(tl2.timerAlongOrientation(Orientation.EAST) == stop); 
		
		// test that the timers decrement correctly
		// test that light one remains GREEN
		// test that light two remains RED
		// test that the two lights are always the opposite of each other
		for (double step = go; step > 0; step--) {
			
			// test that both lights are always opposite of each other
			Assert.assertNotEquals(tl2.statusAlongOrientation(Orientation.NORTH).navigable(), 
					tl2.statusAlongOrientation(Orientation.EAST).navigable());

			// test light one stays GREEN the entire time
			Assert.assertEquals(tl2.statusAlongOrientation(Orientation.NORTH), IntersectionStatus.GO);
			
			// test light two stays RED the entire time
			Assert.assertEquals(tl2.statusAlongOrientation(Orientation.EAST), IntersectionStatus.STOP);
			
			tl2.run(1);
		}
	
		// during the next loop iteration, light one should switch to YELLOW
		
		// test that light one remains YELLOW
		// test that light two remains RED
		// test that the two lights are always the opposite of each other
		for (double step = caution; step > 0; step--) {
			
			// test that both lights are always opposite of each other
			Assert.assertNotEquals(tl2.statusAlongOrientation(Orientation.NORTH).navigable(), 
					tl2.statusAlongOrientation(Orientation.EAST).navigable());

			// test light one stays YELLOW the entire time
			Assert.assertEquals(tl2.statusAlongOrientation(Orientation.NORTH), IntersectionStatus.CAUTION);
					
			// test light two stays RED the entire time
			Assert.assertEquals(tl2.statusAlongOrientation(Orientation.EAST), IntersectionStatus.STOP);
			
			tl2.run(1);
		}
		
		// during the next loop iteration, light one should switch to RED & light two to GREEN
		
		// test that light one remains RED
		// test that light two remains GREEN
		// test that the two lights are always the opposite of each other
		for (double step = go; step > 0; step--) {
			
			// test that both lights are always opposite of each other
			Assert.assertNotEquals(tl2.statusAlongOrientation(Orientation.NORTH).navigable(), 
					tl2.statusAlongOrientation(Orientation.EAST).navigable());

			// test light one stays RED the entire time
			Assert.assertEquals(tl2.statusAlongOrientation(Orientation.NORTH), IntersectionStatus.STOP);
					
			// test light two stays GREEN the entire time
			Assert.assertEquals(tl2.statusAlongOrientation(Orientation.EAST), IntersectionStatus.GO);
			
			tl2.run(1);
		}
		
		// during the next loop iteration, light two should switch to YELLOW
		
		// test that light one remains RED
		// test that light two remains YELLOW
		// test that the two lights are always the opposite of each other 
		for (double step = caution; step > 0; step--) {
			
			// test that both lights are always opposite of each other
			Assert.assertNotEquals(tl2.statusAlongOrientation(Orientation.NORTH).navigable(), 
					tl2.statusAlongOrientation(Orientation.EAST).navigable());

			// test light one stays RED the entire time
			Assert.assertEquals(tl2.statusAlongOrientation(Orientation.NORTH), IntersectionStatus.STOP);
					
			// test light two stays YELLOW the entire time
			Assert.assertEquals(tl2.statusAlongOrientation(Orientation.EAST), IntersectionStatus.CAUTION);
			
			tl2.run(1);
		}
		
		// test that light one switches back to GO after a GO-CAUTION-STOP cycle
		// test that both lights are always opposite of each other
		Assert.assertNotEquals(tl2.statusAlongOrientation(Orientation.NORTH).navigable(), 
				tl2.statusAlongOrientation(Orientation.EAST).navigable());

		// test light one stays RED the entire time
		Assert.assertEquals(tl2.statusAlongOrientation(Orientation.NORTH), IntersectionStatus.GO);
							
		// test light two stays YELLOW the entire time
		Assert.assertEquals(tl2.statusAlongOrientation(Orientation.EAST), IntersectionStatus.STOP);
		
	 }

	public void testAgentRunComplexDurations() {
		
		// when the durations are random doubles, the intersection guarantees only
		// that the light's will not have navigable statuses at the same time
		TwoWayTrafficLight tl = new TwoWayTrafficLight();
		
		// setup test with two lights
		tl.addTrafficLight(50, 55, Orientation.NORTH);
		tl.addTrafficLight(30, 35, Orientation.EAST);
		Assert.assertEquals(tl.numberOfLights(),2);
		
		for (double step = 3000; step > 0; step--) {
			
			/* text output for debugging test
			String north = Orientation.NORTH + " is " + tl3.statusAlongOrientation(Orientation.NORTH) +
					" at " + tl3.timerAlongOrientation(Orientation.NORTH);
			String east = Orientation.EAST + " is " + tl3.statusAlongOrientation(Orientation.EAST) +
					" at " + tl3.timerAlongOrientation(Orientation.EAST);
			System.out.print(north);
			System.out.println(" " + east + " " + step);
			*/
			
			// test that both lights are not both navigable at the same time
		    Assert.assertFalse(tl.statusAlongOrientation(Orientation.NORTH).navigable() && 
					tl.statusAlongOrientation(Orientation.EAST).navigable());
			tl.run(1);
		}
		
	}
	
	public void moveableStuckInIntersection() {
	}
}
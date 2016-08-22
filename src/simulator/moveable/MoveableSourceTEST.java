package simulator.moveable;

import junit.framework.TestCase;

import org.junit.Assert;

import simulator.road.Road;
import simulator.road.RoadBuilder;

public class MoveableSourceTEST extends TestCase {
	
	Orientation o = Orientation.SOUTH;
	
	public MoveableSourceTEST (String name) {
		super(name);
	}
	
	public void testConstructorAndAttributes() {
		// attach a moveable source to a road
		Road r = RoadBuilder.BUILDER.setOrientation(o).build();
		MoveableSource ms = new MoveableSource(r);
		
		// verify the road was set correctly
		Assert.assertEquals(r, ms.road());
		Assert.assertSame(r, ms.road());
		
		// test that delay is within range
		Assert.assertTrue(ms.sourceDelay()>=2 && ms.sourceDelay()<=25);
		
		// test that the timer was initialized correctly
		Assert.assertTrue(ms.sourceDelay()==ms.timerValue());
	}
	
	public void testAddMoveablesToRoad() {
		// attach a moveable source to a road
		Road r = RoadBuilder.BUILDER.setOrientation(o).build();
		MoveableSource ms = new MoveableSource(r);
		
		// run the source and test that it fires at least once within min && max delay
		double delay = ms.sourceDelay();
		boolean fired = false;
		
		// test that on each run the internal timer decrements by 1
		double counter = 0;
		while (!fired) {
			ms.run(1);
			fired = ms.fired();
			
			if (fired) {
				// test that the timer resets after it fired
				Assert.assertTrue(ms.timerValue()==delay);
			 } 
			counter++;
			if (counter >= 25) {
				Assert.fail();
			}
		} 
		
		// the source has to fire at least once
		if (!fired) Assert.fail();
	}
}
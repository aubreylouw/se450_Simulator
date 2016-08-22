package simulator.road;

import java.math.BigDecimal;
import java.util.Iterator;
import junit.framework.TestCase;
import org.junit.Assert;
import simulator.agent.TimeAgent;
import simulator.intersection.TwoWayTrafficLight;
import simulator.moveable.Moveable;
import simulator.moveable.MoveableSource;
import simulator.moveable.Orientation;

public class RoadIntersectionCarTEST extends TestCase {
	
	public RoadIntersectionCarTEST(String name) {
		super(name);
	}
	
	public void testOneRoadOneIntersectionIntegration() {
		
		Orientation o = Orientation.SOUTH;
		
		// create road with one intersection running south
		RoadBuilder.BUILDER.setOrientation(o);
		Road r = RoadBuilder.BUILDER.addIntersection(new TwoWayTrafficLight()).build();
		MoveableSource ms = new MoveableSource(r);
		TimeAgent agentRoad = (TimeAgent) r;
		
		BigDecimal lastRearPosition = BigDecimal.ZERO;
		BigDecimal thisFrontPosition = BigDecimal.ZERO;
		BigDecimal thisRearPosition = BigDecimal.ZERO;
		
		for (int counter = 0; counter<20; counter++) {
			this.testFireSource(ms);
			//System.out.println("====================");
			//System.out.println("ROAD RUNNING!!!!!!!!");
			agentRoad.run(1);
			Iterator<Moveable> carIter= r.getAllMoveablesIterator();
			
			Assert.assertTrue(carIter != null);
			Assert.assertTrue(carIter.hasNext());
			
			boolean oneCarSeen = false;
			
			while (carIter.hasNext()) {
				Moveable m = carIter.next();
				
				thisFrontPosition = m.currentFrontPosition();
				thisRearPosition = m.currentRearPosition();
				
				if (oneCarSeen) {
					if (thisFrontPosition.compareTo(lastRearPosition) > 0) {
						Assert.assertTrue(thisFrontPosition.compareTo(BigDecimal.ZERO) == 0);
					}
				}
					
				lastRearPosition = m.currentRearPosition();
				oneCarSeen = true;
			}
			lastRearPosition = BigDecimal.ZERO;
		}
	}
	
	public void testFireSource(MoveableSource ms) {
		// add a car to the road
		int counter = 0; 
		boolean fired = false;
		while (!fired) {
			ms.run(1);
			fired = ms.fired();
					
			if (counter>24 && !fired) {
				Assert.fail();
			}
			counter++;
		}		
	}
}
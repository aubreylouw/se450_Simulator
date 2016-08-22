package simulator.road;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Assert;
import simulator.intersection.Intersection;
import simulator.intersection.TwoWayTrafficLight;
import simulator.moveable.Orientation;

public class RoadBuilderTEST extends TestCase {

	public RoadBuilderTEST(String name) {
		super(name);
	}
	
	public void testConstructorAndAttributesNoIntersections() {
		// cannot create an incomplete road
		try {
			RoadBuilder.BUILDER.build();
			Assert.fail();
		} catch (IllegalStateException e) {};
		
		// build road w/out intersections
		try {
			RoadBuilder.BUILDER.setOrientation(Orientation.SOUTH);
			RoadBuilder.BUILDER.build();
		} catch (IllegalStateException e) {}
		
		// build roads for all orientations
		for (Orientation o : Orientation.values()) {
			RoadBuilder.BUILDER.setOrientation(o);
			Assert.assertEquals(RoadBuilder.BUILDER.build().orientation(),o);
		}
		
		// build two roads for same orientation
		Orientation o = Orientation.EAST;
		RoadBuilder.BUILDER.setOrientation(o);
		Road r1 = RoadBuilder.BUILDER.build();
		RoadBuilder.BUILDER.setOrientation(o);
		Road r2 = RoadBuilder.BUILDER.build();
		
		// verify that the singleton produces NEW road objects
		Assert.assertNotEquals(r1, r2);
		Assert.assertNotSame(r1, r2);
		
		// verify that length is set w/in range on a road w/ no intersections
		double length = r1.getLength();
		Assert.assertTrue(length<=500);
		Assert.assertTrue(length>=200);
		
		// verify that iterator returns with no intersections
		Iterator<? extends Intersection> iter = r1.getAllIntersectionsIterator();
		Assert.assertFalse(iter.hasNext());
	}
	
	public void testConstructorAndAttributesWithIntersections() {
		// add an intersection and build a road
		Orientation o = Orientation.SOUTH;
		Road r;
		
		RoadBuilder.BUILDER.setOrientation(o);
		
		// generate a random number of intersections b/w 5 - 10
		Random rand = new Random();
		int numIntersections = 1 + (int) (5 * rand.nextDouble());
		
		ArrayList<Intersection> intersections = new ArrayList<>();
		for (int idx = 0; idx < numIntersections; idx++) {
			Intersection tl = new TwoWayTrafficLight();
			intersections.add(tl);
			//System.out.println("trying to add");
			RoadBuilder.BUILDER.addIntersection(tl);
		}
		
		r = RoadBuilder.BUILDER.build();
		//System.out.println(r);			
				
		// confirm all intersections added
		int iterRoadCount = 0, iterArrayCount = 0;
		Iterator<? extends Intersection> iterRoad = r.getAllIntersectionsIterator();
		Iterator<Intersection> iterArray = intersections.listIterator();
		while (iterRoad.hasNext()) {
			iterRoadCount++;
		}
		
		while (iterArray.hasNext()) {
			iterArrayCount++;
		}
		
		Assert.assertTrue(iterRoadCount == iterArrayCount);
		iterRoad = null;
		iterArray = null;
		intersections.clear();
		
		// verify that each intersection exists b/w two valid segments
		double cumLength = 0;
		iterRoad = r.getAllIntersectionsIterator();
		while (iterRoad.hasNext()) {
			Intersection tl = iterRoad.next();
			double front = tl.frontPositionAlongOrientation(o);
			double rear = tl.frontPositionAlongOrientation(o);
			double length = rear - front;
			
			// calculate previous segment length
			cumLength = Math.max(cumLength, rear) - length;
			cumLength = (cumLength - front <= 0) ? cumLength : cumLength - front;
			
			Assert.assertTrue(cumLength<=500);
			Assert.assertTrue(cumLength>=200);
			
			// calculate next segment length if this was the last iteration
			cumLength = r.getLength() - rear;
			
			Assert.assertTrue(cumLength<=500);
			Assert.assertTrue(cumLength>=200);
		}
	}
}
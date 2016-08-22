package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import simulator.animator.TextAnimator;
import simulator.intersection.Intersection;
import simulator.intersection.TwoWayTrafficLight;
import simulator.moveable.CarFactory;
import simulator.moveable.MoveableSource;
import simulator.moveable.Orientation;
import simulator.road.Road;
import simulator.road.RoadBuilder;
import simulator.timeserver.TimeServer;
import simulator.timeserver.TimeServerLinked;

public class ModelObj implements Model {

	private final double DEFAULT_WAKETIME = 1;
	private final TimeServer _timeserver;
	private final LinkedList<Intersection> _modelLights = new LinkedList<>();
	private final LinkedList<Road> _modelRoads = new LinkedList<>();
	private final LinkedList<MoveableSource> _modelSources = new LinkedList<>();
	private final double _modeltime;
	private final HashMap<String, String> _config = new HashMap<>();
	
	ModelObj (
			// timeserver variables
			double time, double timestep,
			
			// grid  variables
			int rows, int columns, TrafficPattern pattern, 
			
			// source variables
			double entryRateMin, double entryRateMax, 
			
			// car factory variables
			double carLengthMin, double carLengthMax,
			double carVelocityMin, double carVelocityMax, 
			double carBrakeDistanceMin, double carBrakeDistanceMax,
			double carStopDistanceMin, double carStopDistanceMax,
			
			// road variables
			double minRoadLength, double maxRoadLength,
			
			//  intersection variables
			double intersectionLengthMin, double intersectionLengthMax,
			double greenTimeMin, double greenTimeMax, 
			double yellowTimeMin, double yellowTimeMax ) {
		
		this._config.put("TIME", String.valueOf(time));
		this._config.put("TIME_STEP", String.valueOf(timestep));
		this._config.put("GRID_ROWS", String.valueOf(rows));
		this._config.put("GRID_COLUMNS", String.valueOf(columns));
		this._config.put("GRID_PATTERN", pattern.toString());
		this._config.put("SOURCE_RATE_MIN", String.valueOf(entryRateMin));
		this._config.put("SOURCE_RATE_MAX", String.valueOf(entryRateMax));
		this._config.put("CAR_LENGTH_MIN", String.valueOf(carLengthMin));
		this._config.put("CAR_LENGTH_MAX", String.valueOf(carLengthMax));
		this._config.put("CAR_VELOCITY_MIN", String.valueOf(carVelocityMin));
		this._config.put("CAR_VELOCITY_MAX", String.valueOf(carVelocityMax));
		this._config.put("CAR_BRAKE_DISTANCE_MIN", String.valueOf(carBrakeDistanceMin));
		this._config.put("CAR_BRAKE_DISTANCE_MAX", String.valueOf(carBrakeDistanceMax));
		this._config.put("CAR_STOP_DISTANCE_MIN", String.valueOf(carStopDistanceMin));
		this._config.put("CAR_STOP_DISTANCE_MAX", String.valueOf(carStopDistanceMax));
		this._config.put("ROAD_LENGTH_MIN", String.valueOf(minRoadLength));
		this._config.put("ROAD_LENGTH_MAX", String.valueOf(maxRoadLength));
		this._config.put("INTERSECTION_LENGTH_MIN", String.valueOf(intersectionLengthMin));
		this._config.put("INTERSECTION_LENGTH_MAX", String.valueOf(intersectionLengthMax));
		this._config.put("LIGHT_GREEN_MIN", String.valueOf(greenTimeMin));
		this._config.put("LIGHT_GREEN_MAX", String.valueOf(greenTimeMax));
		this._config.put("LIGHT_YELLOW_MIN", String.valueOf(yellowTimeMin));
		this._config.put("LIGHT_YELLOW_MAX", String.valueOf(yellowTimeMax));
		
		// create a new timeserver
		this._modeltime = time;
		this._timeserver = new TimeServerLinked (timestep, new TextAnimator());
		
		/*
		 * Create [column *  row] intersections
		 */
		Intersection intersection;
		final ArrayList<Intersection> intersections = new ArrayList<>(rows*columns);
		for (int idx = 0; idx < rows*columns; idx++) {
			double length = intersectionLengthMin + (intersectionLengthMax - intersectionLengthMin) * (new Random()).nextDouble();
			double greenTime = greenTimeMin + (greenTimeMax - greenTimeMin) * (new Random()).nextDouble();
			double yellowTime = yellowTimeMin + (yellowTimeMax - yellowTimeMin) * (new Random()).nextDouble();
			intersection = new TwoWayTrafficLight(length, greenTime, yellowTime); 
			intersections.add(intersection);
			this._modelLights.add(intersection);
		}
		
		/*
		 * Create [column + row] roads
		 */
		// build the columnar roads and add an intersection for each row
		Orientation columnOrientation = Orientation.SOUTH;
		RoadBuilder roadBuilder = RoadBuilder.BUILDER;
		int intersectionIdx = 0;
		for (int outer = 0; outer < columns; outer++) {
			if (!pattern.equals(TrafficPattern.SIMPLE)) 
				columnOrientation = columnOrientation.reverse();
			
			roadBuilder.setOrientation(columnOrientation);
			roadBuilder.setLength(minRoadLength, maxRoadLength);
			
			for (int inner = 0; inner < rows; inner++) {
				Intersection i = intersections.get(intersectionIdx);
				roadBuilder.addIntersection(i);
				intersectionIdx ++;
			}
			
			// add the new road to the internal collection
			Road r = roadBuilder.build();
			//System.out.println("Col road: " + r);
			this._modelRoads.add(r);
		}
		
		// build the rows roads and add an intersection for each column
		Orientation rowOrientation = Orientation.EAST;
		for (int outer = 0; outer < rows; outer++) {
			if (!pattern.equals(TrafficPattern.SIMPLE)) 
				rowOrientation = rowOrientation.reverse();
			
			roadBuilder.setOrientation(rowOrientation);
			roadBuilder.setLength(minRoadLength, maxRoadLength);
			
			// for each row:
			// lookup the column roads in sequence of creation
			// for each column road, get its intersections
			// iterate through intersections, getting only the intersection for that road (row 1, get intersection 1)
			for (int roadIdx =  0; roadIdx < columns; roadIdx++) {
				Iterator<? extends Intersection> iter = this._modelRoads.get(roadIdx).getAllIntersectionsIterator();
				int interIdx = 0;
				while (iter.hasNext() && interIdx < rows) {
					Intersection i = iter.next();
					if (interIdx == outer)  {
						roadBuilder.addIntersection(i);
						break;
					}
					interIdx++;
				}
			}				
			
			// add the new road to the internal collection
			Road r = roadBuilder.build();
			//System.out.println("Row road: " + r);
			this._modelRoads.add(r);
		}
		
		// wipe temp collection
		intersections.clear();
		
		// for each road created, add a moveable source
		// each moveable source needs to know what sort of car factory to use
		CarFactory carFactory = CarFactory.CUSTOM;
		MoveableSource source;
		
		for (Road r: this._modelRoads) {
			
			CarFactory.setBrakeDistance(carBrakeDistanceMin, carBrakeDistanceMax);
			CarFactory.setStopDistance(carStopDistanceMin, carStopDistanceMax);
			CarFactory.setMaxVelocity(carVelocityMin, carVelocityMax);
			CarFactory.setLength(carLengthMin, carLengthMax);
			
			source = new MoveableSource (r, entryRateMin, entryRateMax, carFactory);
			
			this._modelSources.add(source);
		}
	}
	
	public void configure() {
		
	}
	
	@Override
	public void simulate() {
		// reset the timeserver
		this._timeserver.reset();
		
		// enqueue model objects to re-set timeserver to continue the modeling
		for (MoveableSource source : this._modelSources)
			this._timeserver.enqueue(DEFAULT_WAKETIME, source);
		
		for (Road road : this._modelRoads)
			this._timeserver.enqueue(DEFAULT_WAKETIME, road);
		
		for (Intersection intersection : this._modelLights)
			this._timeserver.enqueue(DEFAULT_WAKETIME, intersection);
		
		System.out.println("Running TS with " + this._modeltime);
		
		this._timeserver.run(this._modeltime);
	}
	
	public String toString() {
		StringBuilder currConfig = new StringBuilder();
		
		currConfig.append("Simulation time step (seconds)       ");
		currConfig.append("[").append(this._config.get("TIME_STEP")).append("]").append("\n");
	
		currConfig.append("Simulation run time (seconds)        ");
		currConfig.append("[").append(this._config.get("TIME")).append("]").append("\n");
		
		currConfig.append("Grid size (number of roads)          ");
		currConfig.append("[row=").append(this._config.get("GRID_ROWS"));
		currConfig.append(", column=").append(this._config.get("GRID_COLUMNS")).append("]").append("\n");
		
		currConfig.append("Traffic pattern                      ");
		currConfig.append("[").append(this._config.get("GRID_PATTERN")).append("]").append("\n");
		
		currConfig.append("Car entry rate (seconds/car)         ");
		currConfig.append("[min=").append(this._config.get("SOURCE_RATE_MIN"));
		currConfig.append(", max=").append(this._config.get("SOURCE_RATE_MAX")).append("]").append("\n");
		
		currConfig.append("Road segment length (meters)         ");
		currConfig.append("[min=").append(this._config.get("ROAD_LENGTH_MIN"));
		currConfig.append(", max=").append(this._config.get("ROAD_LENGTH_MAX")).append("]").append("\n");
		
		currConfig.append("Intersection length (meters)         ");
		currConfig.append("[min=").append(this._config.get("INTERSECTION_LENGTH_MIN"));
		currConfig.append(", max=").append(this._config.get("INTERSECTION_LENGTH_MAX")).append("]").append("\n");
		
		currConfig.append("Car length (meters)                  ");
		currConfig.append("[min=").append(this._config.get("CAR_LENGTH_MIN"));
		currConfig.append(", max=").append(this._config.get("CAR_LENGTH_MAX")).append("]").append("\n");
		
		currConfig.append("Car maximum velocity (meters/second) ");
		currConfig.append("[min=").append(this._config.get("CAR_VELOCITY_MIN"));
		currConfig.append(", max=").append(this._config.get("CAR_VELOCITY_MAX")).append("]").append("\n");
		
		currConfig.append("Car stop distance (meters)           ");
		currConfig.append("[min=").append(this._config.get("CAR_STOP_DISTANCE_MIN"));
		currConfig.append(", max=").append(this._config.get("CAR_STOP_DISTANCE_MAX")).append("]").append("\n");
		
		currConfig.append("Car brake distance (meters)          ");
		currConfig.append("[min=").append(this._config.get("CAR_BRAKE_DISTANCE_MIN"));
		currConfig.append(", max=").append(this._config.get("CAR_BRAKE_DISTANCE_MAX")).append("]").append("\n");
		
		currConfig.append("Traffic light green time (seconds)   ");
		currConfig.append("[min=").append(this._config.get("LIGHT_GREEN_MIN"));
		currConfig.append(", max=").append(this._config.get("LIGHT_GREEN_MAX")).append("]").append("\n");
		
		currConfig.append("Traffic light yellow time (seconds)  ");
		currConfig.append("[min=").append(this._config.get("LIGHT_YELLOW_MIN"));
		currConfig.append(", max=").append(this._config.get("LIGHT_YELLOW_MAX")).append("]").append("\n");
		
		return currConfig.toString();
	}
}
package simulator.model;

import simulator.intersection.TwoWayTrafficLight;
import simulator.moveable.CarFactory;
import simulator.moveable.MoveableSource;
import simulator.road.RoadBuilder;

public enum ModelBuilder {
	BUILDER {
		@Override
		// create a default RoadBuilder.Builder
		
		public Model newModel() {

			return new ModelObj (_time, _timeStep, 
					// grid defaults
					_grid_rows, _grid_columns, _grid_pattern,
					// source defaults
					_min_source_rate, _max_source_rate,
					// car defaults
					_min_car_length, _max_car_length, 
					_min_car_velocity, _max_car_velocity, 
					_min_car_brake_distance, _max_car_brake_distance, 
					_min_car_stop_distance, _max_car_stop_distance,
					// road defaults
					_min_road_length , _max_road_length ,
					// intersection defaults
					_min_intersection_length, _max_intersection_length, 
					_min_green_time, _max_green_time, 
					_min_yellow_time, _max_yellow_time);
		}
	};
	
	/*
	 * model defaults
	 */
	private static final double TIME = 1000;
	private static final double TIME_STEP = 0.1;
	private static final int GRID_ROWS = 2;
	private static final int GRID_COLUMNS = 3;
	private static final TrafficPattern GRID_PATTERN = TrafficPattern.ALTERNATING;
	private static final double LIGHT_MIN_YELLOW = TwoWayTrafficLight.MIN_CAUTION_DURATION;
	private static final double LIGHT_MAX_YELLOW = TwoWayTrafficLight.MIN_CAUTION_DURATION;
	private static final double LIGHT_MIN_GREEN = TwoWayTrafficLight.MIN_GO_DURATION;
	private static final double LIGHT_MAX_GREEN = TwoWayTrafficLight.MAX_GO_DURATION;
	private static final double LIGHT_MIN_LENGTH = TwoWayTrafficLight.MIN_LENGTH;
	private static final double LIGHT_MAX_LENGTH = TwoWayTrafficLight.MAX_LENGTH;
	private static final double ROAD_MIN_LENGTH = RoadBuilder.MIN_LENGTH;
	private static final double ROAD_MAX_LENGTH = RoadBuilder.MAX_LENGTH;
	private static final double CAR_MIN_LENGTH = CarFactory.MIN_LENGTH;
	private static final double CAR_MAX_LENGTH = CarFactory.MAX_LENGTH;
	private static final double CAR_MIN_VELOCITY = CarFactory.MIN_VELOCITY;
	private static final double CAR_MAX_VELOCITY = CarFactory.MAX_VELOCITY;
	private static final double CAR_MIN_BRAKE_D = CarFactory.MIN_BRAKE;
	private static final double CAR_MAX_BRAKE_D = CarFactory.MAX_BRAKE;
	private static final double CAR_MIN_STOP_D = CarFactory.MIN_STOP;
	private static final double CAR_MAX_STOP_D = CarFactory.MAX_STOP;
	private static final double SOURCE_MIN_RATE = MoveableSource.MIN_DELAY;
	private static final double SOURCE_MAX_RATE = MoveableSource.MAX_DELAY;
	
	/*
	 * car mutables initially set to defaults
	 */
	private static double _min_car_length = CAR_MIN_LENGTH;
	private static double _max_car_length = CAR_MAX_LENGTH;
	private static double _min_car_velocity = CAR_MIN_VELOCITY;
	private static double _max_car_velocity = CAR_MAX_VELOCITY;
	private static double _min_car_brake_distance = CAR_MIN_BRAKE_D;
	private static double _max_car_brake_distance = CAR_MAX_BRAKE_D;
	private static double _min_car_stop_distance = CAR_MIN_STOP_D;
	private static double _max_car_stop_distance = CAR_MAX_STOP_D;
	
	/*
	 * source mutables initially set to defaults
	 */
	private static double _min_source_rate = SOURCE_MIN_RATE;
	private static double _max_source_rate = SOURCE_MAX_RATE;
	
	/*
	 * road mutables initially set to defaults
	 */
	private static double _min_road_length = ROAD_MIN_LENGTH;
	private static double _max_road_length = ROAD_MAX_LENGTH;
	
	/*
	 * grid mutables initially set to defaults
	 */
	private static int _grid_rows = GRID_ROWS;
	private static int _grid_columns = GRID_COLUMNS;
	private static TrafficPattern _grid_pattern = GRID_PATTERN;
	
	/*
	 * light mutables  initially set to defaults
	 */
	private static double _min_green_time = LIGHT_MIN_GREEN;
	private static double _max_green_time = LIGHT_MAX_GREEN;
	private static double _min_yellow_time = LIGHT_MIN_YELLOW;
	private static double _max_yellow_time = LIGHT_MAX_YELLOW;
	private static double _min_intersection_length = LIGHT_MIN_LENGTH;
	private static double _max_intersection_length = LIGHT_MAX_LENGTH;
	
	/*
	 * time mutables initially set to defaults
	 */
	private static double _time = TIME;
	private static double _timeStep = TIME_STEP;
	
	public abstract Model newModel();
	
	public static void changeModelTime(double time) {
		_time = time;
	}
	
	public static void changeModelTimeStep (double timeStep) {
		_timeStep = timeStep;
	}
	
	public static void changeModelGridSize (int row, int column) {
		_grid_rows = row;
		_grid_columns = column;
	}
	
	public static void changeTrafficPatternn (TrafficPattern pattern) {
		if (pattern.equals(TrafficPattern.NOT_DEFINED))
			pattern = TrafficPattern.SIMPLE;
		_grid_pattern = pattern;
	}
	
	public static void changeCarEntryRate (double min, double max) {
		_min_source_rate = min;
		_max_source_rate = max;
	}
	
	public static void changeRoadSegmentLength (double min, double max) {
		_min_road_length = min;
		_max_road_length = max;
	}
	
	public static void changeIntersectionLength (double min, double max) {
		_min_intersection_length = min;
		_max_intersection_length = max;
	}
	
	public static void changeCarLength (double min, double max) {
		_min_car_length = min;
		_max_car_length = max;	
	}
	
	public static void changeCarMaxVelocity (double min, double max) {
		_min_car_velocity = min;
		_max_car_velocity = max;	
	}
	
	public static void changeCarBrakeDistance (double min, double max) {
		_min_car_brake_distance = min;
		_max_car_brake_distance = max;
	}
	
	public static void changeCarStopDistance (double min, double max) {
		_min_car_stop_distance = min;
		_max_car_stop_distance = max;
	}
	
	public static void changeGreenLightDuration (double min, double max) {
		_min_green_time = min;
		_max_green_time = max;
	}
	
	public static void changeYellowLightDuration (double min, double max) {
		_min_yellow_time = min;
		_max_yellow_time = max;
	}
}
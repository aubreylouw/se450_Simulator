package simulator.moveable;

import java.math.BigDecimal;
import java.util.Random;

import simulator.road.Road;

public enum CarFactory {
	RANDOM {
		
		@Override
		public Car newCar(Road road) {
			BigDecimal maxVelocity = BigDecimal.valueOf(MIN_VELOCITY + (MAX_VELOCITY - MIN_VELOCITY)* RANDOM_FACTORY.nextDouble());
			BigDecimal brakeDistance = BigDecimal.valueOf(MIN_BRAKE + (MAX_BRAKE - MIN_BRAKE)* RANDOM_FACTORY.nextDouble());
			BigDecimal stopDistance = BigDecimal.valueOf(MIN_STOP + (MAX_STOP - MIN_STOP)* RANDOM_FACTORY.nextDouble());
			BigDecimal length = BigDecimal.valueOf(MIN_LENGTH + (MAX_LENGTH - MIN_LENGTH)* RANDOM_FACTORY.nextDouble());
			
			return new Car(maxVelocity, brakeDistance, stopDistance, length, road);
		}
	},
	CUSTOM {

		@Override
		public Car newCar(Road road) {
			BigDecimal maxVelocity = BigDecimal.valueOf(MIN_VELOCITY + (MAX_VELOCITY - MIN_VELOCITY)* RANDOM_FACTORY.nextDouble());
			BigDecimal brakeDistance = BigDecimal.valueOf(MIN_BRAKE + (MAX_BRAKE - MIN_BRAKE)* RANDOM_FACTORY.nextDouble());
			BigDecimal stopDistance = BigDecimal.valueOf(MIN_STOP + (MAX_STOP - MIN_STOP)* RANDOM_FACTORY.nextDouble());
			BigDecimal length = BigDecimal.valueOf(MIN_LENGTH + (MAX_LENGTH - MIN_LENGTH)* RANDOM_FACTORY.nextDouble());
			
			if (!_maxVelocity.equals(BigDecimal.ZERO))
				maxVelocity = _maxVelocity;
			if (!_brakeDistance.equals(BigDecimal.ZERO))
				brakeDistance = _brakeDistance;
			if (!_stopDistance.equals(BigDecimal.ZERO))
				stopDistance = _stopDistance;
			if (!_length.equals(BigDecimal.ZERO))
				length = _length;
			
			if (stopDistance.compareTo(brakeDistance) > 0)
				throw new IllegalStateException ("Stop: " + stopDistance + " > Brake: " + brakeDistance);
			
			return new Car(maxVelocity, brakeDistance, stopDistance, length, road);
		}
	};

	/* default seeding constant */
	public static final double MIN_LENGTH = 5;
	public static final double MAX_LENGTH = 10;
	public static final double MIN_VELOCITY = 10;
	public static final double MAX_VELOCITY = 30;
	public static final double MIN_STOP = 0.5;
	public static final double MAX_STOP = 5;
	public static final double MIN_BRAKE = 9;
	public static final double MAX_BRAKE = 10;
	private static final Random RANDOM_FACTORY = new Random();
	
	/* mutator variables to create a car */
	private static BigDecimal _maxVelocity = BigDecimal.ZERO;
	private static BigDecimal _brakeDistance = BigDecimal.ZERO;
	private static BigDecimal _stopDistance = BigDecimal.ZERO;
	private static BigDecimal _length = BigDecimal.ZERO;
	
	public abstract Car newCar(Road road);
	
	public static void setLength (double min, double max){
		if (min <= 0 || min > max)
			throw new IllegalArgumentException ();
		
		_length = BigDecimal.valueOf(min + (max - min)* RANDOM_FACTORY.nextDouble());
	};
	
	public static void setMaxVelocity (double min, double  max){
		if (min <= 0 || min > max)
			throw new IllegalArgumentException ();
		
		_maxVelocity = BigDecimal.valueOf(min + (max - min)* RANDOM_FACTORY.nextDouble());
	};
	
	public static void setBrakeDistance (double min, double max){
		if (min <= 0 || min > max)
			throw new IllegalArgumentException ();
		
		_brakeDistance = BigDecimal.valueOf(min + (max - min)* RANDOM_FACTORY.nextDouble());
	};

	public static void setStopDistance (double min, double max){
		if (min <= 0 || min > max)
			throw new IllegalArgumentException ();
		
		_stopDistance = BigDecimal.valueOf(min + (max - min)* RANDOM_FACTORY.nextDouble());
	};
}
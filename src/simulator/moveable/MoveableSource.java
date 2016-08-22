package simulator.moveable;

import java.util.Random;
import simulator.agent.TimeAgent;
import simulator.road.Road;

public final class MoveableSource implements TimeAgent {
	
	private static final double DEFAULT_TEMPO = 1.0;
	public static final double MIN_DELAY = 2;
	public static final double MAX_DELAY = 25;
	private final double DELAY;
	private final Road _road;
	private final CarFactory _carFactory;
	private double _timer;
	private boolean _fired;
	private String _state;
	
	public MoveableSource(Road road) {
		this(road, MIN_DELAY, MAX_DELAY, CarFactory.RANDOM);
	}
	
	public MoveableSource (Road road, double minDelay, double maxDelay) {
		this(road, minDelay, maxDelay, CarFactory.RANDOM);
	}

	public MoveableSource (Road road, CarFactory carFactory) {
		this(road, MIN_DELAY, MAX_DELAY, carFactory);
	}
	
	public MoveableSource (Road road, double minDelay, double maxDelay, CarFactory carFactory) {
		this.DELAY = minDelay + (maxDelay - minDelay) * (new Random()).nextDouble();
		this._timer = this.DELAY;
		this._road = road;
		this._fired = false;
		this._carFactory = carFactory;
	}
	
	public Road road() {
		return this._road;
	}
	
	public double sourceDelay() {
		return this.DELAY;
	}
	
	public double timerValue() {
		return this._timer;
	}
	
	@Override
	public void run (double tempoFactor) {
		this._fired = false;
		
		this._timer -= (DEFAULT_TEMPO*tempoFactor);
		
		StringBuilder state = new StringBuilder();
		state.append("Source: ");
		
		if (this._timer <=0) {
			this._timer = this.DELAY;
			//Moveable car = CarFactory.RANDOM.newCar(this._road);
			Moveable car = this._carFactory.newCar(this._road);
			this._road.addMoveable(car);
			this._fired = true;
			state.append("created { ").append(car).append(" }");
		} else state.append("{ NO ACTION }");
		this._state = state.toString();
	}
	
	/*
	 * for testing
	 */
	public boolean fired() {
		return this._fired;
	}

	@Override
	public String state() {
		return this._state;
	}
}
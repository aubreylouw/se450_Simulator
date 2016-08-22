package simulator.timeserver;

import simulator.agent.TimeAgent;

public interface TimeServer {
	public double currentTime();
	public void enqueue(double waketime, TimeAgent thing);
	public void run(double duration);
	public void reset();
}
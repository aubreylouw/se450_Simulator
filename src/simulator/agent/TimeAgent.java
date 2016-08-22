package simulator.agent;

public interface TimeAgent {
	
	/*
	 * Run the agent at a particular tempo factor.
	 *
	 */
	public void run (double tempoFactor);
	
	public String state();
}

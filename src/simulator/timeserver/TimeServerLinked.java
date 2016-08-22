package simulator.timeserver;

import java.util.Observable;
import simulator.agent.TimeAgent;
import simulator.animator.Animator;

public final class TimeServerLinked extends Observable implements TimeServer {
	private static final class Node {
		final double waketime;
		final TimeAgent agent;
		Node next;
  
		public Node(double waketime, TimeAgent agent, Node next) {
			this.waketime = waketime;
			this.agent = agent;
			this.next = next;
		}
	}
	
	private double _currentTime;
	private int _size;
	private Node _head;
	private double _timeStep;

	/*
	* Invariant: _head != null
	* Invariant: _head.agent == null
	* Invariant: (_size == 0) iff (_head.next == null)
	*/
	public TimeServerLinked (double timeStep, Animator animator) {
		_size = 0;
		_head = new Node(0, null, null);
		_timeStep = timeStep;
		super.addObserver(animator);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		Node node = _head.next;
		String sep = "";
		while (node != null) {
			sb.append(sep).append("(").append(node.waketime).append(",");
			sb.append(node.agent).append(")");
			node = node.next;
			sep = ";";
		}
		sb.append("]");
		return (sb.toString());
	}

	public double currentTime() {
		return _currentTime;
	}

	public void enqueue(double waketime, TimeAgent agent)
			throws IllegalArgumentException
	{
		if (waketime < _currentTime)
			throw new IllegalArgumentException();
		Node prevElement = _head;
		while ((prevElement.next != null) &&
				(prevElement.next.waketime <= waketime)) {
			prevElement = prevElement.next;
		}
		Node newElement = new Node(waketime, agent, prevElement.next);
		prevElement.next = newElement;
		_size++;
	}

	TimeAgent dequeue()
	{
		if (_size < 1)
			throw new java.util.NoSuchElementException();
		TimeAgent rval = _head.next.agent;
		_head.next = _head.next.next;
		_size--;
		return rval;
	}

	int size() {
		return _size;
	}

	boolean empty() {
		return size() == 0;
	}

	public void run(double duration) {
		double endtime = _currentTime + duration;
		while ((!empty()) && (_head.next.waketime <= endtime)) {
			
			_currentTime = _head.next.waketime;
		
			TimeAgent ta = dequeue();
			ta.run(_timeStep);
			super.setChanged();
			super.notifyObservers(ta);
			
			this.enqueue(_timeStep + this.currentTime(), ta);
		}
		_currentTime = endtime;
	}

	/*
	 * Resets the timeserver to its original state. Allows the object to be re-used or restarted
	 * if orifinal timeagents are re-enqueued.
	 * @see simulator.timeserver.TimeServer#reset()
	 */
	@Override
	public void reset() {
		_currentTime = 0;
		_size = 0;
		_head = new Node(0, null, null);
	}
}
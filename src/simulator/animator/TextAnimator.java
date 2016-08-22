package simulator.animator;
import java.util.Observable;
import simulator.agent.TimeAgent;

public class TextAnimator implements Animator {
	
	public TextAnimator() {}
	
	@Override
	public void update(Observable model, Object arg){
		System.out.println(((TimeAgent) (arg)).state());
	}
}
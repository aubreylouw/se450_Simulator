package simulator.UI;

public class UIFactory {
	private UIFactory() {}
	static private UI _UI;
	
	static public UI ui () {
		_UI = new TextUI();
				
		return _UI;
	}
}

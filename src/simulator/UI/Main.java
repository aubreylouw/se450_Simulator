package simulator.UI;

import simulator.model.ModelBuilder;

public class Main {
	private Main() {}
	public static void main(String[] args) {
		Control control = new Control(UIFactory.ui(), ModelBuilder.BUILDER);
		control.run();
	}
}
package simulator.UI;

/**
 * @see UIFormBuilder
 */
public final class UIForm {
	private final String _heading;
	private final Pair[] _form;

	static final class Pair {
		final String prompt;
		final UIFormTest test;

		Pair(String thePrompt, UIFormTest theTest) {
			this.prompt = thePrompt;
			this.test = theTest;
		}
	}
  
	UIForm(String heading, Pair[] menu) {
		this._heading = heading;
		this._form = menu;
	}
  
	public int size() {
		return this._form.length;
	}
  
	public String getHeading() {
		return this._heading;
	}
  
	public String getPrompt(int i) {
		return this._form[i].prompt;
	}
  
	public boolean checkInput(int i, String input) {
		if (null == this._form[i])
			return true;
		return this._form[i].test.run(input);
	}
}

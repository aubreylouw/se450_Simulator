package simulator.UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public final class TextUI implements UI {
	final BufferedReader _in;
	final PrintStream _out;

	public TextUI() {
		this._in = new BufferedReader(new InputStreamReader(System.in));
		this._out = System.out;
	}

	public void displayMessage(String message) {
		this._out.println(message);
	}

	public void displayError(String message) {
		this._out.println(message);
	}

	private String getResponse() {
		String result;
		try {
			result = this._in.readLine();
		} catch (IOException e) {
			throw new UIError(); // re-throw UIError
		}
    
		if (result == null) {
			throw new UIError(); // input closed
		}
		
		return result;
	}

	public void processMenu(UIMenu menu) {
		this._out.println(menu.getHeading());
		this._out.println("Enter choice by number:");

		for (int i = 1; i < menu.size(); i++) {
			this._out.println("  " + i + ". " + menu.getPrompt(i));
		}

		String response = getResponse();
		int selection;
		try {
			selection = Integer.parseInt(response, 10);
			if ((selection < 0) || (selection >= menu.size()))
				selection = 0;
		} catch (NumberFormatException e) {
			selection = 0;
		}

		menu.runAction(selection);
	}

	public String[] processForm(UIForm form) {
		String[] inputs = new String[form.size()];
		for (int i = 0; i < form.size(); i++) {
			String prompt = form.getPrompt(i);
			displayMessage(prompt);
			String input = getResponse();
			inputs[i] = input;
			if (form.checkInput(i, input)) 
				inputs[i] = input;
			else {
				displayMessage("Invalid entry. Please re-enter.");
				i--;
			}
		}
		return inputs;
	}
}
package simulator.UI;

import simulator.model.Model;
import simulator.model.ModelBuilder;
import simulator.model.TrafficPattern;

class Control {
	private static final int EXITED = 0;
	private static final int EXIT = 1;
	private static final int START = 2;
	private static final int CHANGE = 3;
	private static final int NUMSTATES = 4;
	private UIMenu[] _menus;
	private int _state;
	private UI _ui;
	private Model _model;
	private ModelBuilder _modelBuilder;
	private UIForm _getMinMaxValueForm;
	private UIForm _getSingleValueForm;
	private UIForm _getStringValueForm;
	private UIForm _getGridValueForm;
  
	Control (UI ui, ModelBuilder modelBuilder) {
		this._ui = ui;
		this._menus = new UIMenu[NUMSTATES];
		this._state = START;
		this.addSTART(START);
		this.addEXIT(EXIT);
		this.addCHANGE(CHANGE);
		this._modelBuilder = modelBuilder;
		this._model = modelBuilder.newModel();
		
		UIFormTest gtZeroTest = new UIFormTest() {
			public boolean run(String input) {
				try {
					double i = Double.parseDouble(input);
					return i > 0;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		};
		
		UIFormTest stringTest = new UIFormTest() {
			public boolean run(String input) {
				return ! "".equals(input.trim());
			}
		};
		
		UIFormBuilder f = new UIFormBuilder();
		f.add("Minimum numerical value (inclusive): ", gtZeroTest);
		f.add("Maximum numerical value (inclusive):", gtZeroTest);
		this._getMinMaxValueForm = f.toUIForm("Enter min/max values:");
		
		UIFormBuilder g = new UIFormBuilder();
		g.add("Fixed numerical value: ", gtZeroTest);
		this._getSingleValueForm = g.toUIForm("Enter new fixed value:");
		
		UIFormBuilder h = new UIFormBuilder();
		h.add("Fixed string value: ", stringTest);
		this._getStringValueForm = h.toUIForm("Enter new fixed value:");
		
		UIFormBuilder i = new UIFormBuilder();
		i.add("Integer rows : ", gtZeroTest);
		i.add("Integer columns :", gtZeroTest);
		this._getGridValueForm = i.toUIForm("Enter grid size:");
	}
  
	void run() {
		try {
			while (this._state != EXITED) {
				this._ui.processMenu(this._menus[this._state]);
			}
		} catch (UIError e) {
			this._ui.displayError("UI closed");
		}
	}
  
	private void addSTART(int stateNum) {
		UIMenuBuilder m = new UIMenuBuilder();
    
		m.add("Default", new UIMenuAction() {
			public void run() {
				Control.this._ui.displayError("doh!");
			}
		});
    
		m.add("Run the simulation", new UIMenuAction() {
			public void run() {
				
				System.out.println("Running simulation!");
				Control.this._model.simulate();
				
				Control.this._state = START;
			}
		});
    
		m.add("Change simulation parameters", new UIMenuAction() {
			public void run() {
				Control.this._state = CHANGE;	
			}
		});
   
		m.add("Exit the simulation",new UIMenuAction() {
			public void run() {
				Control.this._state = EXIT;
			}
		});
     
		Control.this._menus[stateNum] = m.toUIMenu("");
	}
  
	private void addEXIT(int stateNum) {
		UIMenuBuilder m = new UIMenuBuilder();
    
		m.add("Default", new UIMenuAction() { public void run() {} });
		
		m.add("Yes", new UIMenuAction() {
			public void run() {
				Control.this._state = EXITED;
			}
		});
    
		m.add("No",new UIMenuAction() {
			public void run() {
				Control.this._state = START;
			}
		});
    
		Control.this._menus[stateNum] = m.toUIMenu("Are you sure you want to exit?");
	}
	
	private void addCHANGE(int stateNum) {
		UIMenuBuilder m = new UIMenuBuilder();
    
		m.add("Default", new UIMenuAction() { public void run() {} });
		
		m.add("Show current values", new UIMenuAction() {
			public void run() {
				System.out.println("Printing current values:");
				Control.this._ui.displayMessage(Control.this._model.toString());
			}
		});
    
		m.add("Simulation time step",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getSingleValueForm);
				double value = Double.parseDouble(result1[0]);
				Control.this._modelBuilder.changeModelTimeStep(value);
			}
		});
    
		m.add("Simulation run time",new UIMenuAction() {
			public void run() {
				
				String[] result1 = Control.this._ui.processForm(Control.this._getSingleValueForm);
				double value = Double.parseDouble(result1[0]);
				Control.this._modelBuilder.changeModelTime(value);
			}
		});
		
		m.add("Simulation grid size",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getGridValueForm);
				int row = Integer.parseInt(result1[0]);
				int column = Integer.parseInt(result1[1]);
				Control.this._modelBuilder.changeModelGridSize(row, column);
			}
		});
		
		m.add("Simulation traffic pattern",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getStringValueForm);
				TrafficPattern value = TrafficPattern.toTrafficPattern(result1[0]);
				Control.this._modelBuilder.changeTrafficPatternn(value);
			}
		});
		
		m.add("Simulation car entry rate",new UIMenuAction() {
			public void run() {
				//System.out.println("Change car entry rate");
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeCarEntryRate(min, max);
			}
		});
		
		m.add("Simulation road segment length",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeRoadSegmentLength(min, max);
			}
		});
		
		m.add("Simulation intersection length",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeIntersectionLength(min, max);
			}
		});
		
		m.add("Simulation car length",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeCarLength(min, max);
			}
		});
		
		m.add("Simulation car max velocity",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeCarMaxVelocity(min, max);
			}
		});
		
		m.add("Simulation car brake distance",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeCarBrakeDistance(min, max);
			}
		});
		
		m.add("Simulation car stop distance",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeCarStopDistance(min, max);
			}
		});
		
		m.add("Simulation car brake distance",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeCarBrakeDistance(min, max);
			}
		});
		
		m.add("Simulation traffic green light time",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeGreenLightDuration(min, max);
			}
		});
		
		m.add("Simulation traffic yellow light time",new UIMenuAction() {
			public void run() {
				String[] result1 = Control.this._ui.processForm(Control.this._getMinMaxValueForm);
				double min = Double.parseDouble(result1[0]);
				double max = Double.parseDouble(result1[1]);
				Control.this._modelBuilder.changeYellowLightDuration(min, max);
			}
		});
		
		m.add("Reset simulation and return to main menu",new UIMenuAction() {
			public void run() {
				// build a new model based on current status of builder
				Control.this._model = Control.this._modelBuilder.newModel();
				Control.this._state = START;
			}
		});
		
		Control.this._menus[stateNum] = m.toUIMenu("");
	}
}

package simulator.UI;

import java.util.ArrayList;
import java.util.List;

public final class UIFormBuilder {
  
	private final List<UIForm.Pair> _menu = new ArrayList<UIForm.Pair>();
  
	public void add(String prompt, UIFormTest test) {
		this._menu.add(new UIForm.Pair(prompt, test));
	}
  
	public UIForm toUIForm(String heading) {
		if (null == heading)
			throw new IllegalArgumentException();
		if (this._menu.size() < 1)
			throw new IllegalStateException();
		UIForm.Pair[] array = new UIForm.Pair[this._menu.size()];
    
		for (int i = 0; i < _menu.size(); i++)
			array[i] = this._menu.get(i);
		return new UIForm(heading, array);
	}
}

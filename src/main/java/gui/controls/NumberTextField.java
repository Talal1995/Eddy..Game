package gui.controls;

import javafx.scene.control.TextField;

public class NumberTextField extends TextField {
	@Override
	public void replaceText(int start, int end, String text) {
		if (text.length() == 0 | validate(text)) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String text) {
		if (validate(text)) {
			super.replaceSelection(text);
		}
	}

	private boolean validate(String text) {
		return text.matches("\\d|\\.");
	}
}

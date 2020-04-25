package de.immerfroehlich.gui;

public class TextInputDialog {
	
	private String text;
	
	public TextInputDialog(String text) {
		this.text = text;
	}
	
	public String showAndWait() {
		
		javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
		dialog.getDialogPane().setContentText(text);
		dialog.setHeaderText(null);
		
		//TODO Workaround for openjfx bug 222. Remove after backport to Java 11 or update to Java 12
		//See https://github.com/javafxports/openjdk-jfx/issues/222
		dialog.setResizable(true);
		
		return dialog.showAndWait().get();
		
//		.filter(response -> response.equals("OK"))
//		.ifPresent(response -> input = dialog.getEditor().getText());
	}
	
}

package hu.tryharddood.scgui.Logger;

import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static hu.tryharddood.scgui.Logger.Console.Prefix.*;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 19..
 ****************************************************/
public class Console {
	private TextFlow console;

	public Console(TextFlow console) {
		this.console = console;
	}

	public void println(Prefix prefix, String message) {
		Text text = new Text(message);
		text.setFont(Font.font("Consolas", 13));
		Platform.runLater(() ->
		{
			if (getPrefix(prefix).getText().length() == 0)
				console.getChildren().addAll(text, new Text(System.getProperty("line.separator")));
			else
				console.getChildren().addAll(getPrefix(prefix), new Text(" - "), text, new Text(System.getProperty("line.separator")));
		});
	}

	public void println(String message) {
		println(Prefix.NONE, message);
	}

	private Text getPrefix(Prefix prefix) {
		if (prefix == INFO)
		{
			Text text = new Text("INFO");
			text.setStyle("-fx-fill: dodgerblue;-fx-font-weight:bold;");
			text.setFont(Font.font("Consolas", 13));
			return text;
		}
		else if (prefix == WARNING)
		{
			Text text = new Text("WARNING");
			text.setStyle("-fx-fill: orange;-fx-font-weight:bold;");
			text.setFont(Font.font("Consolas", 13));
			return text;
		}
		else if (prefix == ERROR)
		{
			Text text = new Text("ERROR");
			text.setStyle("-fx-fill: red;-fx-font-weight:bold;");
			text.setFont(Font.font("Consolas", 13));
			return text;
		}
		else if (prefix == SUCCESS)
		{
			Text text = new Text("SUCCESS");
			text.setStyle("-fx-fill: forestgreen;-fx-font-weight:bold;");
			text.setFont(Font.font("Consolas", 13));
			return text;
		}
		return new Text("");
	}

	public void clear() {
		console.getChildren().clear();
	}

	public enum Prefix {
		ERROR,
		WARNING,
		INFO,
		SUCCESS,
		NONE
	}
}

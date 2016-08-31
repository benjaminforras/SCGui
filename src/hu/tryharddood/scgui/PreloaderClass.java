package hu.tryharddood.scgui;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Random;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 30..
 ****************************************************/
public class PreloaderClass extends Preloader {
	private Stage _preloaderStage;

	//Thanks internet :D
	private String[] _randomMessages = {"Locating the required gigapixels to render...",
	                                    "Spinning up the hamster...",
	                                    "Shovelling coal into the server...",
	                                    "Programming the flux capacitor",
	                                    "Adding randomly mispeled words into text",
	                                    "Ensuring everything works perfektly",
	                                    "640K ought to be enough for anybody",
	                                    "Please wait the architects are still drafting",
	                                    "Please wait the bits are breeding",
	                                    "Please wait we're building the buildings as fast as we can",
	                                    "Please wait would you prefer chicken, steak, or tofu?",
	                                    "Please wait pay no attention to the man behind the curtain",
	                                    "Please wait and enjoy the elevator music",
	                                    "Please wait while the little elves draw your map",
	                                    "Please wait a few bits tried to escape, but we caught them",
	                                    "Please wait and dream of faster computers",
	                                    "Please wait would you like fries with that?",
	                                    "Please wait checking the gravitational constant in your locale",
	                                    "Please wait go ahead -- hold your breath",
	                                    "Please wait at least you're not on hold",
	                                    "Please wait hum something loud while others stare",
	                                    "Please wait you're not in Kansas any more",
	                                    "Please wait the server is powered by a lemon and two electrodes",
	                                    "Please wait we love you just the way you are",
	                                    "Please wait while a larger software vendor in Seattle takes over the world",
	                                    "Please wait we're testing your patience",
	                                    "Please wait as if you had any other choice",
	                                    "Please wait take a moment to sign up for our lovely prizes",
	                                    "Please wait don't think of purple hippos",
	                                    "Please wait follow the white rabbit",
	                                    "Please wait why don't you order a sandwich?",
	                                    "Please wait while the satellite moves into position",
	                                    "Please wait the bits are flowing slowly today",
	                                    "Please wait dig on the 'X' for buried treasure... ARRR!",
	                                    "Please wait it's still faster than you could draw it",
	                                    "Happy Elf and Sad Elf are talking about your data. Please wait.",
	                                    "All the relevant elves are on break. Please wait.",
	                                    "Your underwear has conflicted our DB. Please change daily.",
	                                    "Generating next funny line...",
	                                    "Entertaining you while you wait...",
	                                    "Improving your reading skills...",
	                                    "Dividing eternity by zero, please be patient...",
	                                    "Just stalling to simulate activity...",
	                                    "Adding random changes to your data...",
	                                    "Waiting for approval from Bill Gates..."
	};

	@Override
	public void start(Stage primaryStage) throws Exception {
		this._preloaderStage = primaryStage;

		primaryStage.initStyle(StageStyle.UNDECORATED);

		VBox vb = new VBox();
		vb.setPadding(new Insets(25, 50, 25, 50));
		vb.setSpacing(10);
		vb.setAlignment(Pos.CENTER);

		JFXSpinner spinner = new JFXSpinner();
		spinner.setRadius(30);
		vb.getChildren().addAll(spinner);

		Text text = new Text(getRandomText());
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFont(Font.font("System", 14));
		vb.getChildren().add(text);

		Scene scene = new Scene(vb);
		primaryStage.setScene(scene);
		primaryStage.show();

		vb.requestFocus();
	}

	@Override
	public void handleStateChangeNotification(javafx.application.Preloader.StateChangeNotification stateChangeNotification) {
		if (stateChangeNotification.getType() == javafx.application.Preloader.StateChangeNotification.Type.BEFORE_START)
		{
			_preloaderStage.hide();
		}
	}

	private String getRandomText() {
		int idx = new Random().nextInt(_randomMessages.length);
		return _randomMessages[idx];
	}
}

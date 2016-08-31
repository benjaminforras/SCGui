package hu.tryharddood.scgui;

import com.sun.javafx.application.LauncherImpl;
import hu.tryharddood.scgui.Controllers.MainController;
import hu.tryharddood.scgui.Logger.Console;
import hu.tryharddood.scgui.Settings.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class SCGui extends Application {

	public static Stage mainStage; //:)

	public static String VERSION = "1.0-SNAPSHOT";

	public static MainController mainController;
	public static Settings       settings;
	public static Console        logger;

	public static String hostIP;
	public static Integer socketPort = 7130;

	public static DatagramSocket datagramSocket;
	public static DatagramPacket datagramPacket;
	public static byte[] buffer = new byte[1024];

	public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	public static List<ScheduledFuture<?>> runningTasks    = new ArrayList<>();

	public static void main(String[] args) throws InterruptedException {
		LauncherImpl.launchApplication(SCGui.class, PreloaderClass.class, args);
		//launch(args);
	}

	public static void showAdminAlert(String player, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Admin Notification!");
		alert.setHeaderText("A player called for an admin using the !admin command");
		alert.setContentText("Player name: " + player + "\n" + "Message: " + message);

		alert.showAndWait();
	}

	public static Console getLogger() {
		return logger;
	}

	private static boolean available(int port) {
		ServerSocket   ss = null;
		DatagramSocket ds = null;
		try
		{
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException ignored)
		{
		} finally
		{
			if (ds != null)
			{
				ds.close();
			}

			if (ss != null)
			{
				try
				{
					ss.close();
				} catch (IOException e)
				{
				/* should not be thrown */
				}
			}
		}

		return false;
	}

	public static void showErrorDialog(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error!");
		alert.setHeaderText("An error occurred");
		alert.setContentText(message);

		alert.showAndWait();
	}

	public static void showSuccessDialog(String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Success!");
		alert.setHeaderText(message);
		//alert.setContentText(message);

		alert.showAndWait();
	}

	@Override
	public void init() throws Exception {
		settings = new Settings();
		settings.load();

		socketPort = getFreePort();

		datagramSocket = new DatagramSocket(SCGui.socketPort);
		datagramPacket = new DatagramPacket(buffer, buffer.length);
		datagramSocket.setReuseAddress(true);

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/hu/tryharddood/scgui/Resources/Main.fxml"));
		Parent root = loader.load();

		mainController = loader.getController();
		mainController.setHostServices(getHostServices());
		mainController.setMainInstance(this);

		Platform.runLater(() ->
		{
			Stage stage = new Stage();

			stage.initStyle(StageStyle.UNDECORATED);
			stage.getIcons().add(new Image(getClass().getResource("/hu/tryharddood/scgui/Resources/icon.png").toString()));

			stage.setTitle("SteamCondenserGUI");
			stage.setScene(new Scene(root));
			stage.show();

			stage.setOnCloseRequest(t -> handleClose());

			mainStage = stage;
		});

		URL            ipURL = new URL("http://checkip.amazonaws.com");
		BufferedReader in    = new BufferedReader(new InputStreamReader(ipURL.openStream()));
		hostIP = in.readLine();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

	}

	public void stopServices() {
		runningTasks.stream().filter(task -> !task.isCancelled()).forEach(task -> task.cancel(true));
	}

	public void handleClose() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm");
		alert.setHeaderText("Are you sure you want to close this application?");
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK)
		{
			runningTasks.stream().filter(task -> !task.isCancelled()).forEach(task -> task.cancel(true));

			executorService.shutdownNow();

			Platform.exit();
			System.exit(0);
		}
	}

	private int getFreePort() {

		for (int i = 7100; i < 7200; i++)
		{
			if (available(i))
			{
				return i;
			}
		}

		for (int i = 30572; i < 310000; i++)
		{
			if (available(i))
			{
				return i;
			}
		}
		return 7130;
	}
}

package hu.tryharddood.scgui.Controllers;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import hu.tryharddood.scgui.Jobs.PlayersTask;
import hu.tryharddood.scgui.Jobs.RconTask;
import hu.tryharddood.scgui.Jobs.ServerInformationTask;
import hu.tryharddood.scgui.Jobs.ServerThread;
import hu.tryharddood.scgui.Logger.Console;
import hu.tryharddood.scgui.Objects.Player;
import hu.tryharddood.scgui.Objects.Server;
import hu.tryharddood.scgui.SCGui;
import hu.tryharddood.scgui.TextUtils;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;
import org.ini4j.Ini;

import java.awt.*;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static hu.tryharddood.scgui.Logger.Console.Prefix.*;
import static hu.tryharddood.scgui.SCGui.VERSION;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 29..
 ****************************************************/
public class MainController {
	/**
	 * Menu
	 */
	@FXML public JFXButton           _menuMinimize;
	@FXML public JFXButton           _menuMaximize;
	@FXML public JFXButton           _menuClose;
	@FXML public Label               _menuHeaderText;
	/**
	 * Console
	 */
	@FXML public ScrollPane          _consoleScrollPane;
	@FXML public JFXToggleButton     _consoleAutoScrollToggle;
	@FXML public TextFlow            _consoleTextFlow;
	@FXML public JFXTextField        _consoleTextField;
	@FXML public JFXButton           _consoleButton;
	/**
	 * ActionButtons
	 */
	@FXML public JFXButton           _actionStartStopButton;
	/**
	 * Settings
	 */
	@FXML public JFXTextField        _settingsTextField;
	@FXML public JFXPasswordField    _settingsPasswordField;
	@FXML public JFXCheckBox         _settingsSavePassword;
	@FXML public JFXCheckBox         _settingsShowAdminAlerts;
	@FXML public JFXButton           _settingsSaveButton;
	@FXML public JFXComboBox<String> _settingsServerType;
	@FXML public JFXComboBox<String> _settingsLoadServerBox;
	@FXML public JFXButton           _settingsLoadServerButton;
	@FXML public Label               _settingsLoadServerLabel;
	@FXML public JFXPasswordField    _settingsLoadPasswordField;
	@FXML public JFXButton           _manageServersButton;
	/**
	 * Tab Management
	 */
	@FXML public JFXTabPane          _elementTabPane;
	@FXML public Tab                 _elementTabConsole;
	/**
	 * Players
	 */
	@FXML public TableView<Player>   _playersTableView;
	@FXML public TableColumn         _playersTableColumnUserID;
	@FXML public TableColumn         _playersTableColumnName;
	@FXML public TableColumn         _playersTableColumnSteamID;
	@FXML public TableColumn         _playersTableColumnIPAddress;
	@FXML public TableColumn         _playersTableColumnRate;
	@FXML public TableColumn         _playersTableColumnConnected;
	@FXML public TableColumn         _playersTableColumnPing;
	/**
	 * Server information
	 */
	@FXML public ImageView           _serverMapImage;
	@FXML public Label               _informationServerName;
	@FXML public Label               _informationServerPort;
	@FXML public Label               _informationServerPlayers;
	@FXML public Label               _informationServerIP;
	@FXML public Label               _informationServerMapName;
	@FXML public Text                _aboutVersion;
	/**
	 * Misc
	 */
	private      SCGui               mainInstance;
	private      HostServices        hostServices;
	private      Server              server;

	/**
	 * Window management
	 */
	private double                 xOffset  = 0;
	private double                 yOffset  = 0;
	/**
	 * Icons
	 */
	private MaterialDesignIconView START    = new MaterialDesignIconView(MaterialDesignIcon.PLAY);
	private MaterialDesignIconView STOP     = new MaterialDesignIconView(MaterialDesignIcon.STOP);
	private MaterialDesignIconView MAXIMIZE = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_MAXIMIZE);
	private MaterialDesignIconView RESTORE  = new MaterialDesignIconView(MaterialDesignIcon.WINDOW_RESTORE);

	private Ini ini = SCGui.settings.getIniConfig();

	public void initialize() {
		SCGui.logger = new Console(_consoleTextFlow);

		_menuHeaderText.setText(" SteamCondenser GUI - " + VERSION);
		_aboutVersion.setText("Version: " + VERSION);

		START.setGlyphSize(16);
		STOP.setGlyphSize(16);
		MAXIMIZE.setGlyphSize(16);
		RESTORE.setGlyphSize(16);

		updateLoadSettings();

		_settingsServerType.getItems().addAll("SourceServer");
		_settingsServerType.getItems().addAll("GoldSrcServer");

		{
			_playersTableView.setOnMouseClicked(event -> _playersTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE));

			_playersTableView.setRowFactory(tableView ->
			{
				final TableRow<Player> row          = new TableRow<>();
				final ContextMenu      contextMenu  = new ContextMenu();

				final MenuItem         kickMenuItem = new MenuItem("Kick player");
				final MenuItem         banMenuItem  = new MenuItem("Ban player");


				kickMenuItem.setOnAction(event ->
				{
					if(server == null)
					{
						return;
					}

					Player player = row.getItem();

					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setTitle("Kick player");
					alert.setHeaderText("Are you sure?");
					alert.setContentText("Kick player: " + player.getName() + "?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK)
					{
						player.kickPlayer(server);
					}
				});

				banMenuItem.setOnAction(event ->
				{
					if(server == null)
					{
						return;
					}

					Player player = row.getItem();

					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setTitle("Ban player");
					alert.setHeaderText("Are you sure?");
					alert.setContentText("Ban player: " + player.getName() + "?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK)
					{
						TextInputDialog dialog = new TextInputDialog();
						dialog.setTitle("Ban player");
						dialog.setHeaderText("Ban player: " + player.getName());
						dialog.setContentText("Please enter the ban lenght (if value is 0 means permanently )");

						Optional<String> banLenght = dialog.showAndWait();
						if (result.isPresent())
						{
							player.banPlayer(server, banLenght.get());
						}
					}
				});
				contextMenu.getItems().addAll(kickMenuItem, banMenuItem);

				row.contextMenuProperty().bind(
						Bindings.when(row.emptyProperty())
								.then((ContextMenu) null)
								.otherwise(contextMenu)
				);
				return row;
			});


			_playersTableColumnUserID.setCellValueFactory(
					new PropertyValueFactory<>("userid")
			);
			_playersTableColumnName.setCellValueFactory(
					new PropertyValueFactory<>("name")
			);
			_playersTableColumnSteamID.setCellValueFactory(
					new PropertyValueFactory<>("steamid")
			);
			_playersTableColumnIPAddress.setCellValueFactory(
					new PropertyValueFactory<>("ip")
			);
			_playersTableColumnRate.setCellValueFactory(
					new PropertyValueFactory<>("rate")
			);
			_playersTableColumnConnected.setCellValueFactory(
					new PropertyValueFactory<>("connect")
			);
			_playersTableColumnPing.setCellValueFactory(
					new PropertyValueFactory<>("ping")
			);
		}

		_consoleTextFlow.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
		{
			if (!_consoleAutoScrollToggle.isSelected()) return;
			_consoleScrollPane.layout();
			_consoleScrollPane.setVvalue(1.0d);
		});

		// Information pane styles
		{
			_informationServerName.textProperty().addListener((observable, oldValue, newValue) -> _informationServerName.setPrefWidth(TextUtils.computeTextWidth(_informationServerName.getFont(), _informationServerName.getText(), 0.0D) + 10));
			_informationServerIP.textProperty().addListener((observable, oldValue, newValue) -> _informationServerIP.setPrefWidth(TextUtils.computeTextWidth(_informationServerIP.getFont(), _informationServerIP.getText(), 0.0D) + 10));
			_informationServerPort.textProperty().addListener((observable, oldValue, newValue) -> _informationServerPort.setPrefWidth(TextUtils.computeTextWidth(_informationServerPort.getFont(), _informationServerPort.getText(), 0.0D) + 10));
			_informationServerPlayers.textProperty().addListener((observable, oldValue, newValue) -> _informationServerPlayers.setPrefWidth(TextUtils.computeTextWidth(_informationServerPlayers.getFont(), _informationServerPlayers.getText(), 0.0D) + 10));
			_informationServerMapName.textProperty().addListener((observable, oldValue, newValue) -> _informationServerMapName.setPrefWidth(TextUtils.computeTextWidth(_informationServerMapName.getFont(), _informationServerMapName.getText(), 0.0D) + 10));
		}

		//Style
		{
			ImageView imageView = new ImageView(new Image(SCGui.class.getResource("/hu/tryharddood/scgui/Resources/icon.png").toString()));
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);

			_menuHeaderText.setGraphic(imageView);
			_menuHeaderText.setCursor(Cursor.MOVE);

			_menuHeaderText.setTooltip(new Tooltip("Icon made by Lokas Software"));

			_menuHeaderText.setOnMousePressed(event ->
			{
				xOffset = _menuHeaderText.getScene().getWindow().getX() - event.getScreenX();
				yOffset = _menuHeaderText.getScene().getWindow().getY() - event.getScreenY();
			});

			_menuHeaderText.setOnMouseDragged(event ->
			{
				_menuHeaderText.getScene().getWindow().setX(event.getScreenX() + xOffset);
				_menuHeaderText.getScene().getWindow().setY(event.getScreenY() + yOffset);
			});
		}

		_consoleTextField.setDisable(true);
	}


	public void onWindowManagement(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(_menuMinimize))
		{
			Stage stage = (Stage) _menuMinimize.getScene().getWindow();
			stage.setIconified(true);
		}
		else if (actionEvent.getSource().equals(_menuMaximize))
		{
			Stage stage = (Stage) _menuMaximize.getScene().getWindow();
			if (stage.isMaximized())
			{
				_menuMaximize.setGraphic(MAXIMIZE);
				stage.setMaximized(false);
			}
			else
			{
				_menuMaximize.setGraphic(RESTORE);
				stage.setMaximized(true);
			}
		}
		else if (actionEvent.getSource().equals(_menuClose))
		{
			getMainInstance().handleClose();
		}
	}

	public HostServices getHostServices() {
		return hostServices;
	}

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	public SCGui getMainInstance() {
		return mainInstance;
	}

	public void setMainInstance(SCGui mainInstance) {
		this.mainInstance = mainInstance;
	}

	public void onSaveSettingsEvent(ActionEvent actionEvent) {

		List<String> errors = new ArrayList<>();
		if (_settingsTextField.getLength() == 0)
		{
			errors.add("Server Address field is empty.");
		}

		if (_settingsServerType.getSelectionModel().getSelectedItem() == null)
		{
			errors.add("You did not select the server's type.");
		}

		if (_settingsSavePassword.isSelected())
		{
			if (_settingsPasswordField.getLength() == 0)
			{
				errors.add("You did not enter the server's RCON password.");
			}
		}

		if (!errors.isEmpty())
		{
			Toolkit.getDefaultToolkit().beep();
			SCGui.showErrorDialog(String.join("\n", errors));
			SCGui.getLogger().println(ERROR, "Can't save the settings with errors. Please correct them.");
			return;
		}

		if (ini.get(_settingsTextField.getText()) == null)
		{
			ini.add(_settingsTextField.getText());
			ini.put(_settingsTextField.getText(), "ServerAddress", _settingsTextField.getText());
			ini.put(_settingsTextField.getText(), "SavePassword", _settingsSavePassword.isSelected());
			if (_settingsSavePassword.isSelected())
				ini.put(_settingsTextField.getText(), "RconPassword", new String(Base64.encodeBase64(_settingsPasswordField.getText().getBytes())));
			ini.put(_settingsTextField.getText(), "AdminNotifications", _settingsShowAdminAlerts.isSelected());
			ini.put(_settingsTextField.getText(), "ServerType", _settingsServerType.getSelectionModel().getSelectedItem());

			SCGui.settings.save();
			SCGui.getLogger().println(SUCCESS, "Settings saved!");
		}
		else
		{
			Toolkit.getDefaultToolkit().beep();
			SCGui.showErrorDialog(String.join("\n", errors));
			SCGui.getLogger().println(ERROR, "Can't save the settings with errors. Please correct them.");
		}
		updateLoadSettings();
	}

	public void onStartStopButtonPressedEvent(ActionEvent actionEvent) {
		if (server == null)
		{
			SCGui.getLogger().println(ERROR, "No server were loaded. Please enter a new or load an existing one.");
			_elementTabPane.getSelectionModel().select(_elementTabConsole);
			return;
		}

		if (_actionStartStopButton.getText().contains("Start"))
		{
			SCGui.getLogger().clear();

			SCGui.getLogger().println(INFO, "Starting..");
			Platform.runLater(() -> setSwitchStatus(Status.STARTED));

			_actionStartStopButton.setDisable(true);
			_elementTabPane.getSelectionModel().select(_elementTabConsole);

			new Thread("VerifyDataThread") {
				@Override
				public void run() {
					try
					{
						server.init();
					} catch (SteamCondenserException | TimeoutException e)
					{
						Platform.runLater(() -> setSwitchStatus(Status.STOPPED));

						SCGui.getLogger().println(ERROR, "Timed out! Please try again. ( Maybe wrong server type? )");
						e.printStackTrace();
						return;
					}

					if (server != null)
					{
						try
						{
							server.rconAuth();
						} catch (TimeoutException | SteamCondenserException e)
						{
							Platform.runLater(() -> setSwitchStatus(Status.STOPPED));

							SCGui.getLogger().println(ERROR, "Timed out! Please try again. ( Maybe wrong server type? )");
							e.printStackTrace();
							return;
						} finally
						{
							try
							{
								server.rconExec("echo INFO: RemoteConsole connected!");
							} catch (TimeoutException | SteamCondenserException e)
							{
								Platform.runLater(() -> setSwitchStatus(Status.STOPPED));

								SCGui.getLogger().println(ERROR, e.getMessage());
								e.printStackTrace();
								return;
							}
						}
					}

					SCGui.getLogger().println(INFO, "RCON init ok");
					try
					{
						server.rconExec("log on");
						server.rconExec("log on");
						server.rconExec("sv_rcon_whitelist_address \"" + SCGui.hostIP + "\"");
						server.rconExec("mp_logdetail 3");
						server.rconExec("logaddress_del " + SCGui.hostIP + ":" + SCGui.socketPort);
						server.rconExec("logaddress_add " + SCGui.hostIP + ":" + SCGui.socketPort);
					} catch (TimeoutException | SteamCondenserException e)
					{
						e.printStackTrace();
						SCGui.getLogger().println(ERROR, e.getMessage());
						SCGui.getLogger().println(ERROR, "Error executing rcon commands.");
					}
					SCGui.getLogger().println("- RCON connection OK");

					_consoleTextField.setDisable(false);

					try
					{
						SCGui.runningTasks.add(SCGui.executorService.scheduleAtFixedRate(new ServerThread(server), 0, 10, TimeUnit.MILLISECONDS));
					} catch (SocketException e)
					{
						SCGui.getLogger().println(ERROR, e.getMessage());
					}

					SCGui.runningTasks.add(SCGui.executorService.scheduleAtFixedRate(new PlayersTask(server), 1, 10, TimeUnit.SECONDS));
					SCGui.runningTasks.add(SCGui.executorService.scheduleAtFixedRate(new ServerInformationTask(server), 2, 15, TimeUnit.SECONDS));
					SCGui.runningTasks.add(SCGui.executorService.scheduleAtFixedRate(new RconTask(server), 120, 120, TimeUnit.SECONDS));
					_actionStartStopButton.setDisable(false);
				}

			}.start();
		}
		else
		{
			Platform.runLater(() -> setSwitchStatus(Status.STOPPED));

			SCGui.getLogger().println(INFO, "Stopping...");
			SCGui.getLogger().println(Console.Prefix.SUCCESS, "Successfully stopped.");

			getMainInstance().stopServices();
		}
	}


	private void setSwitchStatus(Status currentStatus) {
		if (currentStatus == Status.STARTED)
		{
			_actionStartStopButton.setText(" Stop");
			_actionStartStopButton.setGraphic(STOP);

			_settingsTextField.setDisable(true);
			_settingsPasswordField.setDisable(true);
			_settingsShowAdminAlerts.setDisable(true);
			_settingsSavePassword.setDisable(true);
			_settingsSaveButton.setDisable(true);
			_settingsServerType.setDisable(true);

			_settingsLoadServerBox.setDisable(true);
			_settingsLoadPasswordField.setDisable(true);
			_settingsLoadServerButton.setDisable(true);

			_manageServersButton.setDisable(true);
		}
		else
		{
			_actionStartStopButton.setText(" Start");
			_actionStartStopButton.setGraphic(START);

			_settingsTextField.setDisable(false);
			_settingsPasswordField.setDisable(false);
			_settingsShowAdminAlerts.setDisable(false);
			_settingsSavePassword.setDisable(false);
			_settingsSaveButton.setDisable(false);
			_settingsServerType.setDisable(false);

			_settingsLoadServerBox.setDisable(false);
			_settingsLoadPasswordField.setDisable(false);
			_settingsLoadServerButton.setDisable(false);

			_consoleTextField.setDisable(true);
			_actionStartStopButton.setDisable(false);

			_manageServersButton.setDisable(false);
		}
	}


	public void onSendConsoleCommandEvent(ActionEvent actionEvent) {
		if (_consoleTextField.getLength() == 0)
		{
			return;
		}

		SCGui.getLogger().println(INFO, "Trying to send command \"" + _consoleTextField.getText() + "\" to " + server.getIPAddress());

		String response = null;
		try
		{
			response = server.rconExec(_consoleTextField.getText());
		} catch (TimeoutException | SteamCondenserException e)
		{
			SCGui.getLogger().println(ERROR, "Error sending command \"" + _consoleTextField.getText() + "\"");
		}

		if (response != null)
			SCGui.getLogger().println(response);

		_consoleTextField.clear();
	}

	public void onClearConsoleActionEvent(ActionEvent actionEvent) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm");
		alert.setHeaderText("Are you sure you want to clear the console?");
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK)
		{
			SCGui.getLogger().clear();
		}
	}

	public void jumpToLink(ActionEvent actionEvent) {
		Hyperlink link = (Hyperlink) actionEvent.getSource();
		getHostServices().showDocument(link.getText());
	}

	public void onLoadSettingsEvent(ActionEvent actionEvent) {
		Ini.Section section = ini.get(_settingsLoadServerBox.getValue());

		if (section == null)
		{
			SCGui.showErrorDialog("You can't load a not existing server!\nPlease select one from the list.");
			return;
		}

		if (!Boolean.valueOf(section.get("SavePassword", "null")))
		{
			if (_settingsLoadPasswordField.getLength() == 0)
			{
				SCGui.showErrorDialog("Please enter the server's RCON password.");
				return;
			}
		}

		SCGui.getLogger().println("Selected server: " + _settingsLoadServerBox.getValue());
		SCGui.getLogger().println("=========== [LOADING] ===========");
		SCGui.getLogger().println("Loaded server address: " + section.get("ServerAddress"));
		SCGui.getLogger().println("Loaded server rcon password (encrypted): " + section.get("RconPassword", "notsaved"));
		SCGui.getLogger().println("Loaded server save password: " + section.get("SavePassword", "null"));
		SCGui.getLogger().println("Loaded server admin notifications: " + section.get("AdminNotifications", "null"));
		SCGui.getLogger().println("========= [End of LOADING] =========");

		server = new Server(section.get("ServerAddress"), new String(Base64.decodeBase64(section.get("RconPassword", "").getBytes())), Server.ServerType.valueOf(section.get("ServerType", "SourceServer")), Boolean.valueOf(section.get("SavePassword", "null")), Boolean.valueOf(section.get("AdminNotifications", "null")));
		if (!server.getSavePassword())
		{
			server.setRconPassword(_settingsLoadPasswordField.getText());
		}
		SCGui.showSuccessDialog("You have successfully loaded your server!\nPress the start button to start :P");
		_elementTabPane.getSelectionModel().select(_elementTabConsole);
	}

	public void updateLoadSettings() {
		_settingsLoadServerBox.getSelectionModel().clearSelection();
		_settingsLoadServerBox.setItems(FXCollections.observableArrayList());

		if (ini.keySet().size() == 0)
		{
			_settingsLoadServerBox.setDisable(true);
			_settingsLoadServerButton.setDisable(true);

			_settingsLoadServerLabel.setText("Sorry! No servers were found.");
		}
		else
		{
			_settingsLoadServerBox.setDisable(false);
			_settingsLoadServerButton.setDisable(false);

			_settingsLoadServerLabel.setText("Select server");
		}

		for (String key : ini.keySet())
		{
			_settingsLoadServerBox.getItems().add(key);
		}

		_settingsLoadServerBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
		{
			if ((_settingsLoadServerBox.getItems().size() - 1) > newValue.intValue())
				return;

			Ini.Section section = ini.get(_settingsLoadServerBox.getItems().get((Integer) newValue));

			if (Boolean.valueOf(section.get("SavePassword", "null")) == null || !Boolean.valueOf(section.get("SavePassword", "null")))
			{
				_settingsLoadPasswordField.setVisible(true);
				_settingsLoadPasswordField.setDisable(false);
			}
			else
			{
				_settingsLoadPasswordField.setVisible(false);
				_settingsLoadServerButton.setDisable(false);
			}
		});
	}

	public void onManagePaneShow(ActionEvent actionEvent) throws IOException {
		Parent root = FXMLLoader.load(SCGui.class.getResource("/hu/tryharddood/scgui/Resources/ManageServers.fxml"));

		Stage stage = new Stage();
		stage.setTitle("Manage servers");
		stage.setScene(new Scene(root));

		stage.getIcons().add(new javafx.scene.image.Image(SCGui.class.getResource("/hu/tryharddood/scgui/Resources/icon.png").toString()));

		stage.initOwner(SCGui.mainStage);
		stage.initModality(Modality.WINDOW_MODAL);

		stage.show();
	}

	private enum Status {
		STARTED, STOPPED
	}
}

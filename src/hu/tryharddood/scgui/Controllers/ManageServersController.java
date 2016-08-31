package hu.tryharddood.scgui.Controllers;

import com.jfoenix.controls.*;
import hu.tryharddood.scgui.Logger.Console;
import hu.tryharddood.scgui.SCGui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;
import org.ini4j.Ini;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static hu.tryharddood.scgui.Logger.Console.Prefix.ERROR;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 30..
 ****************************************************/
public class ManageServersController {

	@FXML public JFXComboBox<String> _manageSelectServer;
	@FXML public JFXPasswordField    _manageRconPassword;
	@FXML public JFXTextField        _manageServerAddress;
	@FXML public JFXButton           _manageSaveButton;
	@FXML public JFXCheckBox         _manageSavePassword;
	@FXML public JFXCheckBox         _manageNotifyAdmin;
	@FXML public Label               _manageServerLabel;
	@FXML public JFXComboBox<String> _manageServerType;

	private Ini ini = SCGui.settings.getIniConfig();

	public void initialize() {
		if (ini.keySet().size() == 0)
		{
			_manageSelectServer.setDisable(true);
			_manageSaveButton.setDisable(true);

			_manageServerLabel.setText("Sorry! No servers were found.");
		}
		else
		{
			_manageSelectServer.setDisable(false);
			_manageSaveButton.setDisable(false);

			_manageServerLabel.setText("Select server");
		}

		for (String key : ini.keySet())
		{
			_manageSelectServer.getItems().add(key);
		}

		_manageServerType.getItems().addAll("SourceServer");
		_manageServerType.getItems().addAll("GoldSrcServer");

		_manageSelectServer.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
		{
			Ini.Section section = ini.get(_manageSelectServer.getItems().get((Integer) newValue));

			_manageServerAddress.setText(String.valueOf(section.get("ServerAddress")));
			_manageSavePassword.setSelected(Boolean.valueOf(section.get("SavePassword", "false")));
			if (_manageSavePassword.isSelected())
				_manageRconPassword.setText(new String(Base64.decodeBase64(String.valueOf(section.get("RconPassword", "")).getBytes())));
			_manageNotifyAdmin.setSelected(Boolean.valueOf(section.get("NotifyAdmin", "true")));

			_manageServerType.getSelectionModel().select(String.valueOf(section.get("ServerType", "SourceServer")));
		});
	}

	public void onActionSaveEvent(ActionEvent actionEvent) {
		List<String> errors = new ArrayList<>();
		if (_manageServerAddress.getLength() == 0)
		{
			errors.add("Server Address field is empty.");
		}

		if (_manageServerType.getSelectionModel().getSelectedItem() == null)
		{
			errors.add("You did not select the server's type.");
		}

		if (_manageSavePassword.isSelected())
		{
			if (_manageRconPassword.getLength() == 0)
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

		ini.put(_manageServerAddress.getText(), "ServerAddress", _manageServerAddress.getText());
		ini.put(_manageServerAddress.getText(), "SavePassword", _manageSavePassword.isSelected());
		if (_manageSavePassword.isSelected())
			ini.put(_manageServerAddress.getText(), "RconPassword", new String(Base64.encodeBase64(_manageRconPassword.getText().getBytes())));
		ini.put(_manageServerAddress.getText(), "AdminNotifications", _manageNotifyAdmin.isSelected());
		ini.put(_manageServerAddress.getText(), "ServerType", _manageServerType.getSelectionModel().getSelectedItem());

		SCGui.settings.save();
		SCGui.getLogger().println(Console.Prefix.SUCCESS, "You have successfully edited the servers!");

		SCGui.mainController.updateLoadSettings();

		Node  source = (Node) actionEvent.getSource();
		Stage stage  = (Stage) source.getScene().getWindow();
		stage.close();
	}
}

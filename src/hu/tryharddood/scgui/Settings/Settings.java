package hu.tryharddood.scgui.Settings;

import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 18..
 ****************************************************/
public class Settings {

	private File file;
	private Ini  iniConfig;

	public Settings() {
		file = new File(System.getProperty("user.home") + "/.scgui/" + "servers.ini");

		file.getParentFile().mkdirs();
		try
		{
			file.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void save() {
		try
		{
			iniConfig.store(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void load() throws IOException {
		iniConfig = new Ini(file);

		/**serverAddress = properties.getProperty("ServerAddress", "192.168.0.1:27015");

		 byte[] password = Base64.decodeBase64(properties.getProperty("RCONPassword", ""));
		 serverRconPassword = new String(password);

		 savePassword = Boolean.valueOf(properties.getProperty("SavePassword", "false"));
		 notifyAdmin = Boolean.valueOf(properties.getProperty("NotifyAdmin", "true"));
		 serverType = properties.getProperty("ServerType", "SourceServer");*/
	}

	public Ini getIniConfig() {
		return iniConfig;
	}

	/*public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
		properties.setProperty("ServerAddress", serverAddress);
	}

	public String getServerRconPassword() {
		return serverRconPassword;
	}

	public void setServerRconPassword(String serverRconPassword) {
		this.serverRconPassword = serverRconPassword;

		byte[] password = Base64.encodeBase64(serverRconPassword.getBytes());
		properties.setProperty("RCONPassword", new String(password));
	}

	public Boolean getSavePassword() {
		return savePassword;
	}

	public void setSavePassword(Boolean savePassword) {
		this.savePassword = savePassword;
		properties.setProperty("SavePassword", savePassword.toString());
	}

	public Boolean getNotifyAdmin() {
		return notifyAdmin;
	}

	public void setNotifyAdmin(Boolean notifyAdmin) {
		this.notifyAdmin = notifyAdmin;
		properties.setProperty("NotifyAdmin", notifyAdmin.toString());
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
		properties.setProperty("ServerType", serverType);
	}*/
}

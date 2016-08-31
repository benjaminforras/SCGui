package hu.tryharddood.scgui.Jobs;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import hu.tryharddood.scgui.Objects.Server;
import hu.tryharddood.scgui.SCGui;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static hu.tryharddood.scgui.Logger.Console.Prefix.INFO;
import static hu.tryharddood.scgui.SCGui.mainController;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 29..
 ****************************************************/
public class ServerInformationTask implements Runnable {

	private Server _server;

	public ServerInformationTask(Server server) {
		this._server = server;
	}

	@Override
	public void run() {
		SCGui.getLogger().println(INFO, "Fetching server information");

		if (!_server.isRconAuthenticated())
		{
			try
			{
				_server.rconAuth();
			} catch (TimeoutException | SteamCondenserException e)
			{
				SCGui.getLogger().println(e.getLocalizedMessage());
			}
		}

		try
		{
			HashMap<String, Object> data = _server.getServerInfo();

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					mainController._informationServerName.setText(data.get("serverName").toString());
					mainController._informationServerIP.setText(_server.getIPAddress().split(":")[0]);
					mainController._informationServerPort.setText(data.get("serverPort").toString());
					mainController._informationServerPlayers.setText(data.get("numberOfPlayers").toString() + "/" + data.get("maxPlayers").toString());
					mainController._informationServerMapName.setText(data.get("mapName").toString());

					Image       image;
					InputStream is = getClass().getResourceAsStream("/maps/" + data.get("gameDir") + "/" + data.get("mapName") + ".jpg");
					if (is == null)
						image = new Image(SCGui.class.getResource("/hu/tryharddood/scgui/Resources/maps/no-image-available.jpg").toString());
					else image = new Image(is);

					mainController._serverMapImage.setImage(image);
				}
			});
		} catch (SteamCondenserException | TimeoutException e)
		{
			SCGui.getLogger().println(e.getLocalizedMessage());
		}

		/* CSGO Response
		{gameId=3139621093375, dedicated=100,
				networkVersion=17, maxPlayers=32, serverName=[HUN][Da LoW ].ReVoLuTioN|===> 2016 CSGO | SLhosting.eu,
				secure=true, serverPort=27164, gameDir=csgo, operatingSystem=108, numberOfPlayers=9,
				serverTags=secure, appId=730, numberOfBots=0, passwordProtected=false,
				gameVersion=1.35.4.6, gameDescription=Counter-Strike: Global Offensive, mapName=cs_italy}		}
			*/
	}
}

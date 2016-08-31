package hu.tryharddood.scgui.Objects;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 30..
 ****************************************************/
public class Server {

	private String _ipAddress;
	private String _rconPassword;

	private ServerType _serverType;

	private Boolean _savePassword;
	private Boolean _adminNotify;

	private SourceServer  _sourceServer;
	private GoldSrcServer _goldSrcServer;
	private boolean       savePassword;

	public Server(String serverAddress, String rconPassword, ServerType serverType, Boolean savePassword, Boolean adminNotify) {
		this._ipAddress = serverAddress;
		this._rconPassword = rconPassword;
		this._serverType = serverType;
		this._savePassword = savePassword;
		this._adminNotify = adminNotify;
	}

	public void init() throws SteamCondenserException, TimeoutException {
		if (getServerType() == ServerType.SourceServer)
		{
			_sourceServer = new SourceServer(getIPAddress().split(":")[0], Integer.valueOf(getIPAddress().split(":")[1]));
			_sourceServer.initSocket();
			_sourceServer.initialize();
		}
		else if (getServerType() == ServerType.GoldSrcServer)
		{
			_goldSrcServer = new GoldSrcServer(getIPAddress().split(":")[0], Integer.valueOf(getIPAddress().split(":")[1]));
			_goldSrcServer.initSocket();
			_goldSrcServer.initialize();
		}
		else if (getServerType() == ServerType.Minecraft)
		{
			//TODO: Add Minecraft support
		}
		else
		{
			//TODO: Handle other types
		}
	}

	public boolean rconAuth() throws TimeoutException, SteamCondenserException {
		switch (getServerType())
		{
			case SourceServer:
			{
				return _sourceServer.rconAuth(getRconPassword());
			}
			case GoldSrcServer:
			{
				return _goldSrcServer.rconAuth(getRconPassword());
			}
		}
		return false;
	}

	public String rconExec(String command) throws TimeoutException, SteamCondenserException {
		switch (getServerType())
		{
			case SourceServer:
			{
				return _sourceServer.rconExec(command);
			}
			case GoldSrcServer:
			{
				return _goldSrcServer.rconExec(command);
			}
		}
		return null;
	}

	public ServerType getServerType() {
		return _serverType;
	}

	public String getRconPassword() {
		return _rconPassword;
	}

	public void setRconPassword(String rconPassword) {
		this._rconPassword = rconPassword;
	}

	public String getIPAddress() {
		return _ipAddress;
	}

	public boolean isRconAuthenticated() {
		switch (getServerType())
		{
			case SourceServer:
			{
				return _sourceServer.isRconAuthenticated();
			}
			case GoldSrcServer:
			{
				return _goldSrcServer.isRconAuthenticated();
			}
		}
		return false;
	}

	public HashMap<String, Object> getServerInfo() throws TimeoutException, SteamCondenserException {
		switch (getServerType())
		{
			case SourceServer:
			{
				return _sourceServer.getServerInfo();
			}
			case GoldSrcServer:
			{
				return _goldSrcServer.getServerInfo();
			}
		}
		return new HashMap<>();
	}

	public Boolean getSavePassword() {
		return _savePassword;
	}

	public Boolean getAdminNotify() {
		return _adminNotify;
	}

	public enum ServerType {
		SourceServer,
		GoldSrcServer,
		Minecraft,
		Unknown
	}
}

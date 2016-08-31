package hu.tryharddood.scgui.Jobs;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import hu.tryharddood.scgui.Logger.Console;
import hu.tryharddood.scgui.Objects.Server;
import hu.tryharddood.scgui.SCGui;

import java.util.concurrent.TimeoutException;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 19..
 ****************************************************/
public class RconTask implements Runnable {

	private Server server;

	public RconTask(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		SCGui.getLogger().println(Console.Prefix.INFO, "Keeping the RCON connection alive...");

		if (!server.isRconAuthenticated())
		{
			try
			{
				server.rconAuth();
			} catch (SteamCondenserException | TimeoutException e)
			{
				SCGui.getLogger().println(Console.Prefix.ERROR, "Couldn't keep the RCON connection alive.");
				e.printStackTrace();
			}
		}

		try
		{
			server.rconExec("echo INFO: RemoteConsole connected!");
		} catch (TimeoutException | SteamCondenserException e)
		{
			SCGui.getLogger().println(Console.Prefix.ERROR, "Couldn't keep the RCON connection alive.");
			try
			{
				SCGui.getLogger().println(Console.Prefix.INFO, "Retrying...");
				server.init();
			} catch (SteamCondenserException | TimeoutException e1)
			{
				e1.printStackTrace();
				SCGui.getLogger().println(Console.Prefix.ERROR, e.getMessage());
				SCGui.getLogger().println(Console.Prefix.ERROR, "Couldn't keep the RCON connection alive.");
			}
		}
	}
}

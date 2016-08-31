package hu.tryharddood.scgui.Jobs;

import hu.tryharddood.scgui.Logger.Console;
import hu.tryharddood.scgui.Objects.Server;
import hu.tryharddood.scgui.SCGui;

import java.awt.*;
import java.net.SocketException;
import java.util.regex.Pattern;

import static hu.tryharddood.scgui.SCGui.*;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 16..
 ****************************************************/
public class ServerThread implements Runnable {
	private Server _server;

	public ServerThread(Server server) throws SocketException {
		this._server = server;
		SCGui.getLogger().println(Console.Prefix.INFO, "Started listening on port " + SCGui.socketPort + "..");
	}

	@Override
	public void run() {
		try
		{
			datagramSocket.receive(datagramPacket);
			String msg = new String(buffer, 0, datagramPacket.getLength());
			SCGui.getLogger().println(msg.substring(5, msg.length() - 2));

			if (_server.getAdminNotify())
			{
				if (msg.contains("!calladmin") || msg.contains("!admin"))
				{
					Toolkit.getDefaultToolkit().beep();
					SCGui.showAdminAlert(Pattern.compile("\"(.*?)<").matcher(msg).group(1), Pattern.compile("\"(.*?)\"").matcher(msg).group(1));
				}
				//L 08/22/2016 - 21:53:20: "DJ.Laca.CL.[Äŕ¸„ ĹĎŕ¸¬]<2><STEAM_1:0:123104774><TERRORIST>" say "!nvg"
				// "\"(.*?)\<" - "DJ.Laca.CL.[Äŕ¸„ ĹĎŕ¸¬]<
				// ("\"(.*?)\"" - "!nvg"

			}

			datagramPacket.setLength(buffer.length);
		} catch (Exception e)
		{
			SCGui.getLogger().println(Console.Prefix.ERROR, e.getMessage());
			e.printStackTrace();
		}
	}
}

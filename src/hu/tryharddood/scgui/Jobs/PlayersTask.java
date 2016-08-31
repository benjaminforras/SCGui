package hu.tryharddood.scgui.Jobs;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import hu.tryharddood.scgui.Logger.Console;
import hu.tryharddood.scgui.Objects.Player;
import hu.tryharddood.scgui.Objects.Server;
import hu.tryharddood.scgui.SCGui;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static hu.tryharddood.scgui.SCGui.mainController;

/*****************************************************
 *              Created by TryHardDood on 2016. 08. 29..
 ****************************************************/
public class PlayersTask implements Runnable {

	private Server _server;
	private HashMap<String, Map<String, String>> playerHash = new HashMap<>();

	public PlayersTask(Server server) {
		this._server = server;
	}

	private static List<String> getPlayerStatusAttributes(String statusHeader) {
		List<String> statusAttributes = new ArrayList<String>();
		for (String attribute : statusHeader.split("\\s+"))
		{
			switch (attribute)
			{
				case "connected":
					statusAttributes.add("time");
					break;
				case "frag":
					statusAttributes.add("score");
					break;
				default:
					statusAttributes.add(attribute);
					break;
			}
		}
		return statusAttributes;
	}

	private static Map<String, String> splitPlayerStatus(List<String> attributes, String playerStatus) {
		if (!attributes.get(0).equals("userid"))
		{
			playerStatus = playerStatus.replaceAll("^\\d+ +", "");
		}
		int          firstQuote = playerStatus.indexOf('"');
		int          lastQuote  = playerStatus.lastIndexOf('"');
		List<String> tmpData    = new ArrayList<String>();
		tmpData.add(playerStatus.substring(0, firstQuote));
		tmpData.add(playerStatus.substring(firstQuote + 1, lastQuote));
		tmpData.add(playerStatus.substring(lastQuote + 1));
		List<String> data = new ArrayList<String>();
		data.addAll(Arrays.asList(tmpData.get(0).trim().split("\\s+")));
		data.add(tmpData.get(1));
		data.addAll(Arrays.asList(tmpData.get(2).trim().split("\\s+")));
		data.remove("");
		if (attributes.size() > data.size() && attributes.contains("state"))
		{
			data.add(3, null);
			data.add(3, null);
			data.add(3, null);
		}
		else if (attributes.size() < data.size()) { data.remove(1); }
		Map<String, String> playerData = new HashMap<String, String>();
		for (int i = 0; i < data.size(); i++) { playerData.put(attributes.get(i), data.get(i)); }
		return playerData;
	}

	@Override
	public void run() {
		SCGui.getLogger().println(Console.Prefix.INFO, "Fetching players");


		try
		{
			if (!_server.isRconAuthenticated())
			{
				_server.rconAuth();
			}

			getPlayers();
		} catch (TimeoutException | SteamCondenserException e)
		{
			SCGui.getLogger().println(e.getLocalizedMessage());
		} finally
		{
			mainController._playersTableView.getItems().clear();
			for (Map.Entry<String, Map<String, String>> entry : this.playerHash.entrySet())
			{
				mainController._playersTableView.getItems().add(new Player(Integer.valueOf(entry.getValue().get("userid")), entry.getKey(), entry.getValue().get("uniqueid"), entry.getValue().get("adr"), Integer.valueOf(entry.getValue().get("rate")), entry.getValue().get("time"), Integer.valueOf(entry.getValue().get("ping"))));
			}
		}
	}
// Response CSGO
//
// {loss=0, rate=128000, ping=43, name=-Vipera-, time=1:00:12, state=active, userid=1324, uniqueid=STEAM_1:0:52965763, adr=94.27.206.13:32734}

	private void getPlayers() throws TimeoutException, SteamCondenserException {
		List<String> players = Arrays.stream(_server.rconExec("status").split("\n")).filter(line -> line.startsWith("#") && !line.equals("#end")).map(line -> line.substring(1).trim()).collect(Collectors.toList());

		List<String> attributes = getPlayerStatusAttributes(players.remove(0));
		for (String player : players)
		{
			Map<String, String> playerData = splitPlayerStatus(attributes, player);

			String playerName = playerData.get("name");
			playerHash.put(playerName, playerData);
		}
	}
}

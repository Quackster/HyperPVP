package pvp.alexdev.org.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import pvp.alexdev.org.HyperPVP;

public class MiscUtils {


	public static String removeLastChar(String str) {
		try {
			return str.substring(0,str.length()-1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	public static String getUUID(String username) {

		String returned = grabHTML("https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + HyperPVP.getTimestamp());
		
		try {
			JSONObject value = (JSONObject)new JSONParser().parse(returned);
			
			StringBuffer sb = new StringBuffer(value.get("id").toString());
			sb.insert(8, "-");

			sb = new StringBuffer(sb.toString());
			sb.insert(13, "-");

			sb = new StringBuffer(sb.toString());
			sb.insert(18, "-");

			sb = new StringBuffer(sb.toString());
			sb.insert(23, "-");

			return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
		/*String[] usernames = new String[] { username } ;

		HttpProfileRepository repository = new HttpProfileRepository();
		int i = 0;

		while (usernames.length > i) {
			Profile[] profiles = repository.findProfilesByCriteria(new ProfileCriteria[] { new ProfileCriteria(username, "minecraft") });

			if (profiles.length < 1) {
				return "";
			}

			String uuid = profiles[0].getId();

			// 3ba2c13c-4e784-35fa-79e-54a1399a2a0c
			//3ba2c13c 4e78 435f a79e 54a1399a2a0c
			//069a79f4-44e9-4726-a5be-fca90e38aaf5

			StringBuffer sb = new StringBuffer(uuid);
			sb.insert(8, "-");

			sb = new StringBuffer(sb.toString());
			sb.insert(13, "-");

			sb = new StringBuffer(sb.toString());
			sb.insert(18, "-");

			sb = new StringBuffer(sb.toString());
			sb.insert(23, "-");

			return sb.toString();
		}
		return username;*/
	}

	public static String insertDashUUID(String uuid) {
		StringBuffer sb = new StringBuffer(uuid);
		sb.insert(8, "-");

		sb = new StringBuffer(sb.toString());
		sb.insert(13, "-");

		sb = new StringBuffer(sb.toString());
		sb.insert(18, "-");

		sb = new StringBuffer(sb.toString());
		sb.insert(23, "-");

		return sb.toString();
	}

	public static String color(String s) {
		s = s.replaceAll("&0", "§0");
		s = s.replaceAll("&1", "§1");
		s = s.replaceAll("&2", "§2");
		s = s.replaceAll("&3", "§3");
		s = s.replaceAll("&4", "§4");
		s = s.replaceAll("&5", "§5");
		s = s.replaceAll("&6", "§6");
		s = s.replaceAll("&7", "§7");
		s = s.replaceAll("&8", "§8");
		s = s.replaceAll("&9", "§9");
		s = s.replaceAll("&a", "§a");
		s = s.replaceAll("&b", "§b");
		s = s.replaceAll("&c", "§c");
		s = s.replaceAll("&d", "§d");
		s = s.replaceAll("&e", "§e");
		s = s.replaceAll("&f", "§f");
		s = s.replaceAll("&k", "§k");
		s = s.replaceAll("&l", "§l");
		s = s.replaceAll("&m", "§m");
		s = s.replaceAll("&n", "§n");
		s = s.replaceAll("&o", "§o");
		s = s.replaceAll("&r", "§r");
		s = s.replaceAll(";n", System.getProperty("line.separator"));
		//s = s.replaceAll("{r}", "\r");

		return s;
	}

	public static String grabHTML(String url) {

		String output = "";

		try {
			URL web = new URL(url);

			HttpURLConnection httpcon = (HttpURLConnection) web.openConnection(); 
			httpcon.addRequestProperty("User-Agent", "Mozilla/4.76"); 

			InputStream is = httpcon.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";

			while ((line = br.readLine()) != null) {
				output += line + System.getProperty("line.separator");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;

	}

}

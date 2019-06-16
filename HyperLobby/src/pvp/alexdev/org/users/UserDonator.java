package pvp.alexdev.org.users;

public class UserDonator {

	private String name;
	private String date;
	
	public UserDonator(String date2, String name2) {
		this.date = date2;
		this.name = name2;
	}

	public String getName() {
		return name;
	}
	
	public String getDays() {
		return date;
	}
	
}

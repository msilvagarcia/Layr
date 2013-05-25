package layr.engine.sample;

import java.util.Date;
import java.util.List;

public class Hello {
	private String world; // = "Earth"
	private World realworld;
	private Integer size;
	private List<String> countries;
	private World myWorld;

	private Date today;

	public Date getToday() {
		return today;
	}

	public void setToday(Date today) {
		this.today = today;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public String getWorld() {
		return world;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getSize() {
		return size;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}

	public List<String> getCountries() {
		return countries;
	}

	public void setRealworld(World realworld) {
		this.realworld = realworld;
	}

	public World getRealworld() {
		return realworld;
	}

	public World getMyWorld() {
		return myWorld;
	}

	public void setMyWorld(World myWorld) {
		this.myWorld = myWorld;
	}
}

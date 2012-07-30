package coffee.sample;

import java.util.ArrayList;
import java.util.Date;

import layr.annotation.Converter;

public class Hello {
	private String world; // = "Earth"
	private World realworld;
	private Integer size;
	private ArrayList<String> countries;
	private World myWorld;

	@Converter(BrazilianFormatDateConverter.class)
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

	public void setCountries(ArrayList<String> countries) {
		this.countries = countries;
	}

	public ArrayList<String> getCountries() {
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

package layr.engine.sample;

public class World {
	private String name;
	private Hello hello;
	private Long id;
	
	private String somethingBad;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Hello getHello() {
		return hello;
	}

	public void setHello(Hello hello) {
		this.hello = hello;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSomethingBad() {
		return somethingBad;
	}

	public void setSomethingBad(String somethingBad) {
		this.somethingBad = somethingBad;
	}
	
}
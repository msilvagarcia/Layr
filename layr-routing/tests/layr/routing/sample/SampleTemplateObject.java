package layr.routing.sample;

public class SampleTemplateObject {

	private String name;
	public Long age;
	@SuppressWarnings("unused")
	private Double invalid;

	public SampleTemplateObject(String name, Long age, Double invalid) {
		this.name = name;
		this.age = age;
		this.invalid = invalid;
	}

	public String getName() {
		return name;
	}
}

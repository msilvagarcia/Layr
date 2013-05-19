package layr.routing.sample;

public class Hello {

	Long pathParam;
	private Double requestParam;
	
	public Hello( Long pathParam, Double requestParam ) {
		this.pathParam = pathParam;
		this.requestParam = requestParam;
	}

	public Double getRequestParam() {
		return requestParam;
	}
}

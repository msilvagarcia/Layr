package layr.test;

import java.security.Principal;

public class PrincipalStub implements Principal {
	
	private String name;
	
	public PrincipalStub(){}
	
	public PrincipalStub(String name){
		setName(name);
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}

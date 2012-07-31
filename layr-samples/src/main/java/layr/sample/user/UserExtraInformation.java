package layr.sample.user;

import java.util.Date;

import layr.annotation.Converter;


public class UserExtraInformation {

	@Converter(DateConverter.class)
	private Date birthdate;

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	
	
}

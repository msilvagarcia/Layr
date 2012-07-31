package layr.sample.user;

public class User {

	private Long id;
	private String firstName;
	private String lastName;
	private Integer age;
	private Short gender;
	private String obs;
	private String profileName;
	private Boolean active;
	private UserExtraInformation extraInformation;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Short getGender() {
		return gender;
	}

	public void setGender(Short gender) {
		this.gender = gender;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public UserExtraInformation getExtraInformation() {
		return extraInformation;
	}

	public void setExtraInformation(UserExtraInformation extraInformation) {
		this.extraInformation = extraInformation;
	}

}

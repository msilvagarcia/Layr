package layr.sample.user;

import javax.ejb.EJB;

import layr.annotation.Action;
import layr.annotation.Parameter;
import layr.annotation.WebResource;


// Defining the root URL pattern for this resource
@WebResource(rootURL = "/user/")
// You could define a global template through 'template' annotation attribute
public class UserResource {

	// A common used entity on this resource. This will able the
	// templete to use #{user.attribute}, or to use 'user' as a redirect
	// URL pattern (see save) or action URL pattern.
	private User user;

	@EJB
	private UserService userService;

	@Action(template = "templates/user.xhtml")
	// Matches the URL /user/add
	public void add() {
	}

	@Action(template = "templates/user.xhtml",
	// Matches the URL /user/edit/4325, per example.
	pattern = "edit/#{id}")
	public void edit(
	// Yeah, it retrieves the id parameter from URL
	// pattern defined in Action annotation
			@Parameter("id") Long id) {

		setUser(userService.findById(id));
	}

	@Action(
	// After save the data, it will redirect automatically to /user/edit/4325,
	// per example. It will replace the #{user.id} with the just created id data.
	redirectTo = "/user/edit/#{user.id}")
	// Matches the URL /user/save
	public void save() {
		// The 'user' field comes to server populated with form values
		userService.create(user);
		System.out.println(user.getId());
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}

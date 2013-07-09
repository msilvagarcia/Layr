package user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import layr.api.GET;
import layr.api.POST;
import layr.api.PathParameter;
import layr.api.WebResource;
import lombok.Data;
import lombok.NoArgsConstructor;

@WebResource("issue")
public class SampleResource {

	final static Map<Integer, Issue> issues = new HashMap<Integer, SampleResource.Issue>();
	
	@GET
	public Collection<Issue> retrieveAll(){
		return issues.values();
	}
	
	@GET("{id}")
	public Issue retrieveById( @PathParameter("id") Integer id ) {
		return issues.get(id);
	}
	
	@POST("data")
	public void insertIssue( Issue issue ){
		issues.put(issues.size(), issue);
	}
	
	@Data
	@NoArgsConstructor
	final public static class Issue {
		String cause;
	}
}

package layr.sample.user;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Singleton;

@Singleton
public class UserService {

	Map<Long, User> persistence;
	AtomicInteger sequence;
	
	public UserService() {
		persistence = new HashMap<Long, User>();
		sequence = new AtomicInteger(0);
	}
	
	public void create(User user) {
		int id = sequence.incrementAndGet();
		user.setId((long)id);
		persistence.put(user.getId(), user);
	}
	
	public User findById(Long id) {
		return persistence.get(id);
	}
}

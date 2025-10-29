package it.objectmethod.demo.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import it.objectmethod.demo.spring.models.UserObject;
import it.objectmethod.demo.spring.repository.UserRepository;

@Service
public class UsersService {

	@Autowired
	private UserRepository ur;
	@Autowired
	private JwtService jwt;

	public @ResponseBody Iterable<UserObject> getAllUsers() {
		return ur.findAll();
	}

	public void clearList() {
		ur.deleteAll();
	}

	public void removeUser(Long index) {
		ur.deleteById(index);
	}

	public Object getOneUser(Long index) {
		return ur.findById(index).orElse(null);
	}

	public void editUser(Long index, String username, String password) {

		UserObject user = ur.findById(index).orElse(null);
		user.setUsername(username);
		user.setPassword(password);
		ur.save(user);

	}

	public String login(String username, String password) {
		// Hash incoming password the same way we store it, then delegate to repository
		String hashedPassword = org.springframework.util.DigestUtils.md5DigestAsHex(password.getBytes());
		UserObject user = ur.login(username, hashedPassword);
		if (user != null) {
			String token = jwt.createJWTToken(user);
			return token;
		} else {
			return null;
		}
	}

	public UserObject register(String username, String password) {
		// Check if user already exists
		UserObject existingUser = ur.register(username);
		if (existingUser != null) {
			return null;
		}

		// Create new user
		UserObject newUser = new UserObject();
		newUser.setUsername(username);
		// Hash the password using MD5 to match login query
		String hashedPassword = org.springframework.util.DigestUtils.md5DigestAsHex(password.getBytes());
		newUser.setPassword(hashedPassword);
		newUser.setRole("user");
		ur.save(newUser);

		return newUser;
	}

}

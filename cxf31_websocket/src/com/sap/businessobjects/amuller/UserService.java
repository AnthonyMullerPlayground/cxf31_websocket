package com.sap.businessobjects.amuller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.cxf.transport.websocket.WebSocketConstants;
import org.springframework.stereotype.Component;

import com.sap.businessobjects.amuller.model.User;
import com.sap.businessobjects.amuller.model.Users;

@Component
@Path("/users")
public class UserService {

	// For WebSoket handling
	private WebSocketHandler userSocket = new WebSocketHandler();
	
	private final static Users FAKE_USERS = new Users();
	static {
		User user1 = new User("1", "Anthony", "MÜLLER");
		User user3 = new User("2", "Bogdan", "RADULESCU");
		User user4 = new User("3", "Eric", "FESTINGER");
		User user5 = new User("4", "Fabien", "KOBUS");
		User user2 = new User("5", "Stéphane", "MONTAGNON");
		
		FAKE_USERS.userList.add(user1);
		FAKE_USERS.userList.add(user2);
		FAKE_USERS.userList.add(user3);
		FAKE_USERS.userList.add(user4);
		FAKE_USERS.userList.add(user5);
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Users getAll()
	{
		userSocket.sendEvent("GET /users");
		return FAKE_USERS;
	}
	
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_XML)
	public User get(@PathParam("userId") String userId)
	{
		User user = find(userId);
		userSocket.sendEvent("GET /users/" + userId);
		return user;
	}

	@PUT
	@Path("/{userId}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public User put(@PathParam("userId") String userId, User updatedUser)
	{
		User user = null;
		if (updatedUser != null) {
			user = find(userId);
			user.setFirstName(updatedUser.getFirstName());
			user.setLastName(updatedUser.getLastName());
			userSocket.sendEvent("PUT /users/" + userId);
		}

		return user;
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public User put(User newUser)
	{
		User user = null;
		if (newUser != null && newUser.getId() != null) {
			user = find(newUser.getId());
			// User must not already exist with same id
			if(user == null) {
				user = new User();
				user.setId(newUser.getId());
				user.setFirstName(newUser.getFirstName());
				user.setLastName(newUser.getLastName());
				
				userSocket.sendEvent("POST /users");
				FAKE_USERS.userList.add(user);
			}
		}

		return user;
	}
	
	@DELETE
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_XML)
	public User delete(@PathParam("userId") String userId)
	{	
		User user = find(userId);
		FAKE_USERS.userList.remove(user);		
		userSocket.sendEvent("DELETE /users/" + userId);
		return user;
	}
	
    @GET
    @Path("/monitor")
    @Produces("text/*")
    public StreamingOutput monitorCustomers(
            @HeaderParam(WebSocketConstants.DEFAULT_REQUEST_ID_KEY) String reqid) {
        final String listenerId = reqid == null ? "*" : reqid; 
        return new StreamingOutput() {
            public void write(final OutputStream output) throws IOException, WebApplicationException {
            	userSocket.addListener(listenerId, output);
            	output.write(("Subscribed at " + new java.util.Date()).getBytes());
            }
            
        };
    }

	private User find(String userId)
	{		
		List<User> users = FAKE_USERS.userList;
		
		User user = null;
		for (User crtUser : users) {
			if(crtUser.getId().equals(userId)) {
				user = crtUser;
				break;
			}
		}
		return user;
	}	
}

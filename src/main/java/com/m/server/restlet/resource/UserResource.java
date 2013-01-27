package com.m.server.restlet.resource;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.m.server.restlet.domain.UserDO;
import com.m.server.restlet.server.DBHelper;

public class UserResource extends ServerResource {
    private static final DBHelper db = DBHelper.getInstance();

    public UserResource() {

    }

    @Get
    public Representation getUserById() {
        String idStr = (String) getRequestAttributes().get("userId");
        UserDO user = db.getUser(Long.parseLong(idStr));
        if (user == null) {
            return new JsonRepresentation(new JSONObject());
        } else {
            return new JsonRepresentation(user);
        }
    }

    @Delete
    public Representation removeUserById() {
        String idStr = (String) getRequestAttributes().get("userId");
        long id = Long.parseLong(idStr);
        UserDO user = db.deleteUser(id);
        if (user == null) {
            return new JsonRepresentation(new JSONObject());
        } else {
            return new JsonRepresentation(user);
        }
    }

}

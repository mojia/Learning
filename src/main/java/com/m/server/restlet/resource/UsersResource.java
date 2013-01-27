package com.m.server.restlet.resource;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.m.server.restlet.domain.UserDO;
import com.m.server.restlet.server.DBHelper;

public class UsersResource extends ServerResource {
    private final static DBHelper db = DBHelper.getInstance();

    public UsersResource() {

    }

    /**
     * 返回users的用户列表
     * 
     * @return
     * @throws JSONException
     */
    @Get
    public Representation getUsers() throws JSONException {
        Collection<UserDO> users = db.getUsers();
        JSONArray array = new JSONArray();
        for (UserDO user : users) {
            JSONObject jo = new JSONObject(user);
            array.put(jo);
        }

        return new JsonRepresentation(array);
    }

    @Post
    public Representation addUser(Form form) {
        String name = form.getFirstValue("name");
        int age = Integer.parseInt(form.getFirstValue("age"));
        String motto = form.getFirstValue("motto");

        UserDO user = db.addUser(name, age, motto);

        return new JsonRepresentation(user);
    }

}

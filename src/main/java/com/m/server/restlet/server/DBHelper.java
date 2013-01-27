package com.m.server.restlet.server;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.m.server.restlet.domain.UserDO;

public class DBHelper {

    private static ConcurrentHashMap<Long, UserDO> repository;

    private static volatile long idGeneral = 0;
    private static DBHelper instance = new DBHelper();

    private DBHelper() {
        repository = new ConcurrentHashMap<Long, UserDO>();
    }

    public static DBHelper getInstance() {
        return instance;
    }

    public synchronized UserDO addUser(String name, int age, String motto) {
        idGeneral++;
        UserDO user = new UserDO(idGeneral, name, age, motto);
        repository.put(idGeneral, user);
        return user;
    }

    public Collection<UserDO> getUsers() {
        return repository.values();
    }

    public UserDO getUser(long id) {
        return repository.get(id);
    }

    public UserDO deleteUser(long id) {
        if (repository.contains(id)) {
            return repository.remove(id);
        } else {
            return null;
        }
    }

    public void saveUser(UserDO user) {
        repository.put(user.getId(), user);
    }

}

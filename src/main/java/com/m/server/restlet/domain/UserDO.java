package com.m.server.restlet.domain;

public class UserDO {
    private long id;
    private String name;
    private int age;
    private String motto;

    public UserDO(long id, String name, int age, String motto) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
        this.motto = motto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

}

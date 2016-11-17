package org.feedhenry.pointinghenry.model;

import java.util.List;

/**
 * Created by corinne on 11/11/16.
 */

public class Session {
    public String name;
    public User createdBy;
    public List<User> users;
    public Session(String name, User createdBy) {
        this.name = name;
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return String.format("%s, created by %s", name, createdBy.name);
    }
}

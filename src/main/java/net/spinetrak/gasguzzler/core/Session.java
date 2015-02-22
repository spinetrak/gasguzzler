package net.spinetrak.gasguzzler.core;

import java.util.UUID;

public class Session {

    private final int userid;
    private final String token;

    public Session(int userid) {
        this.userid = userid;
        this.token = UUID.randomUUID().toString().substring(0, 23);
    }

    public int getUserid() {
        return userid;
    }

    public String getToken() {
        return token;
    }
}
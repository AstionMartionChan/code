package com.rltx.truck.broker.bo;

import com.wondersgroup.cuteinfo.client.exchangeserver.usersecurty.UserToken;

import java.util.Date;

/**
 * Created by wubin on 2017/4/20.
 */
public class TokenBo {

    // token
    private UserToken token;

    // token
    private Date tokenTime;

    public UserToken getToken() {
        return token;
    }

    public void setToken(UserToken token) {
        this.token = token;
    }

    public Date getTokenTime() {
        return tokenTime;
    }

    public void setTokenTime(Date tokenTime) {
        this.tokenTime = tokenTime;
    }
}

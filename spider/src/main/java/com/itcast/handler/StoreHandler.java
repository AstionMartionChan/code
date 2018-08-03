package com.itcast.handler;

import com.itcast.po.Page;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Created by Leo_Chan on 2018/2/2.
 */
public interface StoreHandler {

    void saveDB(Page page) throws SQLException, IOException, NoSuchAlgorithmException;


}

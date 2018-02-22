package com.hadoop.hdfs.rpc;

import org.apache.hadoop.ipc.VersionedProtocol;

/**
 * Created by leochan on 2018/2/11.
 */
public interface Bizable extends VersionedProtocol {

    long versionID = 12312321L;

    String sendHeart(String content);

}

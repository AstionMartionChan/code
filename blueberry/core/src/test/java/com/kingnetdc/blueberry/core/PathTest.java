package com.kingnetdc.blueberry.core;

import com.kingnetdc.blueberry.core.io.Path;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PathTest {


    /**
     * 测试加载包内的文件
     * 忽略 "/" 符号的影响
     */
    @Test
    public void testReadResourceFile() {
        assertTrue(Path.getResourceFile("/test.txt").equals(Path.getResourceFile("test.txt")));
    }

    /**
     * 测试包内子文件夹的文件
     */
    @Test
    public void testReadResourceFileSubDirectory() {
        assertTrue(Path.getResourceFile("/subdir/subTest.txt").equals(Path.getResourceFile("subdir/subTest.txt")));
    }
}

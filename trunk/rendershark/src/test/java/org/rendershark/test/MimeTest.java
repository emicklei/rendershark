package org.rendershark.test;

import java.net.FileNameMap;
import java.net.URLConnection;

import org.junit.Test;

public class MimeTest {
    @Test
    public void testJpeg() {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        System.out.println(fileNameMap.getContentTypeFor("test.jpg"));
    }
}

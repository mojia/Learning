package com.m.server.fortest;

import org.junit.Before;
import org.junit.Test;

import com.m.server.httpclient.PowerHttpClient;

public class HttpClientForTest {

    private static final PowerHttpClient client = new PowerHttpClient();

    @Before
    public void setup() {

    }

    @Test
    public void testcase00_mars_locate_bynet_post() {
        String url = "http://192.168.2.228:8601/api/v1/location/bynet";

        client.post("/Users/wangxin09/Documents/bynet.json", url);
    }

    // @Test
    public void testcase00_mars_locate_report_post() {
        String url = "http://192.168.2.228:8601/api/v1/location/report";

        client.post("/Users/wangxin09/Documents/CellTower.json", url);
    }
}

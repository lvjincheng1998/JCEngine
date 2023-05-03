package pers.jc.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {

    public static String get(String url, Map<String, String> paramMap) {
        return request(url, paramMap, HttpGet.class);
    }

    public static String post(String url, Map<String, String> paramMap) {
        return request(url, paramMap, HttpPost.class);
    }

    private static String request(String url, Map<String, String> paramMap, Class type) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null && paramMap.size() > 0) {
                List<NameValuePair> pairs = new ArrayList<>();
                for (Map.Entry<String, String> entry : paramMap.entrySet())
                    pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                builder.setParameters(pairs);
            }
            if (type == HttpGet.class) {
                HttpGet httpGet = new HttpGet(builder.build());
                response = httpClient.execute(httpGet);
            } else if (type == HttpPost.class) {
                HttpPost httpPost = new HttpPost(builder.build());
                response = httpClient.execute(httpPost);
            }
            if(response != null && response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (httpClient != null) httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

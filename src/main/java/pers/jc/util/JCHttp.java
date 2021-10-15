package pers.jc.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JCHttp {
	
    public static String get(String httpUrl) {
        return getResult(httpUrl, null, "GET");
    }

    public static String post(String httpUrl, String param) {
        return getResult(httpUrl, param, "POST");
    }

    private static String getResult(String httpUrl, String param, String method) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            if (method.equals("GET")) {
                connection.connect();
            } else if (method.equals("POST")) {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
                outputStream = connection.getOutputStream();
                outputStream.write(param.getBytes());
            }
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuilder.append(str);
                    stringBuilder.append("\r\n");
                }
                result = stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(bufferedReader);
            close(outputStream);
            close(inputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
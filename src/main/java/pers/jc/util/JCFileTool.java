package pers.jc.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class JCFileTool {

    public void copyDir(String oldPath, String newPath) throws Exception {
        File newDir = new File(newPath);
        if (!newDir.exists()) {
            if (!newDir.mkdirs()) {
                throw new Exception("Make Dir Fail");
            }
        }
        File file = new File(oldPath);
        String[] filePath = file.list();
        for (int i = 0; i < Objects.requireNonNull(filePath).length; i++) {
            if ((new File(oldPath + File.separator + filePath[i])).isDirectory()) {
                copyDir(oldPath  + File.separator  + filePath[i], newPath  + File.separator + filePath[i]);
            }
            if (new File(oldPath  + File.separator + filePath[i]).isFile()) {
                copyFile(oldPath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
            }
        }
    }

    public void copyFile(String oldPath, String newPath) throws Exception {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        FileChannel channelIn;
        FileChannel channelOut;
        fileInputStream = new FileInputStream(oldPath);
        fileOutputStream = new FileOutputStream(newPath);
        channelIn = fileInputStream.getChannel();
        channelOut = fileOutputStream.getChannel();
        channelOut.transferFrom(channelIn, 0, channelIn.size());
        channelIn.close();
        channelOut.close();
        fileInputStream.close();
        fileOutputStream.close();
    }

    public String[] readLines(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[0]);
    }

    public String readStr(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        boolean lineFeed = false;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
            lineFeed = true;
        }
        if (lineFeed) stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        bufferedReader.close();
        return stringBuilder.toString();
    }

    public void writeStr(File file, String content) throws Exception {
        // 获取该文件的缓冲输出流
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        // 写入信息
        bufferedWriter.write(content);
        // 清空缓冲区
        bufferedWriter.flush();
        // 关闭输出流
        bufferedWriter.close();
    }
}

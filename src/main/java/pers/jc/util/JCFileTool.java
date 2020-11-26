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
        fileInputStream = new FileInputStream(new File(oldPath));
        fileOutputStream = new FileOutputStream(new File(newPath));
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

    public void writeStr(File file, String content) throws Exception {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.close();
    }
}

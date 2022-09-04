package demo.util;

import pers.jc.util.JCFileTool;

import java.io.File;
import java.util.Arrays;

public class Test_JCFileTool {

    public static void main(String[] args) throws Exception {
        String outPath = new File("").getCanonicalPath() + File.separator + "out";

        File outDir = new File(outPath);
        if (!outDir.exists() && !outDir.mkdir()) return;

        String contentToWrite = "hello\nworld\ngoodbye";
        File txtFile = new File(outPath + File.separator + "hello.txt");
        JCFileTool.writeStr(txtFile, contentToWrite);
        System.out.println("===写入成功===");

        String txtContent = JCFileTool.readStr(txtFile);
        System.out.println("===读取成功===");
        System.out.println(txtContent);

        String[] txtLines = JCFileTool.readLines(txtFile);
        System.out.println("===读取成功-每行存入数组===");
        System.out.println(Arrays.toString(txtLines));

        String copyFileOutputPath = txtFile.getPath().replace(".txt", "_copy.txt");
        JCFileTool.copyFile(txtFile.getPath(), copyFileOutputPath);
        System.out.println("===复制文件成功===");
        System.out.println("目标文件：" + copyFileOutputPath);

        File srcDir = new File(outDir + File.separator + "src_dir");
        if (!srcDir.exists() && !srcDir.mkdir()) return;
        File srcChildDir = new File(srcDir.getPath() + File.separator + "child_dir");
        if (!srcChildDir.exists() && !srcChildDir.mkdir()) return;
        File fileInSrcDir = new File(srcDir.getPath() + File.separator + "file1.txt");
        JCFileTool.writeStr(fileInSrcDir, "hello-file1");
        File fileInSrcChildDir = new File(srcChildDir.getPath() + File.separator + "file2.txt");
        JCFileTool.writeStr(fileInSrcChildDir, "hello-file2");
        System.out.println("===创建测试目录成功");
        System.out.println("目标目录：" + srcDir.getPath() + File.separator);

        String copyDirOutputPath = srcDir.getPath().replace("src_dir", "src_dir_copy");
        JCFileTool.copyDir(srcDir.getPath(), copyDirOutputPath);
        System.out.println("===复制测试目录成功");
        System.out.println("目标目录：" + copyDirOutputPath + File.separator);
    }
}

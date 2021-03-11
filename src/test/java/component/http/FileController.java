package component.http;

import io.netty.handler.codec.http.multipart.FileUpload;
import pers.jc.network.*;
import pers.jc.util.JCLogger;
import pers.jc.util.JCUtil;
import java.io.File;

@HttpComponent("/file")
public class FileController {

    //上传文件
    @HttpPost("/upload")
    public String upload(FileUpload fileUpload) {
        try {
            String catalogPath = new File("").getCanonicalPath() + File.separator + "upload";
            File catalogFile = new File(catalogPath);
            if (!catalogFile.exists()) {
                if (!catalogFile.mkdir()) {
                    throw new Exception("创建目录失败 " + catalogPath);
                }
            }
            String oldFileName = fileUpload.getFilename();
            String newFileName = JCUtil.uuid() + oldFileName.substring(oldFileName.lastIndexOf("."));
            String newFilePath = catalogPath + File.separator + newFileName;
            File newFile = new File(newFilePath);
            fileUpload.renameTo(newFile);
            JCLogger.info("保存目录", newFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "上传成功";
    }

    //访问项目下的静态资源
    @HttpGet("/getResource")
    public HttpResource getResource() {
        return new HttpResource("/project.json");
    }

    //重定向，如果前缀没有http，会自动拼接项目基础路径
    @HttpGet("/doRedirect")
    public HttpRedirect doRedirect() {
        return new HttpRedirect("http://www.baidu.com");
    }
}

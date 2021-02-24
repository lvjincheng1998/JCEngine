package component.http;

import io.netty.handler.codec.http.multipart.FileUpload;
import pers.jc.network.*;
import pers.jc.util.JCLogger;
import pers.jc.util.JCUtil;
import java.io.File;

@HttpComponent("/file")
public class FileController {

    @HttpPost("/upload")
    public String upload(FileUpload fileUpload) {
        try {
            String catalog = new File("").getCanonicalPath() + File.separator + "upload";
            File catalogFile = new File(catalog);
            if (!catalogFile.exists()) {
                if (!catalogFile.mkdir()) {
                    throw new Exception("创建目录失败 " + catalog);
                }
            }
            String oldFileName = fileUpload.getFilename();
            String newFileName = JCUtil.uuid() + oldFileName.substring(oldFileName.lastIndexOf("."));
            String newFilePath = catalog + File.separator + newFileName;
            File newFile = new File(newFilePath);
            fileUpload.renameTo(newFile);
            JCLogger.info("保存目录", newFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "上传成功";
    }

    @HttpGet("/show")
    public HttpResource show() {
        return new HttpResource("/project.json");
    }
}

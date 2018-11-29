package app.logger;

import app.fastdfs.FastDFSClient;
import app.fastdfs.FastDFSFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component("uploadService")
@Slf4j
public class UploadService {

    /**
     * 上传文件
     * path是文件路径
     * @author 周西栋
     * @date
     * @param
     * @return
     */
    public String upload(String path) throws IOException {

        File file = new File(path);

        FileInputStream input = new FileInputStream(file);

        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", input);

        UploadService uploadService = new UploadService();

        String url = uploadService.saveFile(multipartFile);

        log.info("访问路径是：{}",url);

        log.info("当前程序的系统路径：{}",System.getProperty("user.dir"));
        return url;

    }


    /**
     * 将文件上传到fastdfs上
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public String saveFile( MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if(inputStream != null){
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(file);  //upload to fastdfs
        } catch (Exception e) {
            log.error("upload file Exception! : {}",e);
        }
        if (fileAbsolutePath==null) {
            log.error("upload file failed,please upload again!");
        }
//        String path=FastDFSClient.getTrackerUrl()+fileAbsolutePath[0]+ "/"+fileAbsolutePath[1];
        String path= FastDFSClient.getTrackerUrl().split(":8080")[0]+ "/"+fileAbsolutePath[1];
        return path;
    }
}
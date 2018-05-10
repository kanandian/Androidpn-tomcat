package org.androidpn.server.console.controller;

import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class ImageUploadController extends MultiActionController {

    private BussinessService bussinessService;

    public ImageUploadController() {
        bussinessService = ServiceLocator.getBussinessService();
    }

    public ModelAndView list(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        String imageURL = null;

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
        List<FileItem> fileItemList = servletFileUpload.parseRequest(request);

        for (FileItem fileItem : fileItemList) {
            if ("image".equals(fileItem.getFieldName())) {
//                fileItem.getString("utf-8");
                imageURL = uploadImage(request, fileItem);

                String imageName = fileItem.getName();
                String bussinessId = imageName.split("[.]")[0];
                bussinessService.updateImageForBussiness(bussinessId, imageURL);
            }
        }

        request.setAttribute("message", "success");

        ModelAndView mav = new ModelAndView();
        // mav.addObject("list", null);
        mav.setViewName("notification/image");
        return mav;
    }

    private String uploadImage(HttpServletRequest request, FileItem fileItem) throws IOException {
        String imagePath = getServletContext().getRealPath("/bussinessimage");
        File uploadDir = new File(imagePath);

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        if (fileItem != null && fileItem.getContentType().startsWith("image")) {
            String imageName = fileItem.getName(); //获取原文件名
            InputStream inputStream = fileItem.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(imagePath+"/"+imageName);
            byte[] buf = new byte[1024];
            int len = 0;

            while ((len = inputStream.read(buf)) > 0) {
                fileOutputStream.write(buf, 0, len);
                fileOutputStream.flush();
            }

            inputStream.close();
            fileOutputStream.close();

            String serverName = request.getServerName();
            int port = request.getServerPort();
            String imageURL = "http://"+serverName+":"+port+"/bussinessimage/"+imageName;

            return imageURL;
        }

        return "";
    }

//    private String fildUpload(@RequestParam(value="file",required=false) MultipartFile file,
//                              HttpServletRequest request)throws Exception{
//        //基本表单
////        System.out.println(users.toString());
//
//        //获得物理路径webapp所在路径
//        String pathRoot = request.getSession().getServletContext().getRealPath("");
//        String path="";
//        if(!file.isEmpty()){
//            //生成uuid作为文件名称
//            String uuid = UUID.randomUUID().toString().replaceAll("-","");
//            //获得文件类型（可以判断如果不是图片，禁止上传）
//            String contentType=file.getContentType();
//            //获得文件后缀名称
//            String imageName=contentType.substring(contentType.indexOf("/")+1);
//            path="/static/images/"+uuid+"."+imageName;
//            file.transferTo(new File(pathRoot+path));
//        }
//        System.out.println(path);
//        request.setAttribute("imagesPath", path);
//        return "success";
//    }

//    public String get() {
//        System.out.println("sizizi");
//        int a = 1;
//        int b = 2;
//        return "/notification/form";
//    }

}

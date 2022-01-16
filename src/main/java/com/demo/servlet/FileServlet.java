package com.demo.servlet;

import com.demo.bean.FileBean;
import com.demo.dao.FileDao;
import com.demo.tools.StringHandler;
import com.jspsmart.upload.File;
import com.jspsmart.upload.Files;
import com.jspsmart.upload.SmartUpload;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class FileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String servletPath=request.getServletPath();
        if("/upload".equals(servletPath)){
            upload(request,response);
        }
        else if("/downloadview".equals(servletPath)){
            downloadview(request,response);
        }
        else if("/downloadrun".equals(servletPath)){
            downloadrun(request,response);
        }
    }
    private void upload(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String message="";
        //设置允许上传文件的总长度为20兆
        long maxsize=1024*1024*20;
        //设置存放文件的目录(该目录位于web应用根目录下)
        String filedir="/files/";

        int k=0;
        try{
            SmartUpload myup=new SmartUpload();
            myup.initialize(this,request,response);
            //设置允许上传文件的总长度
            myup.setTotalMaxFileSize(maxsize);
            //上传文件
            myup.upload();

            //获取所有的上传文件
            Files files=myup.getFiles();
            //获取上传文件数量
            int count=files.getCount();
            //获取当前时间
            Date date=new Date();
            boolean mark=false;
            FileDao fileDao=new FileDao();
            for(int i=0;i<count;i++){
                //逐个获取上传的文件
                File file=files.getFile(i);
                //如果有文件
                if(!file.isMissing()){
                    mark=true;
                    int filesize=file.getSize();
                    if(filesize==0){
                        message+="<li>文件 <b><font color='red'>"+file.getFilePathName()+"</font></b> 的大小为0！本系统不允许上传0字节的文件！</li><br>";
                    }
                    else{
                        //获取上传文件的名称
                        String filename=file.getFileName();
                        String filetype=file.getContentType().trim();
                        String savename=StringHandler.getSerial(date,i)+"."+file.getFileExt();
                        String fileinfo=myup.getRequest().getParameter("fileinfo"+(i+1));
                        String uptime= StringHandler.timeTostr(date);

                        FileBean filebean=new FileBean();
                        filebean.setFileName(filename);
                        filebean.setFileSaveName(savename);
                        filebean.setFileType(filetype);
                        filebean.setFileSize(String.valueOf(filesize));
                        filebean.setFileInfo(fileinfo);
                        filebean.setFileUpTime(uptime);

                        //保存文件信息到数据库中
                        k=fileDao.addFileInfo(filebean);
                        if(k<=0){
                            //信息保存失败！
                            message+="●文件 <b><font color='red'>"+file.getFilePathName()+"</font></b> 上传失败！<br>";
                        }

                        else{
                            //保存文件到磁盘的指定目录下
                            file.saveAs(filedir+savename,File.SAVEAS_VIRTUAL);
                            message+="●文件 <b><font color='red'>"+file.getFilePathName()+"</font></b> 上传成功！<br>";
                        }
                    }
                }
            }
            if(!mark)
                message="●请至少选择一个要上传的文件！<br>";
            fileDao.closed();
        }
        catch(java.lang.SecurityException e1){
            k=-1;
            message="●上传的文件总大小不允许超过"+(maxsize/1024/1024)+"兆！<br>";
            e1.printStackTrace();
        }
        catch(Exception e2){
            k=-1;
            message="●文件上传失败！<br>";
            e2.printStackTrace();
        }
        catch(java.lang.OutOfMemoryError e3){
            k=-1;
            message="●您上传的文件太大！<br>";
            e3.printStackTrace();
        }
        if(k<=0)
            message+="<a href='javascript:window.history.go(-1)'>>> 返回重试</a><br>";
        else
            message+="<a href='uploadfile.jsp'>>> 继续上传</a><br>";

        message+="<a href='index.jsp'>>> 返回主页</a>";

        request.setAttribute("message",message);
        RequestDispatcher rd=request.getRequestDispatcher("/message.jsp");
        rd.forward(request,response);
    }

    private void downloadview(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        FileDao fileDao=new FileDao();
        try {
            List filelist = fileDao.getFileList();
            request.setAttribute("filelist",filelist);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        fileDao.closed();
        RequestDispatcher rd=request.getRequestDispatcher("/downloadfile.jsp");
        rd.forward(request,response);
    }

    private void downloadrun(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String filedir="/files/";
        String downfilename=request.getParameter("downname");
        FileBean file=null;

        try{
            FileDao fileDao=new FileDao();
            file=fileDao.getFileSingle(downfilename);
            fileDao.closed();
            if(file!=null){
                String filename=new String(file.getFileName().getBytes("gb2312"),"ISO-8859-1");
                SmartUpload mydown=new SmartUpload();
                mydown.initialize(getServletConfig(),request,response);
                mydown.setContentDisposition(null);
                mydown.downloadFile(filedir+downfilename,file.getFileType(),filename);
            }
        }catch(Exception e){
            e.printStackTrace();
            String message="× 下载失败！文件 <b><font color='red'>"+file.getFileName()+"</font></b> 不存在或已经被删除！<br>";
            message+="<a href='javascript:window.history.go(-1)'>返回</a>";
            request.setAttribute("message",message);
            RequestDispatcher rd=request.getRequestDispatcher("/message.jsp");
            rd.forward(request,response);
        }
    }
}

package com.demo.dao;

import com.demo.bean.FileBean;
import com.demo.tools.JDBConnection;
import com.demo.tools.StringHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileDao {
    private JDBConnection connection = null;

    public FileDao(){
        connection = new JDBConnection();
    }

    public int addFileInfo(FileBean file){
        int i=-1;
        String sql="insert into t_file (file_save_name,file_name,file_type,file_size,file_info,file_up_time) values(?,?,?,?,?,?)";
        String[] params={file.getFileSaveName(),file.getFileName(),file.getFileType(),file.getFileSize(),file.getFileInfo(),file.getFileUpTime()};
        connection = new JDBConnection();
        Boolean flag = connection.updateData(sql, params);
        if(flag){
            i = 1;
        }
        return i;
    }

    public FileBean getFileSingle(String savename) throws SQLException{
        FileBean single=null;
        String sql="select * from t_file where file_save_name=?";
        String[] params={savename};
        connection = new JDBConnection();
        ResultSet rs = connection.queryByPsStatement(sql,params);
        if(rs.next()){
            single =new FileBean();
            single.setId(rs.getInt(1));
            single.setFileSaveName(rs.getString(2));
            single.setFileName(rs.getString(3));
            single.setFileType(rs.getString(4));
            single.setFileInfo(rs.getString(5));
            single.setFileUpTime(rs.getString(6));
        }

        return single;
    }

    public List getFileList() throws SQLException{
        String sql="select * from t_file order by file_save_name desc";
        List list=getList(sql,null);
        return list;
    }

    private List getList(String sql,String[] params) throws SQLException{
        List list=null;
        connection = new JDBConnection();

        ResultSet rs=connection.queryByPsStatement(sql,params);

        if(rs!=null){
            list=new ArrayList();
            while(rs.next()){
                FileBean single=new FileBean();
                single.setId(rs.getInt(1));
                single.setFileSaveName(rs.getString(2));
                single.setFileName(rs.getString(3));
                single.setFileType(rs.getString(4));
                single.setFileSize(rs.getString(5));
                single.setFileInfo(rs.getString(6));
                single.setFileUpTime(StringHandler.timeTostr(rs.getTimestamp(7)));
                list.add(single);
            }
            rs.close();
        }
        return list;
    }

    public void closed(){
        connection.closeAll();
    }

}

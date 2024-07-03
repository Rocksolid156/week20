package net.rsolid;

import java.sql.*;

public class GetConnection {
    private final Connection conn;
    public GetConnection(String addr) throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn=DriverManager.getConnection(addr);
    }

    public void CloseConn() throws SQLException {
        conn.close();
    }

    public String GetInfo() throws SQLException {
        DatabaseMetaData dbmd=conn.getMetaData();
        return "JDBC驱动程序:"+dbmd.getDriverName()+","+dbmd.getDriverVersion()+"\nJDBC URL: "+dbmd.getURL()+"\n数据库:"+
                dbmd.getDatabaseProductName()+",版本: "+dbmd.getDatabaseProductVersion()+",用户名:"+dbmd.getUserName();
    }

    public Connection getConn(){
        return conn;
    }
}
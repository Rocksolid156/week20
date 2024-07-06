package net.rsolid;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ParseResult {
    String table,sortColumn;
    DefaultTableModel tableModel;
    Statement stmt;
    ResultSet rset;
    public ParseResult(String table,String sortColumn,DefaultTableModel tableModel,Statement stmt){
        this.table=table;
        this.sortColumn=sortColumn;
        this.tableModel=tableModel;
        this.stmt=stmt;
    }
    public ParseResult(DefaultTableModel tableModel,Statement stmt){
        this.tableModel=tableModel;
        this.stmt=stmt;
    }

    public void query() throws SQLException{
        String sql="SELECT * FROM "+table;
        if (sortColumn!=null&& !sortColumn.isEmpty())
            sql+=" ORDER BY "+sortColumn;
        query(sql,tableModel);
    }
    public void query(String sql,DefaultTableModel tableModel) throws SQLException{
        rset=stmt.executeQuery(sql);

        ResultSetMetaData rsmd=rset.getMetaData();
        int count=rsmd.getColumnCount();
        for (int i=1;i<=count;i++)
            tableModel.addColumn(rsmd.getColumnLabel(i));

        Object[] columns=new Object[count];
        while (rset.next()) {
            for (int i = 1; i <= columns.length; i++)
                columns[i - 1] = rset.getString(i);
            tableModel.addRow(columns);
        }

    }
    public void query(ResultSet rs,DefaultTableModel tableModel) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // 获取列名
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            tableModel.addColumn(metaData.getColumnName(i));
        }

        // 获取数据
        while (rs.next()) {
            Object[] rowData=new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowData[i-1]=rs.getObject(i);
            }
            tableModel.addRow(rowData);
        }
    }
}

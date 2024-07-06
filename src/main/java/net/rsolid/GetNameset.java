package net.rsolid;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class GetNameset extends RunStatement {
    public GetNameset(Connection conn) throws SQLException {
        super(conn);
    }

//    public String[] getNameSet(String tablename, Connection conn) throws SQLException {
//        DefaultTableModel Result = new DefaultTableModel();
//        RunStatement subStmt = new RunStatement(conn);
//        ParseResult subResult = new ParseResult(tablename, null, Result, subStmt.getStmt());
//        subResult.query();
//        int rowCount = Result.getRowCount();
//        String[] fullNames = new String[rowCount];
//        int index = Result.findColumn("fname");
//
//        int j = 0;
//        for (int i = 0; i < rowCount; i++) {
//            Object fullnameObject = Result.getValueAt(i, index);
//            if (fullnameObject != null) {
//                fullNames[i] = fullnameObject.toString();
//                j++;
//            }
//            if (j < rowCount) {
//                String[] validIDs = new String[j];
//                System.arraycopy(fullNames, 0, validIDs, 0, j);
//                return validIDs;
//            }
//        }
//
//        return fullNames;
//    }

    public String[] getNameSet(String tablename, Connection conn, HashMap<String, String> hashMap, int mode) throws SQLException {
        if (mode == 1) {
            DefaultTableModel Result = new DefaultTableModel();
            RunStatement subStmt = new RunStatement(conn);
            ParseResult subResult = new ParseResult(tablename, null, Result, subStmt.getStmt());
            subResult.query();
            int rowCount = Result.getRowCount();
            String[] names = new String[rowCount];
            int index = Result.findColumn("fname");
            int IDindex = switch (tablename) {
                case "patient" -> Result.findColumn("ID");
                case "med" -> Result.findColumn("aname");
                case "department" -> Result.findColumn("dname");
                case "doc" -> Result.findColumn("docid");
                default -> 100;
            };

            int j = 0;
            for (int i = 0; i < rowCount; i++) {
                Object nameObject = Result.getValueAt(i, index);
                Object idObject = Result.getValueAt(i, IDindex);
                if (nameObject != null) {
                    String name = nameObject.toString();
                    String id = idObject.toString();
                    names[j] = name;
                    hashMap.put(name, id);
                    j++;
                }
            }
            if (j < rowCount) {
                String[] validnames = new String[j];
                System.arraycopy(names, 0, validnames, 0, j);
                return validnames;
            }
            return names;
        }
        else if (mode == 2) {
            DefaultTableModel Result = new DefaultTableModel();
            RunStatement subStmt = new RunStatement(conn);
            ParseResult subResult = new ParseResult(tablename, null, Result, subStmt.getStmt());
            subResult.query();
            int rowCount = Result.getRowCount();
            String[] IDs = new String[rowCount];

            int IDindex = switch (tablename) {
                case "patient" -> Result.findColumn("ID");
                case "med" -> Result.findColumn("aname");
                case "department" -> Result.findColumn("dname");
                default -> 0;
            };

            for (int i = 0; i < rowCount; i++) {
                Object idObject = Result.getValueAt(i, IDindex);
                String id = idObject.toString();
                IDs[i] = id;
            }

            return IDs;
        }
        return null;
    }
}
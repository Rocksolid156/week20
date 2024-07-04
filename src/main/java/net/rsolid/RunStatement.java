package net.rsolid;

import java.sql.*;

public class RunStatement {
    Statement stmt;
    Connection conn;
    public RunStatement(Connection conn) throws SQLException {
        this.conn=conn;
        stmt=conn.createStatement();
    }

    public void initDB() throws SQLException {
        String DBmedicine="CREATE TABLE `med` (\n" +
//                "  `id` int NOT NULL AUTO_INCREMENT,\n"+
                "  `aname` varchar(10) NOT NULL COMMENT '药品批准文号',\n" +
                "  `fname` varchar(25) COMMENT '药品全名',\n" +
                "  `price` DECIMAL(10,2) COMMENT '价格',\n" +
                "  `count` int DEFAULT 0 COMMENT '库存',\n" +
                "  `type`  int DEFAULT 0 COMMENT '类型',\n"+
                "  PRIMARY KEY (`aname`)\n"+
//                "  UNIQUE  KEY `aname` (`aname`)"+
                ")";
        String DBdepartment="CREATE TABLE `department` (\n" +
                "  `dname` VARCHAR (5) PRIMARY KEY COMMENT '科室号',\n" +
                "  `fname` VARCHAR (20) COMMENT '科室全名'\n" +
                ");";
        String DBimport="CREATE TABLE `imp` (\n" +
                "  `ID` int PRIMARY KEY AUTO_INCREMENT,\n" +
                "  `aname` varchar(10) NOT NULL,\n" +
                "  `date` datetime COMMENT '入库日期',\n" +
                "  `count` int NOT NULL COMMENT '入库数量',\n" +
                "  FOREIGN KEY (`aname`) REFERENCES `med` (`aname`)\n" +
                ")";
        String DBexport="CREATE TABLE `export` (\n" +
                "  `ID` INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "  `aname` VARCHAR (10) NOT NULL,\n" +
                "  `target` VARCHAR (5) NOT NULL COMMENT '出库目标科室',\n" +
                "  `date` DATETIME COMMENT '出库日期',\n" +
                "  `count` INT COMMENT '出库数量',\n" +
                "  FOREIGN KEY (`aname`) REFERENCES `experiment2`.`med` (`aname`),\n" +
                "  FOREIGN KEY (`target`) REFERENCES `experiment2`.`department` (`dname`)\n" +
                ");\n";
        String DBdoctor="CREATE TABLE `doc` (\n" +
                "  `docid` VARCHAR (5) PRIMARY KEY COMMENT '医生id号',\n" +
                "  `fname` VARCHAR (20) COMMENT '医生全名',\n" +
                "  `belong` VARCHAR (5) NOT NULL COMMENT '所属科室',\n" +
                "  FOREIGN KEY (`belong`) REFERENCES `experiment2`.`department` (`dname`)\n" +
                ");\n";
        String DBpatient="CREATE TABLE `patient` (\n" +
                "  `ID` INT PRIMARY KEY AUTO_INCREMENT COMMENT '患者id号',\n" +
                "  `fname` VARCHAR (20) COMMENT '患者全名',\n" +
                "  `doc` VARCHAR (5) COMMENT '主治医师id',\n" +
                "  `sex` CHAR (6) COMMENT '性别',\n" +
                "  `intime` DATETIME NOT NULL COMMENT '入院日期',\n" +
                "  `outtime` DATETIME COMMENT '出院日期',\n" +
                "  FOREIGN KEY (doc) REFERENCES doc(docid)\n" +
                ");\n";
        String DBprescription="CREATE TABLE prescription (\n" +
                "  ID INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "  patient_id INT,\n" +
                "  medicine VARCHAR (10),\n" +
                "  date DATETIME,\n" +
                "  amount int ,\n" +
                "  FOREIGN KEY (patient_id) REFERENCES patient(id),\n" +
                "  FOREIGN KEY (medicine) REFERENCES med(aname)\n" +
                ");\n";
        String DBbilling="CREATE TABLE billing (\n" +
                "  ID INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "  patient_id INT,\n" +
                "  amount DECIMAL(10, 2),\n" +
                "  date DATETIME,\n" +
                "  FOREIGN KEY (patient_id) REFERENCES patient(id)\n" +
                ");\n";
        String[] tables={DBmedicine,DBdepartment,DBimport,DBexport,DBdoctor,DBpatient,DBprescription,DBbilling};

        String trigger_in= "CREATE\n" +
                "TRIGGER `import_autoupdate` BEFORE INSERT ON `imp` \n" +
                "FOR EACH ROW BEGIN\n" +
                "IF NOT EXISTS(SELECT * FROM med WHERE med.aname=new.aname) THEN\n" +
                "INSERT INTO med(aname,count) VALUES(new.aname,new.count);\n" +
                "ELSE UPDATE med SET COUNT=COUNT+new.count WHERE aname=new.aname;\n" +
                "END IF;\n" +
                "END;\n";
        String trigger_out="CREATE\n" +
                "TRIGGER `export_autoupdate` AFTER INSERT ON `export` \n" +
                "FOR EACH ROW BEGIN\n" +
                "IF NOT EXISTS (SELECT * FROM med WHERE med.aname=new.aname) THEN\n" +
                "signal SQLSTATE '45000' SET message_text='不存在这个药品';\n" +
                "ELSEIF (SELECT med.count FROM med WHERE aname=new.aname)-new.count<0 THEN\n" +
                "signal SQLSTATE '45000' SET message_text='药品库存不足';\n" +
                "ELSEIF NOT EXISTS (SELECT * FROM department WHERE department.dname=new.target) THEN\n"+
                "signal SQLSTATE '45000' SET message_text='科室不存在';\n"+
                "ELSE UPDATE med SET COUNT=COUNT-new.count WHERE aname=new.aname;\n" +
                "END IF;\n" +
                "END;\n";

        String procedure="CREATE PROCEDURE stats(IN start_date DATE, IN end_date DATE)\n" +
                "BEGIN\n" +
                "    SELECT department.dname, COUNT(patient.id) AS 就诊人数\n" +
                "    FROM department\n" +
                "    JOIN doc ON department.dname = doc.belong\n" +
                "    JOIN patient ON doc.docid = patient.doc\n" +
                "    WHERE patient.intime BETWEEN start_date AND end_date\n" +
                "    GROUP BY department.dname;\n" +
                "    \n" +
                "    SELECT export.aname, SUM(export.count) AS 输入情况\n" +
                "    FROM export\n" +
                "    WHERE export.date BETWEEN start_date AND end_date\n" +
                "    GROUP BY export.aname;\n" +
                "END;\n";
        String view1="CREATE VIEW medicine_stock AS\n" +
                "SELECT fname as '药品名', SUM(count) AS '总量'\n" +
                "FROM med\n" +
                "GROUP BY aname;\n";
        String view2="CREATE VIEW View_Chinese_Medicine AS\n" +
                "SELECT \n" +
                "    aname AS 药品批准文号,\n" +
                "    fname AS 药品全名,\n" +
                "    price AS 价格,\n" +
                "    count AS 库存,\n" +
                "    type AS 类型\n" +
                "FROM \n" +
                "    med;\n";
        String view3="CREATE VIEW View_Chinese_Department AS\n" +
                "SELECT \n" +
                "    dname AS 科室号,\n" +
                "    fname AS 科室全名\n" +
                "FROM \n" +
                "    department;\n";
        String view4="CREATE VIEW View_Chinese_Import AS\n" +
                "SELECT \n" +
                "    ID AS 入库ID,\n" +
                "    aname AS 药品批准文号,\n" +
                "    date AS 入库日期,\n" +
                "    count AS 入库数量\n" +
                "FROM \n" +
                "    imp;\n";
        String view5="CREATE VIEW View_Chinese_Export AS\n" +
                "SELECT \n" +
                "    ID AS 出库ID,\n" +
                "    aname AS 药品批准文号,\n" +
                "    target AS 出库目标科室,\n" +
                "    date AS 出库日期,\n" +
                "    count AS 出库数量\n" +
                "FROM \n" +
                "    export;\n";
        String view6="CREATE VIEW View_Chinese_Doctor AS\n" +
                "SELECT \n" +
                "    docid AS 医生ID号,\n" +
                "    fname AS 医生全名,\n" +
                "    belong AS 所属科室\n" +
                "FROM \n" +
                "    doc;\n";
        String view7="CREATE VIEW View_Chinese_Patient AS\n" +
                "SELECT \n" +
                "    ID AS 患者ID号,\n" +
                "    fname AS 患者全名,\n" +
                "    doc AS 主治医师ID,\n" +
                "    sex AS 性别,\n" +
                "    intime AS 入院日期,\n" +
                "    outtime AS 出院日期\n" +
                "FROM \n" +
                "    patient;\n";
        String view8="CREATE VIEW View_Chinese_Prescription AS\n" +
                "SELECT \n" +
                "    ID AS 处方ID,\n" +
                "    patient_id AS 患者ID,\n" +
                "    medicine AS 药品批准文号,\n" +
                "    date AS 处方日期,\n" +
                "    amount AS 处方数量\n" +
                "FROM \n" +
                "    prescription;\n";
        String view9="CREATE VIEW View_Chinese_Billing AS\n" +
                "SELECT \n" +
                "    ID AS 账单ID,\n" +
                "    patient_id AS 患者ID,\n" +
                "    amount AS 账单金额,\n" +
                "    date AS 账单日期\n" +
                "FROM \n" +
                "    billing;\n";
        String[] precompiled={trigger_in,trigger_out,procedure,view1,view2,view3,view4,view5,view6,view7,view8,view9};
        for (String tableSQL:tables){
            try {
                stmt.execute(tableSQL);
                System.out.println(tableSQL+"成功创建");
            }catch (SQLException ex){
                System.err.println(tableSQL+"创建失败，错误代码："+ex.getErrorCode());
            }
        }
        for (String inProcess:precompiled){
            try {
                stmt.execute(inProcess);
                System.out.println(inProcess+"成功创建");
            }catch (SQLException ex){
                System.err.println(inProcess+"创建失败,错误代码："+ex.getErrorCode());
            }
        }
        stmt.close();
    }
    public boolean checkExists(String name,int type) throws SQLException {
        DatabaseMetaData meta= conn.getMetaData();
        ResultSet rs = null;
        if(type==1)
            rs=meta.getTables(null,null,name,new String[]{"TABLE"});
        else if (type == 2) {
            rs=meta.getProcedures(null,null,name);
        } else if (type == 3) {
            rs=meta.getTables(null,null,name,new String[]{"VIEW"});
        }else {
            return false;
        }
        boolean exists=rs.next();
        rs.close();
        return exists;
    }
    public boolean checkTrigger(String triggername) throws SQLException{
        String query="SELECT '"+triggername+ "' FROM INFORMATION_SCHEMA.TRIGGERS WHERE TRIGGER_SCHEMA = 'experiment2' AND TRIGGER_NAME = '"+triggername+"'";
        PreparedStatement stmt=conn.prepareStatement(query);
        ResultSet resultSet=stmt.executeQuery();

        boolean exists=resultSet.next();
        resultSet.close();
        stmt.close();
        return exists;
    }
    public void removeDB() throws SQLException{
        String drop1="drop table if exists imp;";
        String drop2="drop table if exists export;";
        String drop3="drop table if exists doc;";
        String drop4="drop table if exists patient;";
        String drop5="drop table if exists med;";
        String drop6="drop table if exists department;";
        String drop7="drop table if exists billing;";
        String drop8="drop table if exists prescription;";
        String drop9_1="drop trigger if exists import_autoupdate;";
        String drop9_2="drop trigger if exists export_autoupdate;";
        String drop10="drop procedure if exists stats";
        String drop11="drop view if exists medicine_stock";
//        String drop12="drop view if exists View_Chinese";
        String[] drop={drop1,drop2,drop7,drop8,drop4,drop3,drop6,drop5,drop9_1,drop9_2,drop10,drop11};
        for (String current:drop){
            try {
                stmt.execute(current);
                System.out.println("成功重置："+current);
            }catch (SQLException ex){
                System.err.println("重置失败："+current+"  错误代码:"+ex.getErrorCode());
            }
        }
        stmt.close();
    }
    public Statement getStmt(){
        return stmt;
    }
}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class main {
    public static ResultSet rs = null;
    public static ResultSet rsSourceWrong = null;
    public static ResultSet rsSourceAccepted = null;
    public static Connection con = null;
    public static boolean flagDB = true;

    public static void main(String[] args) throws Exception {

        BufferedWriter writer = null;

        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cf?autoReconnect=true&useSSL=false", "root", "******");
        Statement stmt = con.createStatement();
        Statement stmtSourceWrong = con.createStatement();
        Statement stmtSourceAccepted = con.createStatement();
        Statement stmtUpdate = con.createStatement();
        for (int mainCounter = 0; mainCounter < 9200000; mainCounter = mainCounter + 2000) {
            rs = stmt.executeQuery("SELECT id , subaccepted , subwrong  FROM cf.realfaultslocations where   id <  " + mainCounter + "  and  id  >  " + (mainCounter - 2500) + " and  `change` is NULL  "); 
            while (rs.next()) {

                rsSourceAccepted = stmtSourceAccepted.executeQuery("SELECT sourceCode FROM cf.source WHERE submission =" + rs.getString("subaccepted"));
                rsSourceAccepted.next();
                rsSourceWrong = stmtSourceWrong.executeQuery("SELECT sourceCode FROM cf.source WHERE submission =" + rs.getString("subwrong"));
                rsSourceWrong.next();


                System.out.println("------------------------------");
                writer = new BufferedWriter(new FileWriter("sourceWrong.txt", false));

                String codeWrong = rsSourceWrong.getString("sourceCode");
                while (codeWrong.indexOf("\t") > 0 || codeWrong.indexOf(" ") > 0) {
                    codeWrong = codeWrong.replace("\t", "");
                    codeWrong = codeWrong.replace(" ", "");
                }
                writer.write(codeWrong);
                writer.close();

                writer = new BufferedWriter(new FileWriter("sourceAccepted.txt", false));

                String codeAccepted = rsSourceAccepted.getString("sourceCode");
                while (codeAccepted.indexOf("\t") > 0 || codeAccepted.indexOf(" ") > 0) {
                    codeAccepted = codeAccepted.replace("\t", "");
                    codeAccepted = codeAccepted.replace(" ", "");
                }

                writer.write(codeAccepted);
                writer.close();

                List<String> fileReaderWrongCode = new ArrayList<>();
                List<String> fileReaderWrongCodeFinal = new ArrayList<>();
                List<String> fileReaderAccepted = new ArrayList<>();


                fileReaderAccepted = Files.readAllLines(Paths.get("sourceAccepted.txt"));
                writer = new BufferedWriter(new FileWriter("sourceAcceptedFinal.txt", false));
                for (int i = 0; i < fileReaderAccepted.size(); i++) {
                    if (!fileReaderAccepted.get(i).equals("")) {
                        writer.write(fileReaderAccepted.get(i));
                        writer.newLine();
                    }
                }
                writer.close();

                fileReaderWrongCode = Files.readAllLines(Paths.get("sourceWrong.txt"));
                writer = new BufferedWriter(new FileWriter("sourceWrongFinal.txt", false));
                for (int i = 0; i < fileReaderWrongCode.size(); i++) {
                    if (!fileReaderWrongCode.get(i).equals("")) {
                        writer.write(fileReaderWrongCode.get(i));
                        writer.newLine();
                    }
                }
                writer.close();


                node.panchor = null;
                node.typeEdit = "";
                node.counterPrintedLineDelete = 0;
                node.counterPrintedLineChange = 0;
                node.counterPrintedLineInsert = 0;


                Diff d = null;
                d = new Diff();
                d.doDiff("sourceWrongFinal.txt", "sourceAcceptedFinal.txt");
                int countLine = fileReaderWrongCode.size();
                try {
                    double insertRate=0;
                    double changeRate=0;
                    double deleteRate =0;
                    if(countLine != 0 ) {
                        insertRate = node.counterPrintedLineInsert / Double.parseDouble(String.valueOf(countLine));
                        changeRate = node.counterPrintedLineChange / Double.parseDouble(String.valueOf(countLine));
                        deleteRate = node.counterPrintedLineDelete / Double.parseDouble(String.valueOf(countLine));
                    }else {
                        insertRate = node.counterPrintedLineInsert;
                        changeRate = node.counterPrintedLineChange;
                        deleteRate = node.counterPrintedLineDelete;
                    }

                    String[] realFaults;
                    String[] realFaultsInsert;
                    String[] realFaultsChange;
                    String[] realFaultsDelete;
                    realFaults = d.returnRealFaults().split(",");
                    realFaultsInsert = d.returnRealFaultsInsert().split(",");
                    realFaultsChange = d.returnRealFaultsChange().split(",");
                    realFaultsDelete = d.returnRealFaultsDelete().split(",");

                    int countFaults = 0;
                    int countFaultsInsert = 0;
                    int countFaultsChange = 0;
                    int countFaultsDelete = 0;

                    countFaults = new StringTokenizer(d.returnRealFaults(), ",").countTokens();
                    countFaultsInsert = new StringTokenizer(d.returnRealFaultsInsert(), ",").countTokens();
                    countFaultsChange = new StringTokenizer(d.returnRealFaultsChange(), ",").countTokens();
                    countFaultsDelete = new StringTokenizer(d.returnRealFaultsDelete(), ",").countTokens();

                    System.out.println(rs.getString("id") + " --->>> " + d.returnRealFaults() + " ; count:" + countFaults);
                    System.out.println(rs.getString("id") + " ---insert--->>> " + d.returnRealFaultsInsert() + " ; count:" + countFaultsInsert);
                    System.out.println(rs.getString("id") + " ---change--->>> " + d.returnRealFaultsChange() + " ; count:" + countFaultsChange);
                    System.out.println(rs.getString("id") + " ---delete--->>> " + d.returnRealFaultsDelete() + " ; count:" + countFaultsDelete);
                    System.out.println("insert --> " + node.counterPrintedLineInsert + " ;" + format(insertRate));
                    System.out.println("change --> " + node.counterPrintedLineChange + " ;" + format(changeRate));
                    System.out.println("delete --> " + node.counterPrintedLineDelete + " ;" + format(deleteRate));
                    System.out.println("countLine ---> " + countLine);

                    int fLoc = 0;
                    int fLocInsert = 0;
                    int fLocChange = 0;
                    int fLocDelete = 0;
                    int rfLoc = 0;
                    String rfl = "";
                    String rfli = "";
                    String rflc = "";
                    String rfld = "";
                    for (int i = 0; i < fileReaderWrongCode.size(); i++) {
                        if (!fileReaderWrongCode.get(i).equals("")) {
                            rfLoc++;
                        }
                        if (fLoc < countFaults && rfLoc == Integer.parseInt(realFaults[fLoc])) {
                            rfl += (i + 1) + ",";
                            fLoc++;
                        }
                        if (fLocInsert < countFaultsInsert && rfLoc == Integer.parseInt(realFaultsInsert[fLocInsert])) {
                            rfli += (i + 1) + ",";
                            fLocInsert++;
                        }
                        if (fLocChange < countFaultsChange && rfLoc == Integer.parseInt(realFaultsChange[fLocChange])) {
                            rflc += (i + 1) + ",";
                            fLocChange++;
                        }
                        if (fLocDelete < countFaultsDelete && rfLoc == Integer.parseInt(realFaultsDelete[fLocDelete])) {
                            rfld += (i + 1) + ",";
                            fLocDelete++;
                        }
                    }
                    System.out.println(rfl);
                    System.out.println(rfli);
                    System.out.println(rflc);
                    System.out.println(rfld);

                    stmtupdate.executeupdate("update cf.realfaultslocations " +
                            " set " +
                            " `change` = " + node.counterprintedlinechange + " , " +
                            " `insert` = " + node.counterprintedlineinsert + " , " +
                            " `delete` = " + node.counterprintedlinedelete + " , " +
                            " `changerate` = " + format(changerate) + " , " +
                            " `insertrate` = " + format(insertrate) + " , " +
                            " `deleterate` = " + format(deleterate) + " , " +
                            " `faultlocations` = '" + rfl + "', " +
                            " `insertfaultslocations` = '" + rfli + "' , " +
                            " `changefaultslocations` = '" + rflc + "' , " +
                            " `deletefaultslocations` = '" + rfld + "' , " +
                            " `countfaults` = " + countfaults + " , " +
                            " `countinsertfaults` = " + countfaultsinsert + " , " +
                            " `countchangefaults` = " + countfaultschange + " , " +
                            " `countdeletefaults` = " + countfaultsdelete +
                            " where id = " + rs.getstring("id"));
                } catch (Exception e) {
                    e.printStackTrace();
                    BufferedWriter bw = new BufferedWriter(new FileWriter("logFailUpdate9MField.txt", true));
                    bw.write(rs.getString("id"));
                    bw.newLine();
                    bw.close();
                }
            }
        }
        System.out.println(d.printResult(10));
    }

    public static String format(Number n) {
        NumberFormat format = DecimalFormat.getInstance();
        format.setRoundingMode(RoundingMode.FLOOR);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(4);
        return format.format(n);
    }
}

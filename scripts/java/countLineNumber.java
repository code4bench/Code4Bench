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

public class countLineNumber {
    public static ResultSet rs = null;
    public static Connection con = null;

    public static void main(String[] args) throws Exception {

        BufferedWriter writer = null;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cf?autoReconnect=true&useSSL=false", "root", "yourPass");
        Statement stmt = con.createStatement();
        Statement stmtUpdate = con.createStatement();
        for(int i = 0 ; i < 3500000 ; i = i + 1000) {
            System.out.println("************* i : " + i);
            rs = stmt.executeQuery("SELECT sourceId , sourceCode   FROM cf.source where  sourceId < " + i + " and sourceId >" + (i - 1500) + " and countline is null " );
            while (rs.next()) {
                writer = new BufferedWriter(new FileWriter("sourceCode.txt", false));

                String codeAccepted = rs.getString("sourceCode");
                writer.write(codeAccepted);
                writer.close();

                List<String> fileReaderAccepted = new ArrayList<>();


                fileReaderAccepted = Files.readAllLines(Paths.get("sourceCode.txt"));


                int countLine = fileReaderAccepted.size();
                try {
                    System.out.println("countLine ---> " + countLine);
                    stmtUpdate.executeUpdate("UPDATE cf.source " +
                            " set " +
                            " `countline` = " + countLine +
                            " WHERE sourceId = " + rs.getString("sourceId"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


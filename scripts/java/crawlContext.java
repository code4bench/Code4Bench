import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Executable;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class problemnumber {
    public static ResultSet rs = null;
    public static Connection con = null;
    public static boolean flagDB = true;

    public static void main(String[] args) throws Exception {
		// for existing problems in code4bench, crawl questions of the contests
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cf?autoReconnect=true&useSSL=false", "root", "1234");
        Statement stmt = con.createStatement();
        Statement stmtUpdate = con.createStatement();
        rs = stmt.executeQuery("SELECT problemId , contest , `name`  FROM cf.problem  WHERE problemcontext is null "); // id < 50 and id > 45
        String contest = "";
        String name = "";
        while (rs.next()) {
            System.out.println("------------------------------");
            System.out.print(rs.getString("problemId") + " ->");
            System.out.print((contest = rs.getString("contest")) + " ->");
            System.out.println(name = rs.getString("name"));

            try {
                String url = "http://codeforces.com/contest/" + contest + "/problem/" + name;

                System.out.println(url);

                Document doc = Jsoup.connect(url).get();
                Elements problemContext = doc.select("div.problemindexholder");
                System.out.println(problemContext);


                String updateString =
                        "update cf.problem " +
                                "set problemcontext = ? where problemId = ?";


                PreparedStatement updateProblem = con.prepareStatement(updateString);
                updateProblem.setString(1, problemContext.toString());
                updateProblem.setString(2, rs.getString("problemId"));
                updateProblem.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
            Thread.sleep(1684);

            try {
                stmtUpdate.executeUpdate(
                        " UPDATE  cf.problem " +
                                " set " +
                                " `problemcontext` = '" + problemContext + "'" +
                                " WHERE problemId = " + rs.getString("problemId"));
            } catch (Exception e) {
                e.printStackTrace();
                BufferedWriter bw = new BufferedWriter(new FileWriter("logFailUpdate6Field", true));
                bw.write(rs.getString("id"));
                bw.newLine();
                bw.close();
            }


        }
    }
    System.out.println(d.printResult(10));
}



import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
	//crawled files from codeforces
    static String Base = "E:\\mining\\";
    static String rawDataPath = "E:\\codeForces\\cr\\";
    static Connection con;
    static Statement stmt;
    static int totalIgnorSql = 0;
    static int totalIgnorHtml = 0;
    static Map<String, String> datas;
    static int totalSiteError = 0;
    static String name = null;

    public static void main(String[] args) throws IOException {


        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cf?autoReconnect=true&useSSL=false", "root", "******");
            stmt = con.createStatement();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

		//read each file from a list that containing file names
        String namesFile = "C:\\Users\\Amirabbas\\Desktop\\Majd Files\\getFilesNameInDir\\intersect.txt";
        Scanner scanner = new Scanner(new File(namesFile));
        int counterLoop = 0;
        while (scanner.hasNext()) {
            counterLoop++;

            if (counterLoop % 10 == 0) {
                System.out.println("counterLoop : " + counterLoop);
                System.out.println("totalIgnor : " + totalIgnorSql);
                System.out.println("totalIgnorHtml : " + totalIgnorHtml);
                System.out.println("totalSiteError : " + totalSiteError);
                System.out.println("---------------------------------------");
            }
            Element html = null;
            Document doc = null;
            try {
                name = scanner.nextLine();
                File input = new File(rawDataPath + name);
                doc = Jsoup.parse(input, "UTF-8");
                html = doc.getElementsByTag("div").first();
            } catch (Exception e) {
                write(name);
                totalIgnorHtml++;
            }


            if (html == null) {
            } else {
                cleanData(doc);
            }
        }



        try {
            con.close();
        } catch (Exception e) {
            totalIgnorSql++;
        }
        System.out.println("*****************************************");
        System.out.println("counterLoop : " + counterLoop);
        System.out.println("totalIgnor : " + totalIgnorSql);
        System.out.println("totalIgnorHtml : " + totalIgnorHtml);
        System.out.println("totalSiteError : " + totalSiteError);
        System.out.println("*****************************************");
    }

    private static void cleanData(Document doc) {
        extractDataTable(doc);
        extractSourceCode(doc);
        insertIntoDb();


        try {
            if (datas.get("Verdict").toString().equals("Accepted")) {
                extractTestCase(doc);
            }
        } catch (Exception e) {
            write(name);
            totalIgnorHtml++;
        }
    }

    private static void extractTestCase(Document doc) {
        Elements roundboxs = null;
        try {
            roundboxs = doc.select(".roundbox");
        } catch (Exception e) {
            write(name);
            totalIgnorHtml++;
        }

        PreparedStatement prestmt = null;
        int idPro = -1;
		//check problem id
        try {
            prestmt = con.prepareStatement("SELECT * from `problem` WHERE `name` = ? ");
            prestmt.setString(1, datas.get("Problem").toString());
            prestmt.execute();
            ResultSet rs = prestmt.getResultSet();

            while (rs.next()) {
                idPro = rs.getInt(1);
            }
            if (idPro == -1)
                return;
        } catch (Exception e) {
            write(name);
            totalIgnorSql++;
            return;
        }

        PreparedStatement testCaseStmnt = null;
		//check if test cases for this problem inserted before or not
        try {
            testCaseStmnt = con.prepareStatement("Select * from `testCase` where problemId = ?   limit 1");
            testCaseStmnt.setString(1, String.valueOf(idPro));
            testCaseStmnt.execute();
            ResultSet testRS = testCaseStmnt.getResultSet();
            if (testRS.next()) {
                return;
            }
        } catch (Exception e) {
            write(name);
            totalIgnorSql++;
        }

        PreparedStatement pstmt = null;
		//add test cases to DB
        try {
            pstmt = con.prepareStatement("INSERT INTO testCase ( `inputdata` , `expectedResult` , `problemId`) VALUES (? , ? , ? )");


            for (Element e : roundboxs) {

                Element in = e.select(".input-view .text").first();
                Element answer = e.select(".answer-view .text").first();
                Element verdic = e.select(".infoline").last();


                if (verdic != null) {
                    if (in.text().length() < 590 && answer.text().length() < 590) {
                        pstmt.setString(1, in.text());
                        pstmt.setString(2, answer.text());
                        pstmt.setString(3, String.valueOf(idPro));
                        pstmt.addBatch();
                    }

                }
            }
            pstmt.executeBatch();
            pstmt.close();
        } catch (Exception e) {
            write(name);
            totalIgnorSql++;
        }
    }

    private static void extractDataTable(Document doc) {
        datas = new HashMap<>();
        try {
            Elements ths = doc.select(".datatable table th");
            Elements tds = doc.select(".datatable table td");
            for (int i = 0; i < ths.size() - 2; i++) {
                String a = ths.get(i).text();
                String b = tds.get(i).text();
                datas.put(a, b);
            }

        } catch (Exception e) {
            write(name);
            totalIgnorHtml++;
        }
    }

    private static void extractSourceCode(Document doc) {
        try {
            Elements elements = doc.getElementsByClass("program-source");
            Element element = elements.first();
            datas.put("source", element.text());
        } catch (Exception e) {
            write(name);
//            totalIgnor++;
        }
    }

    private static int insertIntoDb() {


        String mem = null;
        String tim = null;
        String userName = null;
        PreparedStatement pstmt = null;
        try {
            mem = datas.get("Memory").toString().toLowerCase().replace("kb", "").replace("mb", "");
            tim = datas.get("Time").toString().toLowerCase().replace("ms", "").replace("s", "");
            userName = datas.get("Author").substring(datas.get("Author").indexOf(":") + 2, datas.get("Author").length());
			//call stored procedure from DB to add data  to multiple tables
            pstmt = con.prepareStatement("call mainInsert( ? , ? , ? , ? , ? , ? , ? , ? , ? )");
            pstmt.setString(1, datas.get("#"));
            pstmt.setString(2, mem);
            pstmt.setString(3, userName);
            pstmt.setString(4, datas.get("Problem"));
            pstmt.setString(5, datas.get("Lang"));
            pstmt.setString(6, tim);
            pstmt.setString(7, datas.get("Verdict"));
            pstmt.setString(8, datas.get("Sent"));
            pstmt.setString(9, datas.get("source"));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            write(name);
            totalIgnorSql++;

        }
        return 0;
    }
    public static void write(String filename){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("E:\\ignored files.txt", true));
            writer.append( name + "\n") ;
            writer.close();
        }catch (Exception e){
            System.out.println("can't write : " + name);
        }

    }
}

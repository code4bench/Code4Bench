import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.transform.sax.SAXSource;


public class main {

    static String acceptedFile = "source\\";
    static String wrongFile = "source\\";
    static ResultSet rs;
    static String scFileName = "source\\sc.bat";

    public static void main(String[] args) {

        //read data from database
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cf?autoReconnect=true&useSSL=false", "root", "yourPass");
            Statement stmt = con.createStatement();

            for (int counterMain = 0; counterMain < 1000; counterMain++) {
                System.out.println("******* counterMain : " + counterMain + " ******");
                rs = stmt.executeQuery(" SELECT * FROM cf.finaltable_c_cpp WHERE  asimw = -2  limit 50;");


                ArrayList<Integer> listFileName = new ArrayList<>();
                String FileNames = "";

                BufferedWriter bwSC;
                ArrayList<String> listSubmitaw = new ArrayList<>();
                ArrayList<String> listSubmitwa = new ArrayList<>();
                ArrayList<Integer> listSubA = new ArrayList<>();
                ArrayList<Integer> listSubW = new ArrayList<>();

                moveAndDeleteFiles(counterMain);

                for (int counterDatabase = 0; counterDatabase < 60 && rs.next(); counterDatabase++) {
                    listSubA.add(rs.getInt("subaccepted"));
                    listSubW.add(rs.getInt("subwrong"));

                    if (listFileName.indexOf(rs.getInt("subaccepted")) < 0) {
                        listFileName.add(rs.getInt("subaccepted"));
                        FileNames += " " + rs.getInt("subaccepted") + "  ";
                    }

                    if (listFileName.indexOf(rs.getInt("subwrong")) < 0) {
                        listFileName.add(rs.getInt("subwrong"));
                        FileNames += "  " + rs.getInt("subwrong") + "  ";
                    }

                    // write data in two different file

                    BufferedWriter bwAccepted = new BufferedWriter(new FileWriter(acceptedFile + rs.getString("subaccepted")));
                    bwAccepted.write(rs.getString("sourceaccepted"));
                    bwAccepted.close();

                    BufferedWriter bwWrong = new BufferedWriter(new FileWriter(wrongFile + rs.getString("subwrong")));
                    bwWrong.write(rs.getString("sourcewrong"));
                    bwWrong.close();

                    listSubmitaw.add(rs.getString("subaccepted") + "," + rs.getString("subwrong"));
                    listSubmitwa.add(rs.getString("subwrong") + "," + rs.getString("subaccepted"));
                }

                PrintWriter writer = new PrintWriter(new File(scFileName));
                writer.print("");
                writer.close();

                bwSC = new BufferedWriter(new FileWriter(scFileName, true));
                bwSC.append("cd source");
                bwSC.newLine();
                bwSC.append("perl moss.bat ");
                bwSC.append(FileNames);
                bwSC.append(" > res.txt");
                bwSC.newLine();
                bwSC.append("exit");
                bwSC.close();

                String query = "  UPDATE  finaltable_c_cpp " +
                        "  SET     " +
                        "  asimw = ? ," +
                        "  wsima = ? , " +
                        "  matchlines = ?  " +
                        "  WHERE  subaccepted =  ?  AND   subwrong = ? ";


                int HowManyUpdate = 0;
                HashMap<String, String> r = getPercentageOfDiff();
                if (r.size() > 0) {
                    for (int c = 0; c < listSubmitaw.size(); c++) {
                        if (r.containsKey(listSubmitaw.get(c))) {
                            HowManyUpdate++;
                            PreparedStatement Upstmt = con.prepareStatement(query);
                            Upstmt.setInt(1, Integer.parseInt(r.get(listSubmitaw.get(c)).split(",")[0]));
                            Upstmt.setInt(2, Integer.parseInt(r.get(listSubmitaw.get(c)).split(",")[1]));
                            Upstmt.setInt(3, Integer.parseInt(r.get(listSubmitaw.get(c)).split(",")[2]));
                            Upstmt.setInt(4, Integer.parseInt(listSubmitaw.get(c).split(",")[0]));
                            Upstmt.setInt(5, Integer.parseInt(listSubmitaw.get(c).split(",")[1]));
                            Upstmt.executeUpdate();
                        }
                    }

                    for (int c = 0; c < listSubmitwa.size(); c++) {
                        if (r.containsKey(listSubmitwa.get(c))) {
                            HowManyUpdate++;
                            PreparedStatement Upstmt = con.prepareStatement(query);
                            Upstmt.setInt(1, Integer.parseInt(r.get(listSubmitwa.get(c)).split(",")[1]));
                            Upstmt.setInt(2, Integer.parseInt(r.get(listSubmitwa.get(c)).split(",")[0]));
                            Upstmt.setInt(3, Integer.parseInt(r.get(listSubmitwa.get(c)).split(",")[2]));
                            Upstmt.setInt(4, Integer.parseInt(listSubmitwa.get(c).split(",")[1]));
                            Upstmt.setInt(5, Integer.parseInt(listSubmitwa.get(c).split(",")[0]));
                            Upstmt.executeUpdate();
                        }
                    }

                    String queryNOsim = "  UPDATE  finaltable_c_cpp " +
                            "  SET     " +
                            "  asimw = -5 ," +
                            "  wsima = -5 , " +
                            "  matchlines = -5  " +
                            "  WHERE  subaccepted =  ?  AND   subwrong = ?  and  (asimw = -3 or asimw = -1 or asimw = -2)";

                    System.out.println("size A : " + listSubA.size());
                    System.out.println("size W : " + listSubW.size());

                    for (int n = 0; n < listSubA.size(); n++) {
//                        System.out.println("start update " + n);
                        PreparedStatement Upstmt = con.prepareStatement(queryNOsim);
                        Upstmt.setInt(1, listSubA.get(n));
                        Upstmt.setInt(2, listSubW.get(n));
                        Upstmt.executeUpdate();
                    }
                } else {
                    HowManyUpdate = -1;
                }
                System.out.println("HowManyUpdate : " + HowManyUpdate);
                moveAndDeleteFiles(counterMain);
                try {
                    System.out.println("go to sleep");
                    Thread.sleep(100000);
                    System.out.println("weak up");
                } catch (Exception e) {
                    System.out.println("got interrupted!");
                }
            }


            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public static void moveAndDeleteFiles(int counterMain) {
        try {
            File moveRes = new File("source\\res.txt");
            if (moveRes.exists()) {
                File resFiles = new File("..\\resultFiles");
                moveRes.renameTo(new File("..\\resultFiles\\res" + resFiles.listFiles().length + ".txt"));
            }

            File files = new File("source");
            for (File f : files.listFiles()) {
                if (!f.getName().equals("moss.bat"))
                    f.delete();
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error in delete and moving files");
        }

    }

    public static HashMap<String, String> getPercentageOfDiff() throws IOException {
        String outputSimilarity = "source\\res.txt", Summary = "";
        myDataSimilarity result = new myDataSimilarity();

        if (Files.exists(Paths.get(outputSimilarity)))
            new File(outputSimilarity).delete();

        System.out.println("FILE EXIST : " + Files.exists(Paths.get(outputSimilarity)));
        while (!Files.exists(Paths.get(outputSimilarity))) {
            Process proc = Runtime.getRuntime().exec("cmd /c start  " + System.getProperty("user.dir") + "\\source\\sc.bat");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        HashMap<String, String> simResult = new HashMap<>();
        List<String> fileContent = Files.readAllLines(Paths.get(outputSimilarity));

        boolean flagHasResultLink = false;

        for (int i = fileContent.size() - 1; i > 0; i--) {
            if (fileContent.get(i).contains("http://moss.stanford.edu/results")) {
                flagHasResultLink = true;
                Document sourceDoc = null;
                try {
                    sourceDoc = Jsoup.connect(fileContent.get(i)).get();
                } catch (Exception e) {
                    System.out.println("can't read result file from Standford : " + e);
                    i = fileContent.size() - 1;
                    flagHasResultLink = false;
                    try {
                        Thread.sleep(2000);
                    } catch (Exception ee) {
                        System.out.println("got interrupted! (http request to sleep )");
                    }
                    continue;
                }
                Elements data = sourceDoc.select("td");
                int counterTD = 0;
                String res = "";
                String ids = "";
                if(data.size() == 0) {
                    if(sourceDoc.text().contains("No matches were found in your submission")){
                        simResult.put("" , "");
                    }
                }
                for (int j = 0; j < data.size(); j++) {
                    String t = data.get(j).text();
                    if (counterTD == 0) {
                        ids += t.substring(0, t.indexOf("(") - 1) + ",";
                        res += t.substring(t.indexOf("(") + 1, t.indexOf("%")) + ",";
                    }
                    if (counterTD == 1) {
                        ids += t.substring(0, t.indexOf("(") - 1);
                        res += t.substring(t.indexOf("(") + 1, t.indexOf("%")) + ",";
                    }
                    if (counterTD == 2) {
                        res += data.get(j).text();
                    }
                    counterTD++;
                    if (counterTD == 3) {
                        counterTD = 0;
                        simResult.put(ids, res);
                        res = "";
                        ids = "";
                    }
                }
                break;
            }
        }
        return simResult;
    }

    public static myData getLineDiff() throws IOException {
        Diff d = new Diff();
        return d.doDiff(wrongFile, acceptedFile);

    }

}

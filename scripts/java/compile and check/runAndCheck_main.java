import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.CORBA.PUBLIC_MEMBER;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class main {

    public static final String basePath = "files\\";
    public static final String sourcePath = "sourceFile\\";
    public static final String acceptedResultPath = "acceptedResult\\";
    public static final String inputsPath = "inputs\\";
    public static final String outputsPath = "outputs\\";
    public static final String statisticalResultPath = "statisticalResult\\";
    public static final String htmlFilePath = "htmlFiles\\";

    public static final String acceptedFileName = "sAccepted.cpp";
    public static final String rejectedFileName = "s.cpp";
    public static final String similarityFileName = "similarity.txt";
    public static final String urlFileName = "urls.csv";
    public static final String scriptFileName = "script.bat";

    public static final String SummaryFileName = "Summary.txt";

    public static String getScriptFullPath() {
        return basePath + version + scriptFileName;
    }

    public static String getUrlFileFullPath() {
        return urlFileName;
    }

    public static String version = "c0\\";

    public static String getRejectedFileFullPath() {
        return basePath + version + sourcePath + rejectedFileName;
    }

    public static String getAcceptedFileFullPath() {
        return basePath + version + sourcePath + acceptedFileName;
    }

    public static String getInputsFullPath() {
        return basePath + version + inputsPath;
    }

    public static String getOutputsFullPath() {
        return basePath + version + outputsPath;
    }

    public static String getAcceptedResultFullPath() {
        return basePath + version + acceptedResultPath;
    }

    public static String getStatisticalResultPath() {
        return basePath + version + statisticalResultPath;
    }

    public static String getSourceFolderFullPath() {
        return basePath + version + sourcePath;
    }

    public static String getHtmlFilesFullPath() {
        return basePath + version + htmlFilePath;
    }

    public static String Summary = "";


    public static boolean generateScript() throws IOException {

        //generating the script file to compile and run test cases
        int fileCounter = new File(getInputsFullPath()).listFiles().length;

        FileWriter f = new FileWriter(getScriptFullPath());
        f.write("cd " + basePath + version + " \n");
        f.write("echo \">>>>>>>>compiling\"\n");
        f.write("g++ -fprofile-arcs -ftest-coverage " + " sourceFile\\s.cpp " + "  -o " + "s.exe\n");

        for (int i = 0; i < fileCounter; i++) {
            f.write("echo \">>>>>>>>runing test" + i + "\"\n");
            f.write("s.exe < " + " inputs\\t" + i + ".txt > " + "outputs\\" + "t" + i + ".txt\n");
            f.write("gcov " + " s.cpp\n");
            f.write("mkdir " + " gcovTrace\\" + (i + 1) + "\n");
            f.write("copy s.cpp.gcov " + "gcovTrace\\" + (i + 1) + "\n");

        }
        f.write("exit");
        f.close();

        return true;
    }

    public static boolean createFolders() {
		//all files have structured path
        File files = new File(basePath); //"files");
        if (!files.exists()) {
            files.mkdir();

        }

        File vers = new File(basePath + version);

        if (new File(basePath + version).exists())
            return true;

        vers.mkdir();

        File outputs = new File(getOutputsFullPath());//"files\\outputs");
        File inputs = new File(getInputsFullPath());//"files\\inputs");
        File sourceFile = new File(getSourceFolderFullPath());   //"files\\sourceFile");
        File acceptedResult = new File(getAcceptedResultFullPath());// "files\\acceptedResult");
        File result = new File(getStatisticalResultPath());


        outputs.mkdir();
        inputs.mkdir();
        sourceFile.mkdir();
        result.mkdir();
        acceptedResult.mkdir();

        return vers.exists() && files.exists() && outputs.exists() && inputs.exists() && sourceFile.exists() && acceptedResult.exists() && result.exists();
    }
    public  static AtomicBoolean finished  = new AtomicBoolean();
    public static boolean runScript() throws IOException {

        main.finished.set(false);
        File files = new File(getInputsFullPath()); //"files");

        Thread tScriptRunner = new Thread(new scriptRunnerClass(), "t1");
        Thread tKill = new Thread(new scriptKillerClass() , "killing");
        tScriptRunner.start();
        tKill.start();


        while (main.finished.get() == false){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String FileToString(String fileName) throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            return "";
        }

        List<String> temp = null ;

        String filetext = "";
        try {
            if( new File(fileName ).length() > 1000)
                return "" ;
            temp = Files.readAllLines(Paths.get(fileName));
            for (int j = 0; j < temp.size(); j++) {
                filetext += temp.get(j);
            }
        }catch (Exception e){

        }

        return filetext;
    }

    public static boolean createResultVector() throws IOException {
        File inputFolder = new File(getInputsFullPath()); //"files\\inputs");
        String result = "";
        int successful = 0, countTestCase;
        countTestCase = inputFolder.listFiles().length;

        if (countTestCase == 0)
            return false;

        for (int i = 0; i < countTestCase; i++) {
            String textFile1 = FileToString(getOutputsFullPath() + "t" + i + ".txt");
            String textFile2 = FileToString(getAcceptedResultFullPath() + "t" + i + ".txt");

            textFile1 = textFile1.replace("\r\n", "").replace(" ", "").replace("\n", "");
            textFile2 = textFile2.replace("\r\n", "").replace(" ", "").replace("\n", "");

            if (Objects.equals(textFile1, textFile2)) {
                if (result.length() == 0) {
                    result += "1";
                    successful++;
                } else {
                    result += ",1";
                    successful++;
                }
            } else {
                if (result.length() == 0)
                    result += "0";
                else
                    result += ",0";
            }
        }

        Summary += "total number of test case : " + countTestCase;
        Summary += "\n";
        Summary += "number of successful test case : " + successful + "  =>  " + ((float) successful / countTestCase);
        Summary += "\n";
        Summary += "number of fail test case : " + (countTestCase - successful) + "  =>  " + ((float) (countTestCase - successful) / countTestCase);
        Summary += "\n";
        Summary += "----------------------------------------------------------";
        Summary += "\n";

        File res = new File(basePath + version + "result.txt");
        FileWriter fw = new FileWriter(res);
        fw.write(result);
        fw.close();
        return true;
    }

    public static boolean runQt() throws IOException {

        FileWriter fw = new FileWriter("scRunQt.bat");
        fw.write(System.getProperty("user.dir") + "\\" + "GcovRunner.exe  " + System.getProperty("user.dir") + "\\" + basePath + version);
        fw.write("\nexit");
        fw.close();

        Process p = Runtime.getRuntime().exec("cmd /c start " + "scRunQt.bat");

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static boolean loadDataFromDB() {
        ResultSet rsTestCase;
        int problemId = 0;
        BufferedWriter bw = null;
        try {
            Statement stmtTestCase = con.createStatement();
            while (rs.next()) {

                version = rs.getString("subwrong") + "\\";
                System.out.print("Id -> " + rs.getString("id")+" ") ;
                if (Files.exists(Paths.get("csvFiles\\" + rs.getString("subwrong") + ".csv")) || new File(basePath + version).exists()) {
                    System.out.println( " skip : " + rs.getString("subwrong"));
                    continue;
                }

                if (!createFolders()) {
                    System.out.println("error code 1");
//                    return false;
                    System.exit(-2);
                }


                bw = new BufferedWriter(new FileWriter(getSourceFolderFullPath() + "s.cpp", true));
                bw.write(rs.getString("sourcewrong"));
                bw.close();

                bw = new BufferedWriter(new FileWriter(basePath + version + "s.cpp", true));
                bw.write(rs.getString("sourcewrong"));
                bw.close();

                problemId = rs.getInt("idproblembase");
                rsTestCase = stmtTestCase.executeQuery("SELECT * FROM cf.basetestcase WHERE proid = " + problemId);
                while (rsTestCase.next()) {
                    File files = new File(getInputsFullPath()); //"files");
                    String testCaseName = "t" + files.listFiles().length + ".txt";

                    String strIn = rsTestCase.getString("inputdata");
                    String strOut = rsTestCase.getString("expectedresult");

                    if (strIn.length() > 6)
                        if (strIn.substring(strIn.length() - 5, strIn.length()).contains("..."))
                            continue;
                    if (strOut.length() > 6)
                        if (strOut.substring(strOut.length() - 5, strOut.length()).contains("..."))
                            continue;

                    bw = new BufferedWriter(new FileWriter(getInputsFullPath() + testCaseName, true));
                    bw.write(rsTestCase.getString("inputdata"));
                    bw.close();

                    bw = new BufferedWriter(new FileWriter(getAcceptedResultFullPath() + testCaseName, true));
                    bw.write(rsTestCase.getString("expectedresult"));
                    bw.close();
                }
                return true;
            }
            flagDB = false;
            return true;
        } catch (
                Exception e)

        {
            e.printStackTrace();
            return false;
        }

    }

    public static ResultSet rs = null;
    public static Connection con = null;
    public static boolean flagDB = true;

    public static void main(String[] args) throws IOException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cf?autoReconnect=true&useSSL=false", "root", "1234");
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT finaltable_c_cpp.* FROM cf.finaltable_c_cpp , author , `user` where `user`.username = author.hashname and author.id = finaltable_c_cpp.author   order by finaltable_c_cpp.id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (flagDB) {

            if (!loadDataFromDB()) {
                continue;
//                return;
            }
            if (flagDB == false) {
                continue;
//                return;
            }

            if (!generateScript()) {
                System.out.println("error code 4");
                continue;
//                return;
            }


            if (!runScript()) {
                System.out.println("error code 5");
                continue;
//                return;
            }


            if (!createResultVector()) {
                System.out.println("error code 6");
                addToLogNoTestCase(version);
//                return;
            } else {

                if (!runQt()) {
                    System.out.println("error code 7");
                    continue;
//                    return;
                }
                if (!moveCsvResultFile()) {
                    System.out.println("error code 8");
                    continue;
//                    return;
                }
            }
        }
    }

    private static boolean moveCsvResultFile() {
        try {
            Thread.sleep(1000);
            Files.copy(Paths.get(System.getProperty("user.dir") + "\\" + basePath + version + "statisticalResult\\executableStatisticRankLine.csv"), Paths.get(System.getProperty("user.dir") + "\\" + "csvFiles\\" + rs.getString("subwrong") + ".csv"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void addToLogNoTestCase(String subwrong) {
        BufferedWriter writer = null;
        try {
            List<String> textFile = Files.readAllLines(Paths.get("LogNoTestCase.txt"));
            if (!textFile.contains(subwrong)) {
                writer = new BufferedWriter(new FileWriter("LogNoTestCase.txt", true));
                writer.append(subwrong);
                writer.newLine();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

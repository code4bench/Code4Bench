//this file will use by runAndCheck.java
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class scriptKillerClass implements Runnable {
    @Override
    public void run() {
        Process p = null;
        String str;
        int pid = 0;
        while (main.finished.get() == false) {
            try {
                Thread.sleep(7000);
                p = Runtime.getRuntime().exec("cmd /c start " + " getPid.bat ");
                InputStream is = p.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;

                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }

                str = Files.readAllLines(Paths.get("pid.txt")).get(0);
                str = str.replace(" ", "").replace("\t", "");

                if (str.matches("-?\\d+(\\.\\d+)?")) {
                    if (pid == Integer.parseInt(str)) {
                        Runtime.getRuntime().exec("cmd /c start " + " kill.bat ");
                    } else {
                        pid = Integer.parseInt(str);
                    }
                } else {
                    pid = -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}


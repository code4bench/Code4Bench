//this file will use by runAndCheck.java
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class scriptRunnerClass implements Runnable {
    public static Process p = null;

    @Override
    public void run() {

        main.finished.set(false);
        try {
            System.out.println(validateTestCases.getScriptFullPath());

            p = Runtime.getRuntime().exec("cmd /c start " + main.getScriptFullPath());

            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        main.finished.set(true);
    }
}

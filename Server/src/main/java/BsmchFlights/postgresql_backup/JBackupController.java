package BsmchFlights.postgresql_backup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@ConfigurationProperties(prefix = "j-backup-controller")
//@Configuration
@PropertySource("classpath:/application.properties")
public class JBackupController {
    @Autowired
    private Environment env;

    private String dbUrl;

    private String dbUsername;

    private String dbPassword;

    private String dbName;

//    @Value("${spring.datasource.url}")
//    private String dbUrl;
//
//    @Value("${spring.datasource.platform}")
//    private String dbName;
//
//    @Value("${spring.datasource.username}")
//    private String dbUsername;
//
//    @Value("${spring.datasource.password}")
//    private String dbPassword;

    public JBackupController() {

    }

    @PostConstruct
    public void init() {
        dbUrl = env.getProperty("spring.datasource.url");
        dbUsername = env.getProperty("spring.datasource.username");
        dbPassword = env.getProperty("spring.datasource.password");
        dbName = env.getProperty("spring.datasource.platform");
    }

    public void executeBackup() {
        init();
        File backupFilePath = new File(System.getProperty("user.home")
                + File.separator + "postgresql_backup");

        if (!backupFilePath.exists()) {
            File dir = backupFilePath;
            dir.mkdirs();
        }

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH-mm-ss");
        Date nowToday = new Date();

        String backupFileName = "pg_backup-" + sdfDate.format(nowToday) + "-"
                + sdfTime.format(nowToday) + ".sql";

//        String dbUrl = env.getProperty("spring.datasource.url");
//        String dbUsername = env.getProperty("spring.datasource.username");
//        String dbPassword = env.getProperty("spring.datasource.password");
//        String dbName = env.getProperty("spring.datasource.platform");

        String dbUrlSubstring = dbUrl.substring(dbUrl.indexOf('/'));

        ArrayList<String> commands = new ArrayList<>();
        commands.add("C:\\Program Files\\PostgreSQL\\14\\bin\\pg_dump.exe");
        commands.add("-h"); //database server host
        commands.add(dbUrlSubstring.substring(2, dbUrlSubstring.indexOf(':'))); //gets the IP
        commands.add("-p"); //database server port number
        commands.add("5432");
        commands.add("-U"); //connect as specified database user
        commands.add(dbUsername);
        commands.add("-F"); //output file format (custom, directory, tar, plain text (default))
        commands.add("c");
        commands.add("-b"); //include large objects in dump
        commands.add("-v"); //verbose mode
        commands.add("-f"); //output file or directory name
        commands.add(backupFilePath.getAbsolutePath() + File.separator + backupFileName);
        commands.add("-d"); //database name
        commands.add(dbName);

        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.environment().put("PGPASSWORD", dbPassword);

            Process process = pb.start();

            try (BufferedReader buf = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line = buf.readLine();
                while (line != null) {
                    System.err.println(line);
                    line = buf.readLine();
                }
            }

            process.waitFor();
            process.destroy();

            System.out.println("===> Success on backup process.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Exception: " + ex);
        }

        TimerTask backupTask = new TimerTask() {
            public void run(){
                executeBackup();
            }
        };

        Timer time = new Timer();
        time.scheduleAtFixedRate(backupTask, new Date(),1000*60*60*4);
    }
}

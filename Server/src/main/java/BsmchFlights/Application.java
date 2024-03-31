package BsmchFlights;

import BsmchFlights.postgresql_backup.JBackupController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@EnableConfigurationProperties({JBackupController.class})
public class Application {

    public static void main(String... args) {

        SpringApplication.run(Application.class, args);
//        JBackupController backupController = new JBackupController();
//        backupController.executeBackup();
    }
}

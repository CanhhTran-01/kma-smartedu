package KMA.SmartEdu;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KmaSmarteduApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // ép JVM đổi lại Timezone tương ứng PostgresQL 18
        SpringApplication.run(KmaSmarteduApplication.class, args);
    }
}

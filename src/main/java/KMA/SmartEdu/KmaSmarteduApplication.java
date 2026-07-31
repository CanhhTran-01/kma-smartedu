package KMA.SmartEdu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KmaSmarteduApplication {

    public static void main(String[] args) {
        SpringApplication.run(KmaSmarteduApplication.class, args);
    }
}

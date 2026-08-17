package KMA.SmartEdu;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KmaSmarteduApplication {

    public static void main(String[] args) {
        // Đồng bộ timezone của JVM với DB/test để tránh lệch timestamp giữa app, Hibernate và PostgreSQL.
        // Nếu bỏ dòng này, createdAt/updatedAt có thể bị lệch giờ giữa local, test và dữ liệu lưu trong DB.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(KmaSmarteduApplication.class, args);
    }
}

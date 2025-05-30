package westmeijer.oskar;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class EmbeddedPostgresConfig {

  @Bean
  public DataSource dataSource() throws IOException {
    return EmbeddedPostgres.builder()
        .start()
        .getPostgresDatabase();
  }
}


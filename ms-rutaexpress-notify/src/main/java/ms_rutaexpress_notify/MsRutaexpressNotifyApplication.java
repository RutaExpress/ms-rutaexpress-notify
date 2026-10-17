package ms_rutaexpress_notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan  // busca records/clases con @ConfigurationProperties (RetryProperties)
public class MsRutaexpressNotifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsRutaexpressNotifyApplication.class, args);
	}
}
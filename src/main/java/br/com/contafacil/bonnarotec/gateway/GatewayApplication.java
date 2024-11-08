package br.com.contafacil.bonnarotec.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan({"br.com.contafacil.shared.bonnarotec.toolslib.domain"})
@EnableJpaRepositories({
		"br.com.contafacil.shared.bonnarotec.toolslib.domain",
		"br.com.contafacil.bonnarotec.gateway.domain"
})
@EnableJpaAuditing
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}

package devops.tim9.agentcampaign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AgentCampaignApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgentCampaignApplication.class, args);
	}

}

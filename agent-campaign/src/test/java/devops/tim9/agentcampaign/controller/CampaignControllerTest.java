package devops.tim9.agentcampaign.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import devops.tim9.agentcampaign.config.JwtAuthenticationRequest;
import devops.tim9.agentcampaign.config.WebSecurityConfig;
import devops.tim9.agentcampaign.dto.MessageDto;
import devops.tim9.agentcampaign.dto.UserDto;
import devops.tim9.agentcampaign.model.Campaign;
import devops.tim9.agentcampaign.model.User;
import devops.tim9.agentcampaign.repository.CampaignRepository;
import devops.tim9.agentcampaign.repository.UserRepository;
import devops.tim9.agentcampaign.security.Authority;
import devops.tim9.agentcampaign.security.Role;
import devops.tim9.agentcampaign.security.UserTokenState;
import devops.tim9.agentcampaign.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, controlledShutdown = false, brokerProperties = { "listeners=PLAINTEXT://localhost:9093",
		"port=9093" })
public class CampaignControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;

	@Autowired
	private WebSecurityConfig webSecurityConfig;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CampaignRepository campaignRepository;

	@Test
	public void deleteCampaign_test_happy() throws Exception {
		User user1 = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.", "johnny",
				"johnny.web", "biography", false, true, true, "123"));
		UserTokenState userTokenState = webSecurityConfig
				.loginTesting(new JwtAuthenticationRequest("johnny", "123", null));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + userTokenState.getAccessToken());
		HttpEntity<Long> httpEntity = new HttpEntity<>(headers);
		Campaign campaign = campaignRepository
				.save(new Campaign(null, new ArrayList<>(), true, "1.8.2021.", null, 2, "F", "young", user1));
		ResponseEntity<MessageDto> responseEntity = testRestTemplate.exchange("/campaigns/" + campaign.getId(),
				HttpMethod.DELETE, httpEntity, MessageDto.class);
		assertEquals("Success", responseEntity.getBody().getHeader());
		assertEquals("Campaign is successfully deleted.", responseEntity.getBody().getMessage());

	}
	
	@Test
	public void getLoggedAgentsCampaigns_test_happy() throws Exception {
		User user1 = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.", "johnny2",
				"johnny.web", "biography", false, true, true, "123"));
		UserTokenState userTokenState = webSecurityConfig
				.loginTesting(new JwtAuthenticationRequest("johnny2", "123", null));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + userTokenState.getAccessToken());
		HttpEntity<Long> httpEntity = new HttpEntity<>(headers);
		Campaign campaign = campaignRepository
				.save(new Campaign(null, new ArrayList<>(), true, "1.8.2021.", null, 2, "F", "young", user1));
		ResponseEntity<Object> responseEntity = testRestTemplate.exchange("/campaigns",
				HttpMethod.GET, httpEntity, Object.class);
		@SuppressWarnings("unchecked")
		List<Campaign> campaigns = (List<Campaign>) responseEntity.getBody();
		assertEquals(1, campaigns.size());

	}
	
	@Test
	public void forMe_test_happy() throws Exception {
		User user1 = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.", "johnny3",
				"johnny.web", "biography", false, true, true, "123"));
		UserTokenState userTokenState = webSecurityConfig
				.loginTesting(new JwtAuthenticationRequest("johnny3", "123", null));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + userTokenState.getAccessToken());
		HttpEntity<Long> httpEntity = new HttpEntity<>(headers);
		Campaign campaign = campaignRepository
				.save(new Campaign(null, new ArrayList<>(), true, "1.8.2021.", null, 2, "F", "young", user1));
		ResponseEntity<Object> responseEntity = testRestTemplate.exchange("/campaigns/forMe",
				HttpMethod.GET, httpEntity, Object.class);
		@SuppressWarnings("unchecked")
		List<Campaign> campaigns = (List<Campaign>) responseEntity.getBody();
		assertEquals(1, campaigns.size());

	}

}

package devops.tim9.agentcampaign.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import devops.tim9.agentcampaign.dto.UserDto;
import devops.tim9.agentcampaign.model.Campaign;
import devops.tim9.agentcampaign.model.User;
import devops.tim9.agentcampaign.repository.CampaignRepository;
import devops.tim9.agentcampaign.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, controlledShutdown = false, brokerProperties = { "listeners=PLAINTEXT://localhost:9093",
		"port=9093" })
@Transactional
public class CampaignServiceTest {
	@Autowired
	UserService userService;

	@Autowired
	CampaignService campaignService;

	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	private AuthenticationManager authenticationManager;

	@MockBean
	private ImageStorageService imageStorageService;

	@Test
	public void deleteCampaign_test_happy() throws Exception {
		User user = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.",
				"johnny", "johnny.web", "biography", false, true, true, "123"));
		Campaign campaign = campaignRepository.save(new Campaign(null,new ArrayList<>(),true, "1.8.2021.", null, 2, "F", "young", user));
		Authentication authentication = Mockito.mock(Authentication.class);
		authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("johnny", "123"));
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		assertEquals(1, campaignRepository.findAll().size());
		campaignService.deleteCampaign(campaign.getId());
		assertEquals(0, campaignRepository.findAll().size());

	}
	
	@Test
	public void logegdAgentsCampaign_test_happy() throws Exception {
		User user = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.",
				"johnny2", "johnny.web", "biography", false, true, true, "123"));
		Campaign campaign = campaignRepository.save(new Campaign(null,new ArrayList<>(),true, "1.8.2021.", null, 2, "F", "young", user));
		Authentication authentication = Mockito.mock(Authentication.class);
		authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("johnny2", "123"));
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		assertEquals(1, campaignService.getLoggedAgentsCampaign().size());

	}
	
	@Test
	public void getCampaignsForUser_test_happy() throws Exception {
		User user = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.",
				"johnny3", "johnny.web", "biography", false, true, true, "123"));
		user.setFollowers(new ArrayList<>());
		userRepository.save(user);
		Campaign campaign = campaignRepository.save(new Campaign(null,new ArrayList<>(),true, "1.8.2021.", null, 2, "M", "young", user));
		Authentication authentication = Mockito.mock(Authentication.class);
		authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("johnny3", "123"));
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		assertEquals(1, campaignService.getCampaignsForUser().size());

	}
	
	@Test
	public void getCampaignsForUser_test_sad() throws Exception {
		User user = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.",
				"johnny4", "johnny.web", "biography", false, true, true, "123"));
		user.setFollowers(new ArrayList<>());
		userRepository.save(user);
		Campaign campaign = campaignRepository.save(new Campaign(null,new ArrayList<>(),true, "1.8.2021.", null, 2, "F", "old", user));
		Authentication authentication = Mockito.mock(Authentication.class);
		authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("johnny4", "123"));
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		assertEquals(0, campaignService.getCampaignsForUser().size());

	}
	
	@Test
	public void checkAgeGroup_test_happy() throws Exception {
		assertEquals("young", campaignService.getUserAgeGroup(2001));
		assertEquals("middle", campaignService.getUserAgeGroup(1966));
		assertEquals("old", campaignService.getUserAgeGroup(1940));
		
	}
	
	@Test
	public void checkTargetGroup_test_happy() throws Exception {
		User user = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.",
				"johnny5", "johnny.web", "biography", false, true, true, "123"));
		user.setFollowers(new ArrayList<>());
		userRepository.save(user);
		Campaign campaign = campaignRepository.save(new Campaign(null,new ArrayList<>(),true, "1.8.2021.", null, 2, "M", "old", user));
		Authentication authentication = Mockito.mock(Authentication.class);
		authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("johnny5", "123"));
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		assertTrue(campaignService.checkTargetGroup(campaign, user));
		
	}
	
	@Test
	public void checkTargetGroup_test_sad() throws Exception {
		User user = userService.registerUser(new UserDto("John", "Doe", "john@email.com", "0000", "M", "1.1.2001.",
				"johnny6", "johnny.web", "biography", false, true, true, "123"));
		user.setFollowers(new ArrayList<>());
		userRepository.save(user);
		Campaign campaign = campaignRepository.save(new Campaign(null,new ArrayList<>(),true, "1.8.2021.", null, 2, "F", "old", user));
		Authentication authentication = Mockito.mock(Authentication.class);
		authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("johnny6", "123"));
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		assertFalse(campaignService.checkTargetGroup(campaign, user));
		
	}

}

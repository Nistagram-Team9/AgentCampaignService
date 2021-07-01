package devops.tim9.agentcampaign.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import devops.tim9.agentcampaign.config.domain.FollowEvent;
import devops.tim9.agentcampaign.exception.ImageStorageException;
import devops.tim9.agentcampaign.model.Campaign;
import devops.tim9.agentcampaign.model.Commercial;
import devops.tim9.agentcampaign.model.User;
import devops.tim9.agentcampaign.repository.CampaignRepository;
import devops.tim9.agentcampaign.repository.CommercialRepository;
import devops.tim9.agentcampaign.repository.UserRepository;

@Service
public class CampaignService {

	private final CampaignRepository campaignRepository;
	private final CommercialRepository commercialRepository;
	private final UserRepository userRepository;
	private final ImageStorageService imageStorageService;
	private final ObjectMapper objectMapper;

	public CampaignService(CampaignRepository campaignRepository, CommercialRepository commercialRepository,
			UserRepository userRepository, ImageStorageService imageStorageService, ObjectMapper objectMapper) {
		this.campaignRepository = campaignRepository;
		this.commercialRepository = commercialRepository;
		this.userRepository = userRepository;
		this.imageStorageService = imageStorageService;
		this.objectMapper = objectMapper;
	}

	public void createCampaign(MultipartFile[] files, List<String> websites, Boolean isShortTerm, String startDate,
			String endDate, Integer howManyTimesADay, String sex, String ageGroup) throws ImageStorageException {
		User user = (User) userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		Campaign campaign = campaignRepository.save(new Campaign(null, new ArrayList<>(), isShortTerm, startDate,
				endDate, howManyTimesADay, sex, ageGroup, user));
		int i = 0;
		for (MultipartFile file : files) {
			String savedImagePath = imageStorageService.storeImage(file, campaign.getId());
			commercialRepository.save(new Commercial(null, campaign, savedImagePath, websites.get(i)));
			i++;

		}

	}

	public void deleteCampaign(Integer id) {
		User user = (User) userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		Optional<Campaign> campaign = campaignRepository.findById(id);
		if (campaign.isPresent()) {
			Campaign campaign2 = campaign.get();
			if (campaign2.getUser().getUsername().equals(user.getUsername())) {
				campaignRepository.delete(campaign2);
			}
		}

	}

	public List<Campaign> getLoggedAgentsCampaign() {
		User user = (User) userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		return campaignRepository.findByUser(user);
	}

	public List<Campaign> getCampaignsForUser() throws ParseException {
		User user = (User) userRepository
				.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		List<Campaign> campaigns = campaignRepository.findAll();
		List<Campaign> campaignsForUser = new ArrayList<>();
		for (Campaign campaign : campaigns) {
			Date date = new SimpleDateFormat("dd.MM.yyyy.").parse(campaign.getStartDate());
			if (date.after(new Date())) {
				if (campaign.getUser().getFollowers().contains(user) || checkTargetGroup(campaign, user)) {
					campaignsForUser.add(campaign);
				}
			}

		}
		return campaignsForUser;

	}

	public boolean checkTargetGroup(Campaign campaign, User user) throws ParseException {
		Date userBirthDate = new SimpleDateFormat("dd.MM.yyyy.").parse(user.getBirthDate());
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(userBirthDate);
		String userAgeGroup = getUserAgeGroup(calendar.get(Calendar.YEAR));
		if (campaign.getAgeGroup().equals(userAgeGroup) || campaign.getSex().equals(user.getSex())) {
			return true;
		}
		return false;
	}

	public String getUserAgeGroup(Integer year) {
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		Integer userYears = calendar.get(Calendar.YEAR)-year;
		if(0<=userYears && userYears<=30) {
			return "young";
		}else if(30<=userYears && userYears<65) {
			return "middle";
		}
		return "old";
	}
	
	public void createFollowing(FollowEvent followEvent) {
		User userFollowed = userRepository.findByUsername(followEvent.getUsernameFollowed());
		User userFollowing = userRepository.findByUsername(followEvent.getUsernameFollowedBy());
		userFollowed.getFollowers().add(userFollowing);
		userRepository.save(userFollowed);
		
	}
	
	@KafkaListener(topics = {"follow-events"})
	public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
		String value = consumerRecord.value();
		try {
			FollowEvent followEvent = objectMapper.readValue(value, FollowEvent.class);
			createFollowing(followEvent);			
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}
	
	
}
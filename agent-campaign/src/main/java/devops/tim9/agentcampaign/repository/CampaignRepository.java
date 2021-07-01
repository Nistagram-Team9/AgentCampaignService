package devops.tim9.agentcampaign.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import devops.tim9.agentcampaign.model.Campaign;
import devops.tim9.agentcampaign.model.User;

public interface CampaignRepository extends JpaRepository<Campaign, Integer>{
	
	List<Campaign> findByUser(User user);

}

package devops.tim9.agentcampaign.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import devops.tim9.agentcampaign.model.Commercial;

public interface CommercialRepository extends JpaRepository<Commercial, Integer> {

}

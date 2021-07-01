package devops.tim9.agentcampaign.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import devops.tim9.agentcampaign.security.VerificationToken;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	VerificationToken findByToken(String token);
}
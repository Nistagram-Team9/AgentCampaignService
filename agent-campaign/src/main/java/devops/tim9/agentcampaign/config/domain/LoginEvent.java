package devops.tim9.agentcampaign.config.domain;


import devops.tim9.agentcampaign.config.JwtAuthenticationRequestToSend;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginEvent {

	private Integer loginEventId;
	private JwtAuthenticationRequestToSend authenticationRequest;
}
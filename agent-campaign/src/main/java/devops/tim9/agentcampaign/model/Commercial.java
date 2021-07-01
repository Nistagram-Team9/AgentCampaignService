package devops.tim9.agentcampaign.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commercial {
	@Id
	@GeneratedValue
	private Integer id;
	
	@JsonIgnore
	@ManyToOne
	private Campaign campaign;
	
	private String imagePath;
	
	private String website;

}

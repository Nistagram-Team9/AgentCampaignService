package devops.tim9.agentcampaign.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@OneToMany(mappedBy="campaign", cascade=CascadeType.REMOVE)
	private List<Commercial> commercials;
	
	private Boolean isShortTerm;
	
	private String startDate;
	
	private String endDate;
	
	private Integer howManyTimesADay;
	
	private String sex;
	
	private String ageGroup;
	
	@ManyToOne
	private User user;

}

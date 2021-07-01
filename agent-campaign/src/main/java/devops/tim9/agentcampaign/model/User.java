package devops.tim9.agentcampaign.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import devops.tim9.agentcampaign.dto.UserDto;
import devops.tim9.agentcampaign.security.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails{
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String name;
	private String surname;
	private String email;
	private String phoneNumber;
	private String sex;
	private String birthDate;
	private String username;
	private String website;
	private String biography;
	private Boolean isPrivate;
	private Boolean canBeTagged;
	private Boolean isActive;
	private String password;
	
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
	private List<Authority> authorities = new ArrayList<>();
	
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<User> followers;
	
	
	@JsonIgnore
	@OneToMany(mappedBy="user")
	private List<Campaign> campaignes;
	
	
	public User(UserDto userDto) {
		this.name = userDto.getName();
		this.surname = userDto.getSurname();
		this.email = userDto.getEmail();
		this.phoneNumber = userDto.getPhoneNumber();
		this.sex = userDto.getSex();
		this.birthDate = userDto.getBirthDate();
		this.username = userDto.getUsername();
		this.website = userDto.getWebsite();
		this.biography = userDto.getBiography();
		this.isPrivate = userDto.getIsPrivate();
		this.canBeTagged = userDto.getCanBeTagged();
		this.isActive = userDto.getIsActive();
		
	}
	
	
	@Override
	public List<Authority> getAuthorities() {
		return this.authorities;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}


	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.password;
	}

}

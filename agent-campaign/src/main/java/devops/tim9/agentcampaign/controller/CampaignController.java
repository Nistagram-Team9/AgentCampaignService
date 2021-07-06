package devops.tim9.agentcampaign.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import devops.tim9.agentcampaign.dto.MessageDto;
import devops.tim9.agentcampaign.exception.ImageStorageException;
import devops.tim9.agentcampaign.model.Campaign;
import devops.tim9.agentcampaign.service.CampaignService;

@RestController
@RequestMapping(value = "/campaigns", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:4200")
public class CampaignController {

	private CampaignService campaignService;

	public CampaignController(CampaignService campaignService) {
		this.campaignService = campaignService;

	}

	@PostMapping
	public ResponseEntity<MessageDto> createCampaign(@RequestParam("files") MultipartFile[] files,
			@RequestParam List<String> websites, @RequestParam Boolean isShortTerm, @RequestParam String startDate,
			@RequestParam String endDate, @RequestParam Integer howManyTimesADay, @RequestParam String sex,
			@RequestParam String ageGroup) throws ImageStorageException {
		campaignService.createCampaign(files, websites, isShortTerm, startDate, endDate, howManyTimesADay, sex,
				ageGroup);
		return new ResponseEntity<>(new MessageDto("Success", "Campaign is successfully created."), HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<MessageDto> deleteCampaign(@PathVariable Integer id) {
		campaignService.deleteCampaign(id);
		return new ResponseEntity<>(new MessageDto("Success", "Campaign is successfully deleted."), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<Campaign>> getLoggedAgentsCampaign() {
		return new ResponseEntity<>(campaignService.getLoggedAgentsCampaign(), HttpStatus.OK);
	}

	@GetMapping(value = "/forMe")
	public ResponseEntity<List<Campaign>> getCampaignsForUser() throws ParseException {
		return new ResponseEntity<>(campaignService.getCampaignsForUser(), HttpStatus.OK);
	}

}

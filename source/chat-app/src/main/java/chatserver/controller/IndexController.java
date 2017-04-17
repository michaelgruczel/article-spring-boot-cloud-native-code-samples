package chatserver.controller;

import chatserver.model.ConcertInfo;
import chatserver.model.Message;
import chatserver.model.MessageRepository;
import chatserver.model.Weather;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

	private MessageRepository repository;

	@Autowired
	private DiscoveryClient discoveryClient;

	@LoadBalanced
	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Autowired
	RestTemplate restTemplate;

	public IndexController(MessageRepository repository) {
		this.repository = repository;
	}

	@RequestMapping("/")
	public String index(Model model) {

		List<Message> list = new ArrayList<>();
		repository.findAll().forEach(list::add);
		model.addAttribute("messages", list);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		model.addAttribute("message", new Message(currentPrincipalName, ""));
		return "index";
	}

	@RequestMapping(path = "/message", method = RequestMethod.POST)
	@HystrixCommand(fallbackMethod = "dataNotAvailable", commandProperties = {
        @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE"),
        @HystrixProperty(name = "execution.timeout.enabled", value = "false")})
	public String addMessage(Model model, @ModelAttribute Message message) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		message.setAuthor(currentPrincipalName);
		System.out.println("Store" + message.getContent() + " from " + message.getAuthor());

		if(message.getContent().startsWith("/weather") && message.getContent().split(" ").length > 1) {
			retrieveWeatherData(message);
		} else if(message.getContent().startsWith("/concerts") && message.getContent().split(" ").length > 1) {
			retrieveConcertData(message);
		}
		System.out.println("Store" + message.getContent() + " from " + message.getAuthor());
		
		repository.save(message);
		return "redirect:/";
	}

	private void retrieveWeatherData(@ModelAttribute Message message) {

		//Weather weather = restTemplate.getForObject("http://localhost:8090/weather?place=" + message.getContent().split(" ")[1], Weather.class);
		Weather weather = restTemplate.getForObject("http://weather-app/weather?place=" + message.getContent().split(" ")[1], Weather.class);
		message.setContent(message.getContent() + " - Weather:" + weather.getContent());
	}

  private void retrieveConcertData(@ModelAttribute Message message) {

		//ConcertInfo concertInfo = restTemplate.getForObject("http://localhost:8100/concerts?place=" + message.getContent().split(" ")[1], ConcertInfo.class);
		ConcertInfo concertInfo = restTemplate.getForObject("http://concert-app/concerts?place=" + message.getContent().split(" ")[1], ConcertInfo.class);
		message.setContent(message.getContent() + " - Concerts:" + concertInfo.getContent());
	}

	public String dataNotAvailable(Model model, @ModelAttribute Message message) {
		//Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//String currentPrincipalName = authentication.getName();
		//message.setAuthor(currentPrincipalName);
		//message.setContent(message.getContent() + " - currently no data available");
		//System.out.println("Store" + message.getContent() + " from " + message.getAuthor());
    //repository.save(message);
    System.out.println("fallback");
		return "redirect:/";
	}

	@RequestMapping("/flushdb")
	public String flushdb(Model model) {
		repository.deleteAll();
		return "redirect:/";
	}

}

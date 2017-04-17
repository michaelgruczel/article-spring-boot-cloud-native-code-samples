package demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// e.g. http://localhost:8090/weather?place=springfield
@RestController
public class ConcertInfoController {

    @Value("${WEATHER_APP_SERVICE_HOST}")
    private String weatherAppHost;
    
    @Value("${WEATHER_APP_SERVICE_PORT}")
    private String weatherAppPort;
    
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
    
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/concerts")
    @HystrixCommand(fallbackMethod = "responseWithoutWeather")
    public ConcertInfo weather(@RequestParam(value="place", defaultValue="") String place) {

            String weatherData = "";
            if(!place.isEmpty()) {
                //Weather weather = restTemplate.getForObject("http://localhost:8090/weather?place=" + place, Weather.class);
                Weather weather = restTemplate.getForObject("http://" + weatherAppHost + ":" + weatherAppPort + "/weather?place=" + place, Weather.class);
                weatherData = " - Weather:" + weather.getContent();
            }
            return new ConcertInfo(fakeConcertData(place) + weatherData);
    }

    public ConcertInfo responseWithoutWeather(@RequestParam(value="place", defaultValue="") String place) {
        return new ConcertInfo(fakeConcertData(place) + " - Weather: no data at the moment");
    }

    private String fakeConcertData(String place) {
        if(place.equalsIgnoreCase("hamburg")) {
            return "There is always cool stuff in Hamburg";
        } else if(place.equalsIgnoreCase("springfield")) {
            return "Clowns band on central park";
        } else if(!place.isEmpty()) {
            return "no concerts known in that town, but something is happening in hamburg and springfield";
        }
        return "Concert Help Service V 0.1, please use /concerts YOURTOWN";
    }

}

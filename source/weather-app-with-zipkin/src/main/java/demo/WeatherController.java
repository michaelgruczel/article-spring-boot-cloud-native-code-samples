package demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// e.g. http://localhost:8090/weather?place=springfield
@RestController
public class WeatherController {

    @RequestMapping("/weather")
    public Weather weather(@RequestParam(value="place", defaultValue="") String place) {
        if(place.equalsIgnoreCase("hamburg")) {
            return new Weather("cloudy, it's always cloudy in hamburg");
        } else if(place.equalsIgnoreCase("springfield")) {
            return new Weather("yellow skyline, strange");
        } else if(!place.isEmpty()) {
            return new Weather("no weather data for that town, we know hamburg and springfield");
        }
        return new Weather("weather service v0.4.2, please add your town to request");
    }

}

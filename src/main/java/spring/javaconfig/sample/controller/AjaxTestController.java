package spring.javaconfig.sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AjaxTestController {

	@RequestMapping("/test")
	public String test() {
		return "{\"message\" : \"ok\"}";
	}
}

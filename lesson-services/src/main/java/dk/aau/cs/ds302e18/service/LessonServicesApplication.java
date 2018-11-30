package dk.aau.cs.ds302e18.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LessonServicesApplication
{

	public static void main(String[] args) {
		SpringApplication.run(LessonServicesApplication.class, args);
	}
}

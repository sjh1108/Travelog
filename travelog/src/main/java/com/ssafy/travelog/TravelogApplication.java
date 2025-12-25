package com.ssafy.travelog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ssafy.travelog.model.mapper")
public class TravelogApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelogApplication.class, args);
	}

}

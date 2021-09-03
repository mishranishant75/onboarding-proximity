package com.example.demo.configClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RefreshScope
@RestController
public class ConfigClient {
	@Value("${msg:Hello default}")
	private String msg;

	@RequestMapping("/msg")
	String getMessage() {
		return this.msg;
	}
}

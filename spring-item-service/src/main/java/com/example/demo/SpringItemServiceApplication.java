package com.example.demo;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;

import com.example.demo.itemEntity.ItemEntity;
import com.example.demo.itemRepo.ItemRepo;

@EnableDiscoveryClient
@SpringBootApplication
public class SpringItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringItemServiceApplication.class, args);
	}
	
	@Bean
	public Consumer<String> feignEventSupplier() {
		System.out.println("inside consumer");
		return (message) -> {
			System.out.println(message);
		};
	}
	
}


@Component
class ItemInitializer implements CommandLineRunner{
	private final ItemRepo itemRepository;
	
	ItemInitializer(ItemRepo itemRepository){
		this.itemRepository = itemRepository;
	}
	
	@Override
	public void run(String... args) {
		Stream.of("Lining", "Puma", "Bad Boy", "Air Jordan", "Nike", "Adidas", "Reebok").
		forEach(item -> itemRepository.save(new ItemEntity(item)));
		
	}
}

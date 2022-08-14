package com.udacity.pricingservice;

import com.udacity.pricingservice.service.PricingService;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PricingServiceApplicationTests {
	@Test
	public void contextLoads() {
	}
	@Test
	public void getPricing() throws Exception {
		assertTrue(PricingService.getPrice(1L) != null);
	}

}

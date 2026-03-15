package com.example.prueba;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requiere MongoDB; ejecutar manualmente si hay instancia disponible")
@SpringBootTest
class PruebaApplicationTests {

	@Test
	void contextLoads() {
	}

}

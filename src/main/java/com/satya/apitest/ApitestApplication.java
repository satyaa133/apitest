package com.satya.apitest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@SpringBootApplication
public class ApitestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApitestApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) {
        return args -> {
            String url = "https://healthrx.co.in";
            
            Map<String, String> request = Map.of(
                "name", "Satya", 
                "regNo", "ADT23SOCB1558",
                "email", "satya@gmail.com" 
            );

            try {
                Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
                String token = (String) response.get("accessToken");
                System.out.println("Got Token: " + token);

                String sqlQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
                                  "(SELECT COUNT(*) FROM EMPLOYEE e2 WHERE e2.DEPARTMENT = e1.DEPARTMENT " +
                                  "AND e2.DOB > e1.DOB) AS YOUNGER_EMPLOYEES_COUNT " +
                                  "FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                                  "ORDER BY e1.EMP_ID DESC;";

                submitSolution(restTemplate, token, sqlQuery);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private void submitSolution(RestTemplate restTemplate, String token, String sqlQuery) {
        String submitUrl = "https://healthrx.co.in";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("finalQuery", sqlQuery);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        String result = restTemplate.postForObject(submitUrl, entity, String.class);
        System.out.println("Result: " + result);
    }
}

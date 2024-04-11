package com.example.ak.apm.apmdemo;

import org.springframework.web.bind.annotation.RestController;

import jakarta.jms.BytesMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RestController
public class HttpController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    private static final String QUEUE_NAME = "empq1";

    // create a logger using java.util.logging
    private static final Logger log = Logger.getLogger(HttpController.class.getName());

    @GetMapping("/")
    public String getMethodName(@RequestParam String name) {
        log.info("Saying hello to: " + name);
        return "hello " + name;
    }

    @PostMapping("/employee")
    public String postMethodName(@RequestParam String name, @RequestParam String position,
            @RequestParam String startDate) {

        log.info("Adding employee: " + name + " " + position + " " + startDate);

        Employee employee = new Employee();
        employee.setName(name);
        employee.setPosition(position);
        employeeRepository.save(employee);

        log.fine("Employee added: " + employee.toString());
        log.info("Writing to queue");

        jmsTemplate.send(QUEUE_NAME, session -> {
            BytesMessage message = session.createBytesMessage();
            message.setJMSMessageID(Integer.toString(employee.getEmployeeID()));
            message.writeUTF("Employee added: " + employee.getName());
            return message;
        });

        return "employee " + name + " " + position + " " + startDate;
    }

    @GetMapping("/employees")
    public String getEmployees() {
        // return all employees
        // read from database using EmployeeRepository
        log.info("Getting all employees");
        return employeeRepository.findAll().toString();
    }

    @GetMapping("/exception")
    public String getException(@RequestParam int num) {
        // return exception if num > 50
        if (num > 50) {
            log.severe("Exception: " + num + " is greater than 50");
            throw new RuntimeException("Number is greater than 50");
        }
        log.info("Number is less than 50: " + num);
        return "Number is less than 50: " + num;
    }

    @GetMapping(path = "/httpbin", produces = "application/json")
    public String getJsonPayload() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://httpbin.org/json";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCodeValue() == 200) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCode());
        }
    }

}

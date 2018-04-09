package com.s305089.software.login.controller;

import com.s305089.software.login.dao.ClientService;
import com.s305089.software.login.model.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class LoginController {
    private static final Logger log = LogManager.getLogger();

    private final ClientService service;
    @Autowired
    public LoginController(ClientService service) {
        this.service = service;
    }


    @PostMapping(value = "/signup")
    public ResponseEntity<Client> signup(@RequestBody Client newClient) {
        if(newClient.getEmail() == null || newClient.getPassword() == null || newClient.getAddress() == null) {
            log.info("New signup is not accepted: {}", newClient);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        Client client = service.findByEmail(newClient.getEmail());
        if (client == null) {
            client = service.save(newClient);
            log.info("New client has signed up: {}", client);
            return new ResponseEntity<>(client, HttpStatus.OK);
        }
        log.info("Client already exists: {}", client);
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @RequestMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<Client> getall() {
        return service.findAll();
    }
}

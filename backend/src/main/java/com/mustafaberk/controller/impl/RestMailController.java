package com.mustafaberk.controller.impl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mustafaberk.service.MailService;

@RestController
public class RestMailController {

	private final MailService mailService;

    public RestMailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/test-mail")
    public String testMail() {
        mailService.sendTestMail("berkmustafa2003@gmail.com");
        return "Mail gönderildi.";
    }
    
}

package com.example.certificateback.service.implementation;

import com.example.certificateback.dto.RecaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    private static final Logger LOG = LoggerFactory.getLogger(RecaptchaService.class);
    @Value("${google.recaptcha.secret}")
    private String secretKey;
    @Value("${google.recaptcha.url}")
    private String verifyUrl;
    @Value("${google.recaptcha.score-threshold}")
    private String threshold;

    public void checkResponse(String recaptcha) {

        RecaptchaResponse recaptchaResponse = this.validateToken(recaptcha);

        if(!recaptchaResponse.getSuccess()) {
            LOG.info("Invalid reCAPTCHA token");
            throw new BadCredentialsException("Invalid reCaptcha token");
        }
        else System.out.println("reCaptcha token is valid");

        if(recaptchaResponse.getScore() <= Double.parseDouble(this.threshold)){
            LOG.info("reCaptcha response: bot spotted");
            throw new BadCredentialsException("reCaptcha response: bot spotted");
        }
        else System.out.println("reCaptcha response: user is not a bot.");
    }

    private RecaptchaResponse validateToken(String recaptchaToken) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("secret", secretKey);
        map.add("response",recaptchaToken);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(map,headers);
        ResponseEntity<RecaptchaResponse> response = restTemplate.exchange(verifyUrl,
                HttpMethod.POST,
                entity,
                RecaptchaResponse.class);

        return response.getBody();
    }
}

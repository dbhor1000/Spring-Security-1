package Javacode.Spring.Security1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
class DefaultController {

    @GetMapping("/default")
    public ResponseEntity<String> getDefault() {

        return ResponseEntity.ok("Default response.");
    }

}

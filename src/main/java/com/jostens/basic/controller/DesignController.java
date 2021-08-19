package com.jostens.basic.controller;

import com.jostens.basic.domain.Design;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/designs")
public class DesignController {
    @RequestMapping(method = RequestMethod.GET, path="/{designId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Design> getDesign(@PathVariable Long designId) {
        Design d1 = new Design();
        d1.setDesignId(Long.valueOf(99009900));
        return new ResponseEntity<Design>(d1, HttpStatus.OK);
    }
}

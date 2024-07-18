package com.socialmedia.socialmedia.controller.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/image")
public class ImageController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadImage(@RequestParam("file") MultipartFile file) {

    }
}

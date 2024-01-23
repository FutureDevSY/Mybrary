package com.mybrary.backend.domain.bookshelf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bookshelf 컨트롤러", description = "Bookshelf Controller API")
@RestController
@RequestMapping("/api/v1/bookshelf")
public class BookshelfControllerV1 {

    /*
    * Bookshelf(책장)은 도메인으로 나누기 애매한것같다. 검토 필요
    * */

}

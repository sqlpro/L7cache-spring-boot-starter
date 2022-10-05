package com.example.demo.controller;

import com.example.demo.l7cache.L7Cache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CacheController {

	private final L7Cache l7Cache;

	@GetMapping("/renew/{key}")
	public ResponseEntity renew(@PathVariable("key") String key) {
		l7Cache.renew(key);
		return ResponseEntity.ok("SUCCESS");
	}
}

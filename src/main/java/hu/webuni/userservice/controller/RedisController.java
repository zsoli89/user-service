package hu.webuni.userservice.controller;

import hu.webuni.userservice.security.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/redis")
public class RedisController {

    private final RedisService redisService;

    @GetMapping("/add/exp/{username}/{token}")
    @ResponseStatus(HttpStatus.OK)
    public void addWithExpiration(@PathVariable String username, @PathVariable String token) {
        redisService.setValueWithExpiration(username, token, 60L);
    }

    @GetMapping("/find/{redisKey}")
    @ResponseStatus(HttpStatus.OK)
    public String addWithExpiration(@PathVariable String redisKey) {
        return redisService.getValueFromRedis(redisKey);
    }


}

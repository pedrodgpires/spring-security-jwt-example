package tech.pedropires.springsecurity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.pedropires.springsecurity.domain.repository.TweetRepository;
import tech.pedropires.springsecurity.domain.repository.UserRepository;
import tech.pedropires.springsecurity.domain.tweets.Tweet;
import tech.pedropires.springsecurity.domain.users.User;
import tech.pedropires.springsecurity.dto.CreateTweetDto;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tweets")
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController (TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }


    @PostMapping("/new")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto tweetDto,
                                            JwtAuthenticationToken token) {
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));
        if(user.isPresent()){
            Tweet tweet = new Tweet(user.get(), tweetDto.content());
            tweetRepository.save(tweet);
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Tweet cannot be created");
    }





}

package tech.pedropires.springsecurity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.pedropires.springsecurity.domain.repository.TweetRepository;
import tech.pedropires.springsecurity.domain.repository.UserRepository;
import tech.pedropires.springsecurity.domain.tweets.Tweet;
import tech.pedropires.springsecurity.domain.users.Role;
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
        // Find the user by the token
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));
        // Check if the user exists
        if(user.isPresent()){
            Tweet tweet = new Tweet(user.get(), tweetDto.content());
            tweetRepository.save(tweet);
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Tweet cannot be created");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId,
                                            JwtAuthenticationToken token) {
        // Find the user by the token
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));
        // Check if the user is an admin, then they can delete any tweet
        if(user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found");
        }
        boolean isAdmin = user.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));
        // Check if the tweet exists and get it
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tweet not found."));
        // Check if the user is the owner of the tweet or an admin to delete the tweet
        if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            tweetRepository.deleteById(tweetId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }




}

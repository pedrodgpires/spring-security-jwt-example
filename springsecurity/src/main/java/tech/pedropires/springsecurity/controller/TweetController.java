package tech.pedropires.springsecurity.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.pedropires.springsecurity.domain.repository.TweetRepository;
import tech.pedropires.springsecurity.domain.repository.UserRepository;
import tech.pedropires.springsecurity.domain.tweets.Tweet;
import tech.pedropires.springsecurity.domain.users.Role;
import tech.pedropires.springsecurity.domain.users.User;
import tech.pedropires.springsecurity.dto.CreateTweetDto;
import tech.pedropires.springsecurity.dto.FeedDto;
import tech.pedropires.springsecurity.dto.FeedItemDto;

import java.util.Optional;
import java.util.UUID;

/**
 * This class is a REST controller that handles requests related to Tweet entities.
 */
@RestController
@RequestMapping("/tweets")
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for the TweetController.
     *
     * @param tweetRepository The TweetRepository to use.
     * @param userRepository  The UserRepository to use.
     */
    public TweetController (TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new tweet for the user
     *
     * @param tweetDto the tweet dto
     * @param token the token of the user
     * @return a response entity with the status of the creation
     */
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

    /**
     * Delete a tweet by the tweetId, if the user is the owner of the tweet or an admin
     *
     * @param tweetId the id of the tweet
     * @param token the token of the user
     * @return a response entity with the status of the deletion
     */
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

    /**
     * Get the current feed of the user
     *
     * @param page the page number
     * @param size the size of the page
     * @return the feed of the user
     */
    @GetMapping("/feed")
    public ResponseEntity<FeedDto> getCurrentFeed(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        // Get the tweets from the repository and map them to the FeedItemDto
        // The PageRequest is used to get the tweets in a paginated way and sorted by the creationTimestamp
        Page<FeedItemDto> tweets = tweetRepository.findAll(
                        PageRequest.of(page, size, Sort.Direction.DESC, "creationTimestamp"))
                .map(tweet ->
                        new FeedItemDto(
                                tweet.getTweetId(),
                                tweet.getContent(),
                                tweet.getUser().getUsername()
                        )
                );
        return ResponseEntity.ok(new FeedDto(
                tweets.getContent(), page, size, tweets.getTotalPages(), tweets.getTotalElements()));
    }



}

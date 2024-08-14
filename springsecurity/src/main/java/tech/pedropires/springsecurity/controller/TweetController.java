package tech.pedropires.springsecurity.controller;

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
import tech.pedropires.springsecurity.dto.CreateTweetDto;
import tech.pedropires.springsecurity.dto.FeedDto;
import tech.pedropires.springsecurity.service.TweetService;

/**
 * This class is a REST controller that handles requests related to Tweet entities.
 */
@RestController
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    /**
     * Constructor for the TweetController.
     *
     * @param tweetService The TweetService to use.
     */
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    /**
     * Create a new tweet for the user
     *
     * @param tweetDto the tweet dto
     * @param token the token of the user
     * @return a response entity with the status of the request
     */
    @PostMapping("/new")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto tweetDto,
                                            JwtAuthenticationToken token) {
        boolean created = tweetService.createTweet(tweetDto, token);
        if (!created) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Tweet cannot be created");
        }
        return ResponseEntity.ok().build();
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
        try {
            boolean deleted = tweetService.deleteTweet(tweetId, token);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
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
        FeedDto feed = tweetService.getAllTweets(page, size);
        return ResponseEntity.ok(feed);
    }



}

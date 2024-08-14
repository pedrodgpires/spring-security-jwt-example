package tech.pedropires.springsecurity.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
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
 * This class is a service that handles requests related to tweets.
 */
@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new tweet for the user if the user exists
     *
     * @param tweetDto the tweet dto
     * @param token    the token of the user
     * @return a boolean indicating if the tweet was created
     */
    public boolean createTweet(CreateTweetDto tweetDto, JwtAuthenticationToken token) {
        // Find the user by the token
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));
        // Check if the user exists
        if (user.isEmpty()) {
            return false;
        } else {
            Tweet tweet = new Tweet(user.get(), tweetDto.content());
            tweetRepository.save(tweet);
            return true;
        }
    }

    /**
     * Delete a tweet if the user is the owner of the tweet or an admin
     *
     * @param tweetId the id of the tweet
     * @param token   the token of the user
     * @return a boolean indicating if the tweet was deleted
     */
    public boolean deleteTweet(Long tweetId, JwtAuthenticationToken token) {

        // Find the user by the token
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));
        // Check if the user is an admin, then they can delete any tweet
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        boolean isAdmin = user.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));
        // Check if the tweet exists and get it
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("Tweet not found"));
        // Check if the user is the owner of the tweet or an admin to delete the tweet
        if (isAdmin || isTweetOwner(tweet, token)) {
            tweetRepository.deleteById(tweetId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the user is the owner of the tweet
     *
     * @param tweet the tweet
     * @param token the token of the user
     * @return a boolean indicating if the user is the owner of the tweet
     */
    private boolean isTweetOwner(Tweet tweet, JwtAuthenticationToken token) {
        return tweet.getUser().getUserId().equals(UUID.fromString(token.getName()));
    }

    /**
     * Get all tweets in a paginated way
     *
     * @param page index of the page
     * @param size size of the page
     * @return a FeedDto with the tweets
     */
    public FeedDto getAllTweets(int page, int size) {
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
        // Return the FeedDto with the tweets
        return new FeedDto(
                tweets.getContent(), page, size, tweets.getTotalPages(), tweets.getTotalElements());
    }


}

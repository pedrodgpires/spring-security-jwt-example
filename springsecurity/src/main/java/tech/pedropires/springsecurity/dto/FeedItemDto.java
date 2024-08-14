package tech.pedropires.springsecurity.dto;

/**
 * Represents a feed item in the feed
 *
 * @param tweetId  The tweet id
 * @param content  The tweet content
 * @param username The username of the user that created the tweet
 */
public record FeedItemDto(long tweetId,
                          String content,
                          String username) {
}

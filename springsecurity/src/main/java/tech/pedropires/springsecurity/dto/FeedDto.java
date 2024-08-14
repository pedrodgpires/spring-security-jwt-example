package tech.pedropires.springsecurity.dto;

import java.util.List;

/**
 * Represents a feed in the system with a list of feed items
 *
 * @param feedItems     The feed items
 * @param page          The current page
 * @param pageSize      The page size
 * @param totalPages    The total number of pages
 * @param totalElements The total number of elements
 */
public record FeedDto(List<FeedItemDto> feedItems,
                      int page,
                      int pageSize,
                      int totalPages,
                      long totalElements) {
}

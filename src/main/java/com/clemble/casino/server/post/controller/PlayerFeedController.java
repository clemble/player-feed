package com.clemble.casino.server.post.controller;

import com.clemble.casino.WebMapping;
import com.clemble.casino.goal.post.GoalPost;
import com.clemble.casino.player.PlayerConnection;
import com.clemble.casino.player.PlayerPostWebMapping;
import com.clemble.casino.player.service.PlayerConnectionService;
import com.clemble.casino.player.service.PlayerFeedService;
import com.clemble.casino.post.PlayerPost;
import com.clemble.casino.server.ServerController;
import com.clemble.casino.server.event.share.SystemSharePostEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.post.repository.PlayerPostRepository;
import com.clemble.casino.social.SocialProvider;
import com.google.common.collect.ImmutableCollection;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by mavarazy on 11/30/14.
 */
@RestController
public class PlayerFeedController implements PlayerFeedService, ServerController {

    final private PlayerPostRepository postRepository;
    final private PlayerConnectionService connectionService;
    final private SystemNotificationService notificationService;

    public PlayerFeedController(
            PlayerConnectionService connectionService,
            PlayerPostRepository postRepository,
            SystemNotificationService notificationService) {
        this.postRepository = postRepository;
        this.connectionService = connectionService;
        this.notificationService = notificationService;
    }

    @Override
    public PlayerPost[] myFeed() {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = RequestMethod.GET, value = PlayerPostWebMapping.MY_POSTS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public PlayerPost[] myFeed(@CookieValue("player") String player) {
        // Step 1. Searching for connections
        Collection<PlayerConnection> connections = new ArrayList<PlayerConnection>(connectionService.getConnections(player));
        Collection<String> connectionIds = connections.stream().map((connection) -> connection.getPlayer()).collect(Collectors.toList());
        // Step 2. Fetching player connections
        return postRepository.findByPlayerInOrderByCreatedDesc(connectionIds).toArray(new PlayerPost[0]);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = PlayerPostWebMapping.GET_POSTS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public PlayerPost[] getFeed(@PathVariable("player") String player) {
        // Step 1. Fetching player connections
        return postRepository.findByPlayerOrderByCreatedDesc(player).toArray(new PlayerPost[0]);
    }

    @Override
    public PlayerPost share(String key, SocialProvider provider) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(method = RequestMethod.POST, value = PlayerPostWebMapping.POST_SHARE, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public PlayerPost share(@CookieValue("player")String player, @PathVariable("postKey") String key, @RequestBody SocialProvider provider) {
        // Step 1. Fetching post
        // Fix this
        GoalPost post = (GoalPost) postRepository.findOne(key);
        // Step 2. Share post using provider.
        notificationService.send(new SystemSharePostEvent(player, provider, post));
        return post;
    }

}

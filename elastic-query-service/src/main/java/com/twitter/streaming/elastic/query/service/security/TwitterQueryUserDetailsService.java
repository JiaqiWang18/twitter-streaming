package com.twitter.streaming.elastic.query.service.security;

import com.twitter.streaming.elastic.query.service.business.impl.TwitterQueryUserService;
import com.twitter.streaming.elastic.query.service.transformer.UserPermissionsToUserDetailTransformer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TwitterQueryUserDetailsService implements UserDetailsService {

    private final TwitterQueryUserService twitterQueryUserService;

    private final UserPermissionsToUserDetailTransformer transformer;

    public TwitterQueryUserDetailsService(TwitterQueryUserService twitterQueryUserService, UserPermissionsToUserDetailTransformer transformer) {
        this.twitterQueryUserService = twitterQueryUserService;
        this.transformer = transformer;
    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        return twitterQueryUserService
                .findAllPermissionsByUsername(username)
                .map(transformer::transform)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

package com.twitter.streaming.elastic.query.service.business.impl;
import com.twitter.streaming.elastic.query.service.dataaccess.entity.UserPermission;

import com.twitter.streaming.elastic.query.service.business.QueryUserService;
import com.twitter.streaming.elastic.query.service.dataaccess.repository.UserPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TwitterQueryUserService implements QueryUserService {

    private final UserPermissionRepository userPermissionRepository;

    public TwitterQueryUserService(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @Override
    public Optional<List<UserPermission>> findAllPermissionsByUsername(String username) {
        return userPermissionRepository.findPermissionsByUsername(username);
    }
}

package com.twitter.streaming.elastic.query.service.transformer;

import com.twitter.streaming.elastic.query.service.dataaccess.entity.UserPermission;
import com.twitter.streaming.elastic.query.service.security.PermissionType;
import com.twitter.streaming.elastic.query.service.security.TwitterQueryUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserPermissionsToUserDetailTransformer {
    public TwitterQueryUser transform(List<UserPermission> userPermissions) {
        return TwitterQueryUser.builder()
                .username(userPermissions.get(0).getUsername())
                .permissions(userPermissions.stream().collect(
                        java.util.stream.Collectors.toMap(
                                UserPermission::getDocumentId,
                                permission -> PermissionType.valueOf(permission.getPermissionType())
                        )
                ))
                .build();
    }

}

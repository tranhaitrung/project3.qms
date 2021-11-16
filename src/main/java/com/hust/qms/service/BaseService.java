package com.hust.qms.service;

import com.hust.qms.config.UserDetailsImpl;
import com.hust.qms.entity.PermissionUserRole;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class BaseService {

    public Long getCurrentId() {
        Map<String, Object> moreInfo = this.getMoreInfo();
        if (moreInfo != null && moreInfo.get("current_id") != null) {
            return Long.valueOf(String.valueOf(moreInfo.get("current_id")));
        }

        return null;
    }

    public List<String> getCurrentRoles() {
        Map<String, Object> moreInfo = this.getMoreInfo();
        List<String> roles = new ArrayList<String>();
        if (moreInfo != null && moreInfo.get("roles") != null) {
            roles.addAll((List<String>) moreInfo.get("roles"));
        }

        return roles;
    }

    public String getCurrentAccessToken() {
        Map<String, Object> moreInfo = this.getMoreInfo();
        if (moreInfo != null && moreInfo.get("access_token") != null) {
            return moreInfo.get("access_token").toString();
        }
        return null;
    }

    public Map<String, Object> getMoreInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

       Map<String, Object> moreInfo = new HashMap<>();
       moreInfo.put("current_id", userDetails.getId());
       moreInfo.put("current_username", userDetails.getUsername());
       moreInfo.put("roles", roles);
       moreInfo.put("access_toke", null);
        return moreInfo;
    }

    public String AUTHORIZATION_HEADER() {
        return "Bearer " + getCurrentAccessToken();
    }
}

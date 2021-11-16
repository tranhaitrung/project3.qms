package com.hust.qms.service;

import com.hust.qms.entity.PermissionUserRole;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class BaseService {
//    protected TokenStore tokenStore;

//    @Autowired
//    public BaseService(TokenStore tokenStore) {
//        this.tokenStore = tokenStore;
//    }
//
//    public Long getCurrentId() {
//        Map<String, Object> moreInfo = this.getMoreInfo();
//        if (moreInfo != null && moreInfo.get("rfr_id") != null) {
//            return Long.valueOf(String.valueOf(moreInfo.get("rfr_id")));
//        }
//
//        return null;
//    }
//
//    public List<String> getCurrentRoles() {
//        Map<String, Object> moreInfo = this.getMoreInfo();
//        List<String> roles = new ArrayList<String>();
//        if (moreInfo != null && moreInfo.get("roles") != null) {
//
//            ArrayList<PermissionUserRole> permissionUserRoles = (ArrayList<PermissionUserRole>) moreInfo.get("roles");
//            for (PermissionUserRole p : permissionUserRoles) {
//                roles.add(p.getRoleCode());
//            }
//        }
//
//        return roles;
//    }
//
//    public String getCurrentAccessToken() {
//        Map<String, Object> moreInfo = this.getMoreInfo();
//        if (moreInfo != null && moreInfo.get("access_token") != null) {
//            return moreInfo.get("access_token").toString();
//        }
//        return null;
//    }
//
//    public Map<String, Object> getMoreInfo() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) {
//            return null;
//        }
//
//        if (!(authentication.getDetails() instanceof OAuth2AuthenticationDetails)) {
//            return null;
//        }
//        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
//        String tokenValue = details.getTokenValue();
//        OAuth2AccessToken token = tokenStore.readAccessToken(tokenValue);
//        Map<String, Object> moreInfo = token.getAdditionalInformation();
//        moreInfo.put("access_token", token.getValue());
//        return moreInfo;
//    }
//
//    public String AUTHORIZATION_HEADER() {
//        return "Bearer " + getCurrentAccessToken();
//    }
}

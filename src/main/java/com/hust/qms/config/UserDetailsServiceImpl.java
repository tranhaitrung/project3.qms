package com.hust.qms.config;


import com.hust.qms.entity.PermissionUserRole;
import com.hust.qms.entity.User;
import com.hust.qms.repository.PermissionUserRoleRepository;
import com.hust.qms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.hust.qms.common.Const.Status.ACTIVE;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionUserRoleRepository permissionUserRoleRepository;

//    @Autowired
//    private UserDetailsServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        List<PermissionUserRole> permissionUserRoles = permissionUserRoleRepository.findAllByUserIdAndStatus(user.getId(), ACTIVE);
        return UserDetailsImpl.build(user, permissionUserRoles);
    }
}

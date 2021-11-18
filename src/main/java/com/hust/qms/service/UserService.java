package com.hust.qms.service;

import com.hust.qms.dto.UserDTO;
import com.hust.qms.entity.User;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hust.qms.common.Const.Status.ACTIVE;
import static com.hust.qms.exception.ServiceResponse.FORBIDDEN_RESPONSE;
import static com.hust.qms.exception.ServiceResponse.SUCCESS_RESPONSE;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private CheckRolesService checkRolesService;

    public ServiceResponse updateInfoUser(UserDTO userDTO) {
        Long userId;
        if (userDTO.getUserId() != null) {
            if (checkRolesService.authorizeRole("ADMIN,MANAGER,EMPLOYEE")) {
                userId = userDTO.getUserId();
            }else {
                return FORBIDDEN_RESPONSE("Bạn không đủ quyền để thực hiện chức năng này!");
            }
        }else {
            userId = baseService.getCurrentId();
        }

        User user = userRepository.findByIdAndStatus(userId, ACTIVE);

        user.setAddress(userDTO.getAddress());
        user.setAvatar(userDTO.getAvatar());
        user.setBirthday(userDTO.getBirthday());
        user.setCity(userDTO.getCity());
        user.setCountry(userDTO.getCountry());
        user.setCountryCode(userDTO.getCountryCode());
        user.setDisplayName(userDTO.getDisplayName());

        return SUCCESS_RESPONSE("Cập nhật thông tin thành công!", user);
    }

}

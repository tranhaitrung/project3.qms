package com.hust.qms.service;

import com.hust.qms.config.JwtUtils;
import com.hust.qms.config.UserDetailsImpl;
import com.hust.qms.dto.UserDTO;
import com.hust.qms.dto.VerifyDTO;
import com.hust.qms.entity.*;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.mail.EmailService;
import com.hust.qms.repository.*;
import com.hust.qms.response.JwtResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.rmi.activation.ActivationException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static com.hust.qms.common.Const.Role.*;
import static com.hust.qms.common.Const.Status.*;
import static com.hust.qms.common.Const.TypeVeriy.EMAIL;
import static com.hust.qms.exception.ServiceResponse.*;

@Service
public class AuthenService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerifyCodeRepository verifyCodeRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PermissionUserRoleRepository permissionUserRoleRepository;

    @Autowired
    private PermissionRoleRepository permissionRoleRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private BaseService baseService;

    @Autowired
    private CheckRolesService checkRolesService;

    @Autowired
    private CustomerRepository customerRepository;

    public Object login(UserDTO input) {

        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + input.getUsername()));

        if (INACTIVE.equals(user.getStatus())) {
            return BAD_RESPONSE("Vui lòng kích hoạt tài khoản bằng mã 6 chữ số!");
        }


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        JwtResponse jwtResponse = new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                user.getDisplayName(),
                user.getAvatar(),
                roles);

        return jwtResponse;
    }


    public ServiceResponse resisterCustomer(UserDTO input) {

        PermissionRole permissionRole = permissionRoleRepository.findByCode(CUSTOMER);

        if (userRepository.existsByUsername(input.getUsername())) {
            return BAD_RESPONSE("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(input.getUsername())) {
            return BAD_RESPONSE("Error: Email is already taken!");
        }

        String fullname = String.format("%s %s", input.getFirstName(), input.getLastName());

        User user = User.builder()
                .username(input.getUsername())
                .password(encoder.encode(input.getPassword()))
                .email(input.getUsername())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .fullName(fullname)
                .displayName(fullname)
                .status(INACTIVE)
                .avatar("https://res.cloudinary.com/litchitech/image/upload/v1638332250/PROJECT3/avatardefault_odgzm2.jpg")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        User success =  userRepository.save(user);

        PermissionUserRole permissionUserRole = PermissionUserRole.builder()
                .userId(success.getId())
                .roleId(permissionRole.getId())
                .roleCode(permissionRole.getCode())
                .status(ACTIVE)
                .manualAdd(0)
                .createdBy(success.getId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        permissionUserRoleRepository.save(permissionUserRole);

        UserDTO userDTO = UserDTO.builder()
                .userId(success.getId())
                .username(success.getUsername())
                .email(success.getEmail())
                .phone(success.getPhone())
                .status(success.getStatus())
                .firstName(success.getFirstName())
                .lastName(success.getLastName())
                .fullName(success.getFullName())
                .displayName(success.getFullName())
                .createdAt(success.getCreatedAt())
                .build();
        generateVerifyCode(userDTO);

        Customer customer = Customer.builder()
                .username(success.getUsername())
                .userId(success.getId())
                .email(success.getEmail())
                .status(INACTIVE)
                .firstName(success.getFirstName())
                .lastName(success.getLastName())
                .fullName(success.getFullName())
                .displayName(success.getDisplayName())
                .avatar("https://res.cloudinary.com/litchitech/image/upload/v1638332250/PROJECT3/avatardefault_odgzm2.jpg")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        customerRepository.save(customer);

        sendCode(userDTO);

        return SUCCESS_RESPONSE("Đăng ký tài khoản thành công!", userDTO);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse createMemberAccount(UserDTO input) {

        PermissionRole permissionRole = permissionRoleRepository.findByCode(input.getRoleCode());

        if (permissionRole == null) return BAD_RESPONSE("Mã phân quyền không hợp lệ!");

        if (ADMIN.equals(input.getRoleCode())) return FORBIDDEN_RESPONSE("Bạn có có quyền tạo vai trò ADMIN");

        if (MANAGER.equals(input.getRoleCode())) {
            if (!checkRolesService.authorizeRole("ADMIN"))
                return FORBIDDEN_RESPONSE("Bạn không có quyền tạo vai trò MANAGER");
        }

        if (userRepository.existsByUsername(input.getUsername())) {
            return BAD_RESPONSE("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(input.getUsername())) {
            return BAD_RESPONSE("Error: Email is already taken!");
        }

        String fullName = null;
        if (!StringUtils.isBlank(input.getFirstName()) && !StringUtils.isBlank(input.getLastName())) {
            fullName = String.format("%s %s", input.getFirstName(), input.getLastName());
        }

        User user = User.builder()
                .username(input.getUsername())
                .password(encoder.encode(input.getPassword()))
                .email(input.getUsername())
                .status(ACTIVE)
                .address(input.getAddress())
                .city(input.getCity())
                .district(input.getDistrict())
                .displayName(fullName)
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .fullName(fullName)
                .phone(input.getPhone())
                .countryCode(input.getCountryCode())
                .country(input.getCountry())
                .birthday(input.getBirthday())
                .avatar("https://res.cloudinary.com/litchitech/image/upload/v1638332250/PROJECT3/avatardefault_odgzm2.jpg")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        User success =  userRepository.save(user);

        PermissionUserRole permissionUserRole = PermissionUserRole.builder()
                .userId(success.getId())
                .roleId(permissionRole.getId())
                .roleCode(permissionRole.getCode())
                .status(ACTIVE)
                .manualAdd(0)
                .createdBy(success.getId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        permissionUserRoleRepository.save(permissionUserRole);

        UserDTO userDTO = UserDTO.builder()
                .userId(success.getId())
                .username(success.getUsername())
                .email(success.getEmail())
                .phone(success.getPhone())
                .build();
        generateVerifyCode(userDTO);

        Member member = Member.builder()
                .userId(success.getId())
                .username(success.getUsername())
                .address(success.getAddress())
                .city(input.getCity())
                .status(ACTIVE)
                .district(input.getDistrict())
                .displayName(fullName)
                .email(success.getEmail())
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .fullName(fullName)
                .phone(input.getPhone())
                .countryCode(input.getCountryCode())
                .country(input.getCountry())
                .birthday(input.getBirthday())
                .avatar("https://res.cloudinary.com/litchitech/image/upload/v1638332250/PROJECT3/avatardefault_odgzm2.jpg")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        memberRepository.save(member);

        String content = "Bạn đã được tạo tài khoản thành công với vai trò "+ input.getRoleCode() +" \nTài khoản đăng nhập: \n - username: "+input.getUsername()+"\n - password: "+input.getPassword();

        emailService.sendSimpleMessage(userDTO.getEmail(), "Tạo thành khoản thành công", content);

        return SUCCESS_RESPONSE("Tạo tài khoản nhân viên thành công!", success);
    }

    public ServiceResponse sendCode(UserDTO userDTO) {
        List<VerifyCode> verifyCode = verifyCodeRepository.findAllByUserIdAndStatus(userDTO.getUserId(), ACTIVE);
        String content = "Mã kích hoạt tài khoản của bạn là: " + verifyCode.get(0).getVerifyCode();
        emailService.sendSimpleMessage(userDTO.getEmail(), "Verify Account", content);
        return RESPONSE_MESSAGES("SUCCESS", HttpStatus.SC_OK, "Gửi mã xác nhận thành công");
    }

    public void generateVerifyCode(UserDTO userDTO) {
        String code = RandomStringUtils.randomNumeric(6);

        Long expire = System.currentTimeMillis()+60000*10;

        VerifyCode verifyCode = VerifyCode.builder()
                .userId(userDTO.getUserId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .verifyCode(code)
                .type(EMAIL)
                .status(ACTIVE)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .expiredAt(new Timestamp(expire))
                .build();
        verifyCodeRepository.save(verifyCode);
    }

    public ServiceResponse activeAccount(VerifyDTO input) {
        User user = userRepository.findByUsername(input.getUsername())
                .orElse(null);

        if (user == null) return BAD_RESPONSE("User Not Found with username: " + input.getUsername());

        List<VerifyCode> verifyCodes = verifyCodeRepository.findAllByUsernameAndStatusAndTypeAndVerifyCode(input.getUsername(), ACTIVE, EMAIL, input.getVerifyCode());

        if (verifyCodes == null || verifyCodes.size()==0) {
            return BAD_RESPONSE("Mã xác thực không đúng hoặc đã hết hạn!");
        }

        user.setStatus(ACTIVE);
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        Customer customer = customerRepository.findCustomerByUsername(input.getUsername());
        customer.setStatus(ACTIVE);
        customer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        customerRepository.save(customer);

        verifyCodeRepository.delete(verifyCodes.get(0));

        return SUCCESS_RESPONSE("Xác thực tài khoản thành công", user);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER,EMPLOYEE')")
    public ServiceResponse blockUser(UserDTO userDTO) {

        User user = userRepository.findByUsername(userDTO.getUsername()).orElse(null);
        if (user != null) {
            user.setStatus(BLOCK);
            user.setUpdatedBy(baseService.getCurrentId());
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            return SUCCESS_RESPONSE("block customer successfull",null);
        }
        return BAD_RESPONSE("Block customer fail!");
    }
}

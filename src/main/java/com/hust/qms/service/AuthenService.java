package com.hust.qms.service;

import com.hust.qms.config.JwtUtils;
import com.hust.qms.config.UserDetailsImpl;
import com.hust.qms.dto.UserDTO;
import com.hust.qms.dto.VerifyDTO;
import com.hust.qms.entity.*;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.mail.EmailService;
import com.hust.qms.repository.*;
import com.hust.qms.exception.JwtResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public ServiceResponse validateAccountActive(UserDTO input) {
        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Kh??ng t??m th???y t??i kho???n: " + input.getUsername()));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));

        UserDTO userDTO = UserDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
        if (user.getVerify_email() == 0) {
            generateVerifyCode(userDTO);
            sendCode(userDTO);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("emailVerify", user.getVerify_email());
        response.put("phoneVerify", user.getVerify_phone());
        return SUCCESS_RESPONSE("SUCCESS", response);
    }

    public ServiceResponse login(UserDTO input) {

        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Kh??ng t??m th???y t??i kho???n: " + input.getUsername()));

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
        return SUCCESS_RESPONSE("SUCCESS", jwtResponse);
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
                .verify_phone(0)
                .verify_email(0)
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

        return SUCCESS_RESPONSE("????ng k?? t??i kho???n th??nh c??ng!", userDTO);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse createMemberAccount(UserDTO input) {

        PermissionRole permissionRole = permissionRoleRepository.findByCode(input.getRoleCode());

        if (permissionRole == null) return BAD_RESPONSE("M?? ph??n quy???n kh??ng h???p l???!");

        if (ADMIN.equals(input.getRoleCode())) return FORBIDDEN_RESPONSE("B???n c?? c?? quy???n t???o vai tr?? ADMIN");

        if (MANAGER.equals(input.getRoleCode())) {
            if (!checkRolesService.authorizeRole("ADMIN"))
                return FORBIDDEN_RESPONSE("B???n kh??ng c?? quy???n t???o vai tr?? MANAGER");
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
                .verify_email(1)
                .verify_phone(1)
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
                .fullName(success.getFullName())
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

        String content = "B???n ???? ???????c t???o t??i kho???n th??nh c??ng v???i vai tr?? "+ input.getRoleCode() +" \nT??i kho???n ????ng nh???p: \n - username: "+input.getUsername()+"\n - password: "+input.getPassword();

        emailService.sendSimpleMessage(userDTO.getEmail(), "T???o th??nh kho???n th??nh c??ng", content);

        return SUCCESS_RESPONSE("T???o t??i kho???n nh??n vi??n th??nh c??ng!", userDTO);
    }

    public ServiceResponse sendCode(UserDTO userDTO) {
        List<VerifyCode> verifyCode = verifyCodeRepository.findAllByUserIdAndStatus(userDTO.getUserId(), ACTIVE);
        String content = "M?? k??ch ho???t t??i kho???n c???a b???n l??: " + verifyCode.get(0).getVerifyCode();
        emailService.sendSimpleMessage(userDTO.getEmail(), "Verify Account", content);
        return RESPONSE_MESSAGES("SUCCESS", HttpStatus.SC_OK, "G???i m?? x??c nh???n th??nh c??ng");
    }

    public ServiceResponse resendVerifyCode(UserDTO userDTO) {
        User user = userRepository.getUserByUsername(userDTO.getUsername());
        userDTO.setUserId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        generateVerifyCode(userDTO);
        sendCode(userDTO);
        return SUCCESS_RESPONSE("SUCCESS", null);
    }

    public void generateVerifyCode(UserDTO userDTO) {
        String code = RandomStringUtils.randomNumeric(6);

        Long expire = System.currentTimeMillis()+60000*5;

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

        if (user == null) return BAD_RESPONSE("Kh??ng t??m th???y t??i kho???n: " + input.getUsername());

        List<VerifyCode> verifyCodes = verifyCodeRepository.findAllByUsernameAndStatusAndTypeAndVerifyCode(input.getUsername(), ACTIVE, EMAIL, input.getVerifyCode());

        if (verifyCodes == null || verifyCodes.size()==0) {
            return BAD_RESPONSE("M?? x??c th???c kh??ng ????ng ho???c ???? h???t h???n!");
        }

        user.setStatus(ACTIVE);
        user.setVerify_email(1);
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        Customer customer = customerRepository.findCustomerByUsername(input.getUsername());
        customer.setStatus(ACTIVE);
        customer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        customerRepository.save(customer);

        verifyCodeRepository.delete(verifyCodes.get(0));

        return SUCCESS_RESPONSE("X??c th???c t??i kho???n th??nh c??ng", user);
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

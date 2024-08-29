package com.example.shopapp.services.user;


import com.example.shopapp.components.JwtTokenUtils;
import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.UpdateUserDTO;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.ExpiredTokenException;
import com.example.shopapp.exceptions.PermissionDenyException;
import com.example.shopapp.models.Role;
import com.example.shopapp.models.Token;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.RoleRepository;
import com.example.shopapp.repositories.TokenRepository;
import com.example.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final LocalizationUtils localizationUtils;
    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        //register user
        String phoneNumber = userDTO.getPhoneNumber();
        // Kiểm tra xem số điện thoại đã tồn tại hay chưa
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Số điện thoại đã tồn tại");
        }
        Role role =roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(
//                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));
                        "Role does not exists"));
        if(role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenyException("Không được phép đăng ký tài khoản Admin");
        }
        //convert from userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .email(userDTO.getEmail())
                .active(true)
                .build();

        newUser.setRole(role);

        // Kiểm tra nếu có accountId, không yêu cầu password
        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(
            String phoneNumber,
            String password,
            Long roleId
    ) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException("Wrong phone password");
        }
        //return optionalUser.get();//muốn trả JWT token ?
        User existingUser = optionalUser.get();
        //check password
        if (existingUser.getFacebookAccountId() == 0
                && existingUser.getGoogleAccountId() == 0) {
            if(!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Wrong phone password");
            }
        }
//        Optional<Role> optionalRole = roleRepository.findById(roleId);
//        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
//            throw new DataNotFoundException("Role does not exists");
//        }
        if(!optionalUser.get().isActive()) {
            throw new DataNotFoundException("User is locked");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password,
                existingUser.getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateStringToken(existingUser);
    }
    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Find the existing user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the phone number is being changed and if it already exists for another user
        String newPhoneNumber = updatedUserDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(newPhoneNumber) &&
                userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        // Update user information based on the DTO
        if (updatedUserDTO.getFullName() != null) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if (newPhoneNumber != null) {
            existingUser.setPhoneNumber(newPhoneNumber);
        }
        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if (updatedUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        }
        if (updatedUserDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(updatedUserDTO.getFacebookAccountId());
        }
        if (updatedUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
        }

        // Update the password if it is provided in the DTO
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            if(!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        //existingUser.setRole(updatedRole);
        // Save the updated user
        return userRepository.save(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }
    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }
}
























//private final UserRepository userRepository;
//private final RoleRepository roleRepository;
//private final PasswordEncoder passwordEncoder;
//private final JwtTokenUtils jwtTokenUtils;
//private final AuthenticationManager authenticationManager;
//private final TokenRepository tokenRepository;
//@Override
//public User createUser(UserDTO userDTO) {
//    if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())){
//        throw new DataIntegrityViolationException("Phone Number already exists");
//    }
//    User newUser=User.builder()
//            .fullName(userDTO.getFullName())
//            .phoneNumber(userDTO.getPhoneNumber())
//            .address(userDTO.getAddress())
//            .active(true)
//            .dateOfBirth(userDTO.getDateOfBirth())
//            .facebookAccountId(userDTO.getFacebookAccountId())
//            .googleAccountId(userDTO.getGoogleAccountId())
//            .email(userDTO.getEmail())
//            .profileImage(userDTO.getProfileImage())
//            .build();
//    Role role=roleRepository.findById(userDTO.getRoleID())
//            .orElseThrow(()->new RuntimeException("Role not found"));
//    newUser.setRole(role);
//    if(userDTO.getFacebookAccountId()==0 && userDTO.getGoogleAccountId()==0){
//        String password=userDTO.getPassword();
//        newUser.setPassword(passwordEncoder.encode(password));
//    }
//    return userRepository.save(newUser);
//}
//
//@Override
//public String login(String phoneNumber, String password) throws DataNotFoundException {
//    User user=userRepository.findByPhoneNumber(phoneNumber)
//            .orElseThrow(()->new DataNotFoundException("User not found"));
//    if (user.getFacebookAccountId()==0 && user.getGoogleAccountId()==0){
//        if (!passwordEncoder.matches(password,user.getPassword())){
//            throw new BadCredentialsException("Invalid phone number or password");
//        }
//    }
//    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
//            =new UsernamePasswordAuthenticationToken(phoneNumber,password);
//    //authenticate with spring security
//    authenticationManager.authenticate(usernamePasswordAuthenticationToken);
//    return jwtTokenUtils.generateStringToken(user);
//
//}
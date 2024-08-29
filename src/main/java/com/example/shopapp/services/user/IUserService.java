package com.example.shopapp.services.user;

import com.example.shopapp.dtos.UpdateUserDTO;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.User;
import jakarta.transaction.Transactional;

public interface IUserService {

    @Transactional
    User createUser(UserDTO userDTO) throws Exception;

    String login(
            String phoneNumber,
            String password,
            Long roleId
    ) throws Exception;

    @Transactional
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;
}

package com.example.shopapp.services.token;

import com.example.shopapp.models.Token;
import com.example.shopapp.models.User;
import jakarta.transaction.Transactional;

public interface ITokenService {
    @Transactional
    Token refreshToken(String refreshToken, User user) throws Exception;

    @Transactional
    Token addToken(User user, String token, boolean isMobileDevice);
}

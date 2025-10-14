package com.siewe_rostand.tvcam.auth.services;

import com.siewe_rostand.tvcam.auth.dto.AuthenticationRequest;
import com.siewe_rostand.tvcam.auth.dto.AuthenticationResponse;
import com.siewe_rostand.tvcam.auth.dto.ForgetPasswordForm;
import com.siewe_rostand.tvcam.auth.dto.RegisterRequest;
import com.siewe_rostand.tvcam.shared.Exceptions.UnAuthorizeException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author rostand
 * @project tv-cam
 */


public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    HttpResponse<Object> authenticate(AuthenticationRequest request);

    HttpResponse<Object> forgottenPassword(ForgetPasswordForm forgetPasswordForm);

    HttpResponse<Object> getUserInfo(HttpServletRequest request) throws UnAuthorizeException;
}


package com.siewe_rostand.tvcam.auth;

import com.siewe_rostand.tvcam.shared.HttpResponse;

/**
 * @author rostand
 * @project tv-cam
 */


public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    HttpResponse authenticate(AuthenticationRequest request);

    HttpResponse forgottenPassword(ForgetPasswordForm forgetPasswordForm);
}

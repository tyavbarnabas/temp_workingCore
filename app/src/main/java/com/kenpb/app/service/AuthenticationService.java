package com.kenpb.app.service;

import com.kenpb.app.dtos.ApiResponse;
import com.kenpb.app.dtos.RegistrationRequest;



public interface AuthenticationService {

    ApiResponse register(RegistrationRequest request);
}

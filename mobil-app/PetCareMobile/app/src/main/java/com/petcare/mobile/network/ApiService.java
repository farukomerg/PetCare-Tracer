package com.petcare.mobile.network;

import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.LoginRequest;
import com.petcare.mobile.model.LoginResponse;
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.model.RegisterRequest;
import com.petcare.mobile.model.UserResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<UserResponse>> register(@Body RegisterRequest request);

    @GET("api/pets/user/{userId}")
    Call<ApiResponse<List<PetResponse>>> getPetsByUserId(@Path("userId") long userId);
}

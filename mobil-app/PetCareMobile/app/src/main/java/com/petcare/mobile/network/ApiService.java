package com.petcare.mobile.network;

import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.AppointmentResponse;
import com.petcare.mobile.model.CreateAppointmentRequest;
import com.petcare.mobile.model.CreatePetRequest;
import com.petcare.mobile.model.LoginRequest;
import com.petcare.mobile.model.LoginResponse;
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.model.RegisterRequest;
import com.petcare.mobile.model.UserResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // ─── Auth ───
    @POST("api/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<UserResponse>> register(@Body RegisterRequest request);

    // ─── Pets ───
    @GET("api/pets/user/{userId}")
    Call<ApiResponse<List<PetResponse>>> getPetsByUserId(@Path("userId") long userId);

    @GET("api/pets/{petId}")
    Call<ApiResponse<PetResponse>> getPetById(@Path("petId") long petId);

    @POST("api/pets")
    Call<ApiResponse<PetResponse>> createPet(@Body CreatePetRequest request);

    @DELETE("api/pets/{petId}")
    Call<ApiResponse<Void>> deletePet(@Path("petId") long petId);

    // ─── Appointments ───
    @GET("api/appointments/pet/{petId}")
    Call<ApiResponse<List<AppointmentResponse>>> getAppointmentsByPetId(@Path("petId") long petId);

    @POST("api/appointments")
    Call<ApiResponse<AppointmentResponse>> createAppointment(@Body CreateAppointmentRequest request);

    @DELETE("api/appointments/{id}")
    Call<ApiResponse<Void>> deleteAppointment(@Path("id") long id);
}

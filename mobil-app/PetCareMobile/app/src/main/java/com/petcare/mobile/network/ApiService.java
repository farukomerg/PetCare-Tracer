package com.petcare.mobile.network;

import com.petcare.mobile.model.ApiResponse;
import com.petcare.mobile.model.AppointmentResponse;
import com.petcare.mobile.model.CreateAppointmentRequest;
import com.petcare.mobile.model.CreatePetRequest;
import com.petcare.mobile.model.CreateReminderRequest;
import com.petcare.mobile.model.LoginRequest;
import com.petcare.mobile.model.LoginResponse;
import com.petcare.mobile.model.PetResponse;
import com.petcare.mobile.model.RegisterRequest;
import com.petcare.mobile.model.ReminderResponse;
import com.petcare.mobile.model.UpdatePetRequest;
import com.petcare.mobile.model.UserResponse;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<UserResponse>> register(@Body RegisterRequest request);

    @GET("api/pets/user/{userId}")
    Call<ApiResponse<List<PetResponse>>> getPetsByUserId(@Path("userId") long userId);

    @GET("api/pets/{petId}")
    Call<ApiResponse<PetResponse>> getPetById(@Path("petId") long petId);

    @POST("api/pets")
    Call<ApiResponse<PetResponse>> createPet(@Body CreatePetRequest request);

    @PUT("api/pets/{petId}")
    Call<ApiResponse<PetResponse>> updatePet(@Path("petId") long petId, @Body UpdatePetRequest request);

    @DELETE("api/pets/{petId}")
    Call<ApiResponse<Void>> deletePet(@Path("petId") long petId);

    @GET("api/appointments")
    Call<ApiResponse<List<AppointmentResponse>>> getAllAppointments();

    @GET("api/appointments/pet/{petId}")
    Call<ApiResponse<List<AppointmentResponse>>> getAppointmentsByPetId(@Path("petId") long petId);

    @POST("api/appointments")
    Call<ApiResponse<AppointmentResponse>> createAppointment(@Body CreateAppointmentRequest request);

    @PUT("api/appointments/{id}")
    Call<ApiResponse<AppointmentResponse>> updateAppointment(@Path("id") long id, @Body CreateAppointmentRequest request);

    @DELETE("api/appointments/{id}")
    Call<ApiResponse<Void>> deleteAppointment(@Path("id") long id);

    /** Veteriner listesini getir */
    @GET("api/users/vets")
    Call<ApiResponse<List<UserResponse>>> getVets();

    /** Veteriner ID'sine göre randevuları getir */
    @GET("api/appointments/vet/{vetId}")
    Call<ApiResponse<List<AppointmentResponse>>> getAppointmentsByVetId(@Path("vetId") long vetId);

    /** Randevu durumunu güncelle (VET kullanır) */
    @PATCH("api/appointments/{id}/status")
    Call<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
            @Path("id") long id, @Body Map<String, String> body);

    // ─── Reminders (Hatırlatma) ───
    @GET("api/reminders/pet/{petId}")
    Call<ApiResponse<List<ReminderResponse>>> getRemindersByPetId(@Path("petId") long petId);

    @POST("api/reminders")
    Call<ApiResponse<ReminderResponse>> createReminder(@Body CreateReminderRequest request);

    @DELETE("api/reminders/{id}")
    Call<ApiResponse<Void>> deleteReminder(@Path("id") long id);
}

package dev.jianastrero.retrofitsample.api;

import dev.jianastrero.retrofitsample.model.dto.request.LoginDto;
import dev.jianastrero.retrofitsample.model.dto.response.RefreshTokenDto;
import dev.jianastrero.retrofitsample.model.dto.response.UserDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface UserApi {

    @POST("login")
    Call<RefreshTokenDto> login(@Body LoginDto user);

    @POST("refresh-token")
    Call<RefreshTokenDto> refreshToken();

    @GET("user/me")
    Call<UserDto> me();

    @PATCH("user/me")
    Call<UserDto> updateMe();

}

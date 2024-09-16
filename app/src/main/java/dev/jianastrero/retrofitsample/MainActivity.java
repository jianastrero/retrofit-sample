package dev.jianastrero.retrofitsample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;

import dev.jianastrero.retrofitsample.api.API;
import dev.jianastrero.retrofitsample.model.dto.request.LoginDto;
import dev.jianastrero.retrofitsample.model.dto.response.ErrorDto;
import dev.jianastrero.retrofitsample.model.dto.response.RefreshTokenDto;
import dev.jianastrero.retrofitsample.model.dto.response.UserDto;
import dev.jianastrero.retrofitsample.prefs.AppPreferences;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppPreferences.initialize(this);

        if (AppPreferences.getInstance().getAccessToken() == null) {
            login();
        } else {
            me();
        }
    }

    private void login() {
        API.userApi()
                .login(new LoginDto("username", "password"))
                .enqueue(new Callback<RefreshTokenDto>() {
                    @Override
                    public void onResponse(@NonNull Call<RefreshTokenDto> call, @NonNull Response<RefreshTokenDto> response) {
                        if (response.isSuccessful()) {
                            RefreshTokenDto refreshTokenDto = response.body();
                            if (refreshTokenDto != null) {
                                Log.d("JIANDDEBUG", "Access Token: " + refreshTokenDto.getAccessToken());
                                Log.d("JIANDDEBUG", "Token Type: " + refreshTokenDto.getTokenType());
                                AppPreferences.getInstance().setAccessToken(refreshTokenDto.getAccessToken());
                                me();
                            }
                        } else {
                            ResponseBody errorBody = null;
                            try {
                                errorBody = response.errorBody();
                                if (errorBody != null) {
                                    String json = errorBody.string();
                                    ErrorDto errorDto = API.gson.fromJson(json, ErrorDto.class);
                                    Toast.makeText(MainActivity.this, errorDto.getDetail(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (errorBody != null) {
                                    errorBody.close();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RefreshTokenDto> call, Throwable t) {
                        Log.e("JIANDDEBUG", "Failed to login", t);
                        Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void me() {
        API.userApi().me().enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(@NonNull Call<UserDto> call, @NonNull Response<UserDto> response) {
                if (response.isSuccessful()) {
                    UserDto userDto = response.body();
                    if (userDto != null) {
                        Log.d("JIANDDEBUG", "Username: " + userDto.getUsername());
                        Toast.makeText(MainActivity.this, "Hello, " + userDto.getUsername(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ResponseBody errorBody = null;
                    try {
                        errorBody = response.errorBody();
                        if (errorBody != null) {
                            String json = errorBody.string();
                            ErrorDto errorDto = API.gson.fromJson(json, ErrorDto.class);
                            Toast.makeText(MainActivity.this, errorDto.getDetail(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (errorBody != null) {
                            errorBody.close();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Log.e("JIANDDEBUG", "Failed to fetch user", t);
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

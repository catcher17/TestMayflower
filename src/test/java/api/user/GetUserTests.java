package api.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.qameta.allure.Description;
import okhttp3.Request;
import okhttp3.Response;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetUserTests extends BaseUserTest {
    @Test()
    @Description("Проверка получения списка пользователей при наличии зарегистрированных пользователей")
    public void getAllUsersTest() throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "/get")
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String responseBody = response.body().string();
        JsonArray responseJsonArray = JsonParser.parseString(responseBody).getAsJsonArray();
        JsonObject jsonUserObject = responseJsonArray.get(0).getAsJsonObject();

        step("Проверяем что созданный пользователь успешно возвращается в списке и поля соответствуют ожидаемым", () ->
        {
            assertThat("Код ответа должен быть 200", response.code(), is(200));
            assertThat("Проверка id пользователя", jsonUserObject.get("id").getAsInt(), is(1));
            assertThat("Проверка userName пользователя", jsonUserObject.get("username").getAsString(), is("test_user"));
            assertThat("Проверка email пользователя", jsonUserObject.get("email").getAsString(), is("test_user@test.test"));
            //
            assertThat("Проверка password пользователя", jsonUserObject.get("password").getAsString(), is("$2a$10$eOSMiWoR.qcG3khiRpwWRO2DJLoI5KqUQ7fSWAWdmJIYjTyl7tM1e"));
            assertThat("Проверка created_at пользователя", jsonUserObject.get("created_at").getAsString(), is("2024-10-16 07:17:20"));
        });
    }

    @Test()
    @Description("Проверка получения списка пользователей при отсутствии пользователей")
    public void getAllUsersWithEmptyDBTest() throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "/get")
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String responseBody = response.body().string();
        step("Проверяем что созданный пользователь успешно возвращается в списке", () ->
        {
            assertThat("Код ответа должен быть 200", response.code(), is(200));
            //Можно заменить на модельку User
            assertThat("Проверка корректности response", responseBody, equalTo("[]"));
        });
    }
}

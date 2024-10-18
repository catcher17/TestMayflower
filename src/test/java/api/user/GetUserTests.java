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
        //Получаю первого пользователя т.к. он создается в начале тестов на пустой базе
        JsonObject jsonUserObject = responseJsonArray.get(0).getAsJsonObject();

        step("Проверяем что созданный пользователь успешно возвращается в списке и поля соответствуют ожидаемым", () ->
        {
            assertThat("Код ответа должен быть 200", response.code(), is(200));
            assertThat("Проверка id пользователя", jsonUserObject.get("id").getAsInt(), is(1));
            assertThat("Проверка userName пользователя", jsonUserObject.get("username").getAsString(), is("existing_user"));
            assertThat("Проверка email пользователя", jsonUserObject.get("email").getAsString(), is("existing_user_email@test.test"));
            assertThat("Проверка password пользователя", jsonUserObject.get("password").getAsString(), is("$2a$10$TXN1d4Ke26zohYa4fW2E8eCBrIoYPPcUyLKrfWJSfbSg9o0Wu2xAC"));
            // Закоментил т.к. пользователь создается во время тестов не очевидна точная дата
            // Если поле важно можно проверить, что разница во времени не превышает N секунд/минут и тд
            // assertThat("Проверка created_at пользователя", jsonUserObject.get("created_at").getAsString(), is("2024-10-16 07:17:20"));
        });
        response.close();
    }

    @Test()
    @Description("Проверка получения списка пользователей при отсутствии пользователей")
    public void getAllUsersWithEmptyDBTest() throws Exception {
        //Будет падать т.к. не отчистить пользователей
        //Тут очищаем базу от пользователей
        //Если тесты будут выполняться параллельно можно вынести тест отдельно и настроить порядок запуска в all_test.xml
        Request request = new Request.Builder()
                .url(BASE_URL + "/get")
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String responseBody = response.body().string();
        step("Проверяем что возвращаемый ответ соответствует ожиданиям", () ->
        {
            assertThat("Код ответа должен быть 200", response.code(), is(200));
            assertThat("Проверка корректности response", responseBody, equalTo("[]"));
        });
        response.close();
    }
}

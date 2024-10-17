package api.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.qameta.allure.Description;

import okhttp3.*;
import org.mindrot.jbcrypt.BCrypt;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateUserTests extends BaseUserTest {

    @Test()
    @Description("Регистрация пользователя с корректными данными")
    public void successfullyCreatingUserTest() throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("username", uniqueUuidUserName)
                .add("email", uniqueUuidEmail)
                .add("password", "test_password")
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/create")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String responseBody = response.body().string();
        JsonObject jsonResponseBody = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonObject bodyDetails = jsonResponseBody.getAsJsonObject("details");
        boolean isPasswordHashValid = BCrypt.checkpw("test_password", bodyDetails.get("password").getAsString());

        step("Проверяем что пользователь успешно зарегистрирован и response корректен", () ->
        {
            assertThat("Код ответа должен быть 200", response.code(), is(200));
            assertThat("Проверка поля success:true", jsonResponseBody.get("success").getAsBoolean(), is(true));
            //Опечатка в response.message
            assertThat("Проверка поля message:User Successfully created", jsonResponseBody.get("message").getAsString(), is("User Successully created"));
            assertThat("Проверка имени пользователя на соответствие указанному при регистрации", bodyDetails.get("username").getAsString(), is(uniqueUuidUserName));
            assertThat("Проверка почты пользователя на соответствие указанной при регистрации", bodyDetails.get("email").getAsString(), is(uniqueUuidEmail));
            assertThat("Пароль захеширован и соответствует указанному при регистрации", isPasswordHashValid, is(true));
            //дополнительно проверить наличие пользователя в бд, чтобы не завязываться на другие get методы
        });
    }

    @DataProvider(name = "missingFieldsUserData")
    public Object[][] createMissingFieldsUserData() {
        return new Object[][]{
                {"", "valid_email1@test.com", "test_password", "A username is required"},
                {"validUsername1", "", "test_password", "An Email is required"},
                //Странное сообщение при отсутствии пароля
                {"validUsername1", "valid_email1@test.com", "", "A password for the user"}
        };
    }

    @Test(dataProvider = "missingFieldsUserData")
    @Description("Регистрация пользователя с незаполненным полем")
    public void tryCreateUserWithMissingFieldTest(String userName, String userEmail, String password, String errorMessage) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("username", userName)
                .add("email", userEmail)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/create")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String responseBody = response.body().string();
        JsonObject jsonResponseBody = JsonParser.parseString(responseBody).getAsJsonObject();

        step("Проверяем что при не заполненном поле регистрация не проходит", () ->
        {
            assertThat("Код ответа должен быть 400", response.code(), is(400));
            assertThat("Проверка поля success:false", jsonResponseBody.get("success").getAsBoolean(), is(false));
            assertThat("Проверка, что 'message' содержит " + errorMessage,
                    jsonResponseBody.get("message").getAsString(), containsString(errorMessage));
        });
    }

    @DataProvider(name = "existenceFieldsUserData")
    public Object[][] createExistenceFieldsUserData() {
        return new Object[][]{
                {"validUsername", "new_valid_email@test.com", "This username is taken. Try another."},
                {"new_validUsername", "valid_email@test.com", "Email already exists"},
        };
    }

    @Test(dataProvider = "existenceFieldsUserData")
    @Description("Регистрация пользователя с уже зарегистрированными userName/email")
    public void tryCreateUserWithExistingUserNameOrEmail(String userName, String userEmail, String errorMessage) throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("username", userName)
                .add("email", userEmail)
                .add("password", "test_password")
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/create")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String responseBody = response.body().string();
        JsonObject jsonResponseBody = JsonParser.parseString(responseBody).getAsJsonObject();

        step("Проверяем что при уже существующих в базе полях userName/email регистрация не проходит", () ->
        {
            assertThat("Код ответа должен быть 400", response.code(), is(400));
            assertThat("Проверка поля success:false", jsonResponseBody.get("success").getAsBoolean(), is(false));
            assertThat("Проверка, что 'message' содержит " + errorMessage,
                    jsonResponseBody.get("message").getAsString(), containsString(errorMessage));
        });
    }

    @Test()
    @Description("Регистрация пользователя с невалидным email")
    public void tryCreateUserWithInvalidEmail() throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("username", uniqueUuidUserName)
                .add("email", "invalidEmail")
                .add("password", "test_password")
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/create")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();

        String responseBody = response.body().string();
        JsonObject jsonResponseBody = JsonParser.parseString(responseBody).getAsJsonObject();
        //Будет падать т.к. нет валидации, думаю должна быть минимальная проверка наличия @
        step("Проверяем что при не заполненном поле", () ->
        {
            assertThat("Код ответа должен быть 400", response.code(), is(400));
            assertThat("Проверка поля success:false", jsonResponseBody.get("success").getAsBoolean(), is(false));
            assertThat("Проверка, что 'message' содержит ошибку вида - Required valid email",
                    jsonResponseBody.get("message").getAsString(), containsString("Required valid email"));
        });
    }
}

package api.user;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BaseUserTest {
    protected OkHttpClient okHttpClient;
    protected static final String BASE_URL = "http://3.73.86.8:3333/user";
    protected String uniqueUuidUserName;
    protected String uniqueUuidEmail;

    @BeforeClass
    public void setupTestEnv() throws Exception {
        //MockServer.startMockServer();
        okHttpClient = new OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).build();
        //Добавил создание сюда т.к. нужно только паре тестов
        RequestBody existingUserFormBody = new FormBody.Builder()
                .add("username", "existing_user")
                .add("email", "existing_user_email@test.test")
                .add("password", "existing_user_password")
                .build();

        Request existingUserCreateRequest = new Request.Builder()
                .url(BASE_URL + "/create")
                .post(existingUserFormBody)
                .build();

        okHttpClient.newCall(existingUserCreateRequest).execute().close();
    }

    @BeforeMethod
    public void generateTestData() {
        uniqueUuidUserName = "test_user_" + UUID.randomUUID();
        uniqueUuidEmail = UUID.randomUUID() + "@gmail.com";
    }

    @AfterClass
    public void clearTestEnv() {
        //MockServer.stopMockServer();
    }
}

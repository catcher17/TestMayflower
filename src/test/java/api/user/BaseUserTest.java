package api.user;

import org.example.mock.MockServer;
import okhttp3.OkHttpClient;
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
    public void setupTestEnv() {
        //MockServer.startMockServer();
        okHttpClient = new OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).build();
        //генерация пользователей для тестов, добавил бы через бд, чтоб не завязываться на post
    }

    @BeforeMethod
    public void generateTestData() {
        uniqueUuidUserName = "test_user_" + UUID.randomUUID();
        uniqueUuidEmail = UUID.randomUUID() + "@gmail.com";
    }

    @AfterClass
    public void clearTestEnv() {
        //MockServer.stopMockServer();
        //удаление тестовых пользователей
    }
}

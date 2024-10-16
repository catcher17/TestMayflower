package org.example.mock;

import org.mockserver.integration.ClientAndServer;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


//Для локальной отладки
public class MockServer {

    private static ClientAndServer mockServer;

    public static void startMockServer() {
        mockServer = startClientAndServer(1080);

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/user/create")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody("{\n" +
                                "    \"success\": true,\n" +
                                "    \"details\": {\n" +
                                "        \"username\": \"test_user2\",\n" +
                                "        \"email\": \"test_usertest.test2\",\n" +
                                "        \"password\": \"$2a$10$Su4xwgY583GzmsmH5a2TNefs9afFJz4trD6XHiIuep/U6GkxxpcbW\",\n" +
                                "        \"created_at\": \"2024-10-16 09:25:22\",\n" +
                                "        \"updated_at\": \"2024-10-16 09:25:22\",\n" +
                                "        \"id\": 2\n" +
                                "    },\n" +
                                "    \"message\": \"User Successully created\"\n" +
                                "}")
        );

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/user/get")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody("[{\"id\": 1," +
                                " \"username\": \"test_user\"," +
                                " \"email\": \"test_user@test.test\"," +
                                " \"password\": \"$2a$10$eOSMiWoR.qcG3khiRpwWRO2DJLoI5KqUQ7fSWAWdmJIYjTyl7tM1e\"," +
                                " \"created_at\": \"2024-10-16 07:17:20\"," +
                                " \"updated_at\": \"2024-10-16 07:17:20\"}]")
        );
    }

    public static void stopMockServer() {
        mockServer.stop();
    }
}

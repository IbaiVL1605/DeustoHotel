package com.example.deusto_hotel.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.deusto_hotel.proxy.Proxy;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProxyTest {

    @Mock
    HttpClient httpClient;

    @InjectMocks
    Proxy proxy;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(201);

        when(httpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn((HttpResponse) response);

        proxy.signup("Juan López", "juan@email.com", "juan123");

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void shouldNotRegisterUserIfEmailAlreadyExists() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(400);
        when(response.body()).thenReturn("Error: Email already in use");

        when(httpClient.send(any(HttpRequest.class), any()))
                .thenReturn((HttpResponse) response);

        assertThrows(RuntimeException.class, () -> {
            proxy.signup("Juan López", "juan@email.com", "juan123");
        });

        // Verificamos que, aun fallando, se intentó hacer la petición una vez
        verify(httpClient, times(1)).send(any(HttpRequest.class), any());
    }
}

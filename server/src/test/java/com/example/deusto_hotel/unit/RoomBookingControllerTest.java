package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.RoomBookingController;
import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.RoomBookingStatus;
import com.example.deusto_hotel.service.RoomBookingService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomBookingController.class)
class RoomBookingControllerTest {


}
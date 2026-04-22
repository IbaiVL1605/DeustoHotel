package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.mapper.RoomBookingMapper;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.repository.RoomRepository;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.service.RoomBookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomBookingServiceTest {

}
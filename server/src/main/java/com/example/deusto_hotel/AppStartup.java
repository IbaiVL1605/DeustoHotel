package com.example.deusto_hotel;

import com.example.deusto_hotel.model.*;
import com.example.deusto_hotel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class AppStartup implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @Autowired
    private CourtBookingRepository courtBookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existen datos para no duplicarlos
        if (userRepository.count() > 0) {
            return;
        }

        // 1. Crear Usuarios con diferentes roles
        User admin = new User();
        admin.setNombre("Admin User");
        admin.setEmail("admin@deusto.com");
        admin.setPassword("admin123");
        admin.setRol(Role.ADMIN);
        admin.setBloqueado(false);
        admin.setCancelaciones(0);
        userRepository.save(admin);

        User receptionist = new User();
        receptionist.setNombre("Maria García");
        receptionist.setEmail("maria@deusto.com");
        receptionist.setPassword("maria123");
        receptionist.setRol(Role.RECEPTIONIST);
        receptionist.setBloqueado(false);
        receptionist.setCancelaciones(0);
        userRepository.save(receptionist);

        User client1 = new User();
        client1.setNombre("Juan López");
            client1.setEmail("juan@email.com");
        client1.setPassword("juan123");
        client1.setRol(Role.CLIENT);
        client1.setBloqueado(false);
        client1.setCancelaciones(0);
        userRepository.save(client1);

        User client2 = new User();
        client2.setNombre("Ana Martínez");
        client2.setEmail("ana@email.com");
        client2.setPassword("ana123");
        client2.setRol(Role.CLIENT);
        client2.setBloqueado(false);
        client2.setCancelaciones(0);
        userRepository.save(client2);

        User client3 = new User();
        client3.setNombre("Carlos Rodríguez");
        client3.setEmail("carlos@email.com");
        client3.setPassword("carlos123");
        client3.setRol(Role.CLIENT);
        client3.setBloqueado(false);
        client3.setCancelaciones(1);
        userRepository.save(client3);

        // 2. Crear Habitaciones con diferentes tipos
        Room room1 = new Room();
        room1.setNumero("101");
        room1.setTipo(RoomType.INDIVIDUAL);
        room1.setEstado(RoomStatus.DISPONIBLE);
        roomRepository.save(room1);

        Room room2 = new Room();
        room2.setNumero("102");
        room2.setTipo(RoomType.DOBLE);
        room2.setEstado(RoomStatus.DISPONIBLE);
        roomRepository.save(room2);

        Room room3 = new Room();
        room3.setNumero("201");
        room3.setTipo(RoomType.SUITE);
        room3.setCapacidad(4);
        room3.setPrecioPorNoche(200);
        room3.setEstado(RoomStatus.DISPONIBLE);
        roomRepository.save(room3);

        Room room4 = new Room();
        room4.setNumero("202");
        room4.setTipo(RoomType.DOBLE);
        room4.setEstado(RoomStatus.BLOQUEADA);
        roomRepository.save(room4);

        // 3. Crear Pistas deportivas con diferentes tipos
        Court court1 = new Court();
        court1.setNombre("Pista Tenis 1");
        court1.setTipo(CourtType.TENIS);
        court1.setPrecioPorHora(50.0);
        court1.setEstado(CourtStatus.DISPONIBLE);
        courtRepository.save(court1);

        Court court2 = new Court();
        court2.setNombre("Pista Pádel 1");
        court2.setTipo(CourtType.PADEL);
        court2.setPrecioPorHora(40.0);
        court2.setEstado(CourtStatus.DISPONIBLE);
        courtRepository.save(court2);

        Court court3 = new Court();
        court3.setNombre("Pista Fútbol");
        court3.setTipo(CourtType.FUTBOL);
        court3.setPrecioPorHora(60.0);
        court3.setEstado(CourtStatus.DISPONIBLE);
        courtRepository.save(court3);

        Court court4 = new Court();
        court4.setNombre("Piscina Principal");
        court4.setTipo(CourtType.PISCINA);
        court4.setPrecioPorHora(25.0);
        court4.setEstado(CourtStatus.BLOQUEADA);
        courtRepository.save(court4);

        // 4. Crear Reservas de Habitaciones con diferentes estados
        RoomBooking roomBooking1 = new RoomBooking();
        roomBooking1.setCliente(client1);
        roomBooking1.setHabitacion(room1);
        roomBooking1.setCheckIn(LocalDate.now().plusDays(1));
        roomBooking1.setCheckOut(LocalDate.now().plusDays(3));
        roomBooking1.setEstado(RoomBookingStatus.PENDIENTE);
        roomBooking1.setPrecioTotal(160.0);
        roomBookingRepository.save(roomBooking1);

        RoomBooking roomBooking2 = new RoomBooking();
        roomBooking2.setCliente(client2);
        roomBooking2.setHabitacion(room2);
        roomBooking2.setCheckIn(LocalDate.now());
        roomBooking2.setCheckOut(LocalDate.now().plusDays(2));
        roomBooking2.setEstado(RoomBookingStatus.CONFIRMADA);
        roomBooking2.setPrecioTotal(240.0);
        roomBookingRepository.save(roomBooking2);

        RoomBooking roomBooking3 = new RoomBooking();
        roomBooking3.setCliente(client3);
        roomBooking3.setHabitacion(room3);
        roomBooking3.setCheckIn(LocalDate.now().plusDays(5));
        roomBooking3.setCheckOut(LocalDate.now().plusDays(7));
        roomBooking3.setEstado(RoomBookingStatus.CANCELADA);
        roomBooking3.setPrecioTotal(400.0);
        roomBookingRepository.save(roomBooking3);

        // 5. Crear Reservas de Pistas con diferentes estados
        CourtBooking courtBooking1 = new CourtBooking();
        courtBooking1.setCliente(client1);
        courtBooking1.setPista(court1);
        courtBooking1.setFecha(LocalDate.now().plusDays(2));
        courtBooking1.setHoraInicio(LocalTime.of(10, 0));
        courtBooking1.setHoraFin(LocalTime.of(11, 0));
        courtBooking1.setEstado(CourtBookingStatus.PENDIENTE);
        courtBooking1.setPrecioTotal(50.0);
        courtBookingRepository.save(courtBooking1);

        CourtBooking courtBooking2 = new CourtBooking();
        courtBooking2.setCliente(client2);
        courtBooking2.setPista(court2);
        courtBooking2.setFecha(LocalDate.now().plusDays(1));
        courtBooking2.setHoraInicio(LocalTime.of(14, 0));
        courtBooking2.setHoraFin(LocalTime.of(15, 30));
        courtBooking2.setEstado(CourtBookingStatus.CONFIRMADA);
        courtBooking2.setPrecioTotal(60.0);
        courtBookingRepository.save(courtBooking2);

        CourtBooking courtBooking3 = new CourtBooking();
        courtBooking3.setCliente(client3);
        courtBooking3.setPista(court3);
        courtBooking3.setFecha(LocalDate.now().plusDays(3));
        courtBooking3.setHoraInicio(LocalTime.of(18, 0));
        courtBooking3.setHoraFin(LocalTime.of(19, 0));
        courtBooking3.setEstado(CourtBookingStatus.CONFIRMADA);
        courtBooking3.setPrecioTotal(60.0);
        courtBookingRepository.save(courtBooking3);

        // 6. Crear Reseñas de clientes sobre habitaciones
        Review review1 = new Review();
        review1.setCliente(client1);
        review1.setHabitacion(room1);
        review1.setPuntuacion(5);
        review1.setComentario("Excelente habitación, muy limpia y cómoda. El personal muy atento.");
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setCliente(client2);
        review2.setHabitacion(room2);
        review2.setPuntuacion(4);
        review2.setComentario("Buena relación calidad-precio. La ubicación es perfecta.");
        reviewRepository.save(review2);

        Review review3 = new Review();
        review3.setCliente(client3);
        review3.setHabitacion(room3);
        review3.setPuntuacion(5);
        review3.setComentario("Suite increíble, super lujosa. Volveré sin duda.");
        reviewRepository.save(review3);

        System.out.println("✓ Base de datos inicializada con datos de ejemplo");
    }
}

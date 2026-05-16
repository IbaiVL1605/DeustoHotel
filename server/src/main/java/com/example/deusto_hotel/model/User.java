package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un usuario del hotel.
 * <p>
 * Esta clase modela los usuarios del sistema de gestión hotelera, permitiendo
 * almacenar información personal del usuario, credenciales de acceso, rol asignado
 * y estado de cuenta. Cada usuario puede realizar reservas de habitaciones, reservas
 * de canchas, y escribir reseñas sobre sus experiencias en el hotel.
 * </p>
 * <p>
 * Los usuarios pueden ser bloqueados en el sistema, lo cual previene que realicen
 * nuevas reservas. También se registran las cancelaciones que ha realizado el usuario,
 * que pueden ser usadas para determinar si debe ser bloqueado.
 * </p>
 *
 * @author Deusto Hotel Team
 * @version 1.0
 * @see Role
 * @see RoomBooking
 * @see CourtBooking
 * @see Review
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    /** Identificador único del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del usuario. */
    @Column(nullable = false)
    private String nombre;

    /** Correo electrónico del usuario. Debe ser único en el sistema. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Contraseña del usuario en texto plano (se recomienda usar encriptación en producción). */
    @Column(nullable = false)
    private String password;

    /** Rol asignado al usuario (CLIENT, ADMIN, etc.). Define los permisos del usuario. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role rol;

    /** Indica si el usuario está bloqueado en el sistema. Un usuario bloqueado no puede realizar reservas. */
    @Column(nullable = false)
    private boolean bloqueado = false;

    /** Número de cancelaciones realizadas por el usuario. Se utiliza para determinar el bloqueo del usuario. */
    @Column(nullable = false)
    private int cancelaciones = 0;

    /** Fecha y hora de creación de la cuenta del usuario. Se establece automáticamente al insertar el registro. */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    /** Lista de reservas de habitaciones realizadas por el usuario. Relación uno-a-muchos con RoomBooking. */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<RoomBooking> roomBookings = new ArrayList<>();

    /** Lista de reservas de canchas realizadas por el usuario. Relación uno-a-muchos con CourtBooking. */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<CourtBooking> courtBookings = new ArrayList<>();

    /** Lista de reseñas escritas por el usuario. Relación uno-a-muchos con Review. */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
}

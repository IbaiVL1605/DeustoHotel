package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una habitación en el hotel.
 * <p>
 * Esta clase modela las habitaciones del sistema de gestión hotelera, permitiendo
 * almacenar información sobre el tipo de habitación, su estado, capacidad y precio.
 * Soporta diferentes tipos de habitaciones (SUITE y otros tipos predefinidos) con
 * capacidades y precios específicos.
 * </p>
 * <p>
 * La capacidad y el precio se pueden configurar independientemente solo para habitaciones
 * de tipo SUITE. Para otros tipos de habitaciones, estos valores se determinan automáticamente
 * según el tipo.
 * </p>
 *
 * @author Deusto Hotel Team
 * @version 1.0
 * @see RoomType
 * @see RoomStatus
 * @see RoomBooking
 */
@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    /** Identificador único de la habitación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número de la habitación. Debe ser único. */
    @Column(nullable = false, unique = true)
    private String numero;

    /** Tipo de habitación (SIMPLE, DOBLE, TRIPLE, SUITE, etc.). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType tipo;

    /** Capacidad de ocupantes de la habitación. Solo configurable para SUITE. */
    @Column(nullable = true)
    private int capacidad;

    /** Precio por noche de la habitación. Solo configurable para SUITE. */
    @Column(nullable = true)
    private int precioPorNoche;

    /** Estado actual de la habitación (DISPONIBLE, OCUPADA, MANTENIMIENTO). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus estado = RoomStatus.DISPONIBLE;

    /** Lista de reservas asociadas a esta habitación. */
    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL)
    private List<RoomBooking> roomBookings = new ArrayList<>();

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    /**
     * Constructor que inicializa una habitación con un identificador y tipo específico.
     * <p>
     * Establece valores por defecto: capacidad en 1, precio en 1, número basado en el ID,
     * y estado en DISPONIBLE.
     * </p>
     *
     * @param id identificador único de la habitación
     * @param roomType tipo de habitación
     */
    public Room(Long id, RoomType roomType) {
        this.capacidad = 1;
        this.precioPorNoche = 1;
        this.id = id;
        this.tipo = roomType;
        this.numero = String.valueOf(id);
        this.estado = RoomStatus.DISPONIBLE;

    }

    /**
     * Establece la capacidad de la habitación.
     * <p>
     * Solo se permite establecer la capacidad para habitaciones de tipo SUITE.
     * Para otros tipos de habitaciones, la capacidad es determinada automáticamente
     * por el tipo y no puede ser modificada.
     * </p>
     *
     * @param capacidad número de ocupantes permitidos
     * @throws IllegalArgumentException si la habitación no es de tipo SUITE
     */
    public void setCapacidad(int capacidad) {
        if(!this.tipo.equals(RoomType.SUITE)){
            throw new IllegalArgumentException("No se puede establecer capacidad para habitaciones que no son SUITE");
        }
        this.capacidad = capacidad;
    }

    /**
     * Obtiene la capacidad de la habitación.
     * <p>
     * Para habitaciones SUITE, retorna el valor configurado específicamente.
     * Para otros tipos de habitaciones, retorna la capacidad definida en el tipo.
     * </p>
     *
     * @return capacidad de la habitación (número de ocupantes permitidos)
     */
    public int getCapacidad() {
        if(this.tipo.equals(RoomType.SUITE)){
            return capacidad;
        } else {
            return tipo.getCapacidad();
        }
    }

    /**
     * Obtiene el precio por noche de la habitación.
     * <p>
     * Para habitaciones que no son SUITE, retorna el precio definido en el tipo.
     * Para habitaciones SUITE, retorna el valor configurado específicamente.
     * </p>
     *
     * @return precio por noche en unidades monetarias
     */
    public int getPrecioPorNoche() {
        if(!this.tipo.equals(RoomType.SUITE)){
            return tipo.getPrecioPorNoche();
        }
        return this.precioPorNoche;
    }

    /**
     * Establece el precio por noche de la habitación.
     * <p>
     * Solo se permite establecer el precio para habitaciones de tipo SUITE.
     * Para otros tipos de habitaciones, el precio es determinado automáticamente
     * por el tipo y no puede ser modificado.
     * </p>
     *
     * @param precioPorNoche precio por noche en unidades monetarias
     * @throws IllegalArgumentException si la habitación no es de tipo SUITE
     */
    public void setPrecioPorNoche(int precioPorNoche) {
        if(!this.tipo.equals(RoomType.SUITE)){
            throw new IllegalArgumentException("No se puede establecer precio para habitaciones que no son SUITE");
        }

        this.precioPorNoche = precioPorNoche;
    }



}

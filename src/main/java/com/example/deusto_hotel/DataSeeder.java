package com.example.deusto_hotel;

import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtStatus;
import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CourtRepository courtRepository;

    @Override
    public void run(String... args) throws Exception {
        if (courtRepository.count() == 0) {
            Court court1 = new Court();
            court1.setNombre("Pista de Tenis 1");
            court1.setTipo(CourtType.TENIS);
            court1.setPrecioPorHora(15.0);
            court1.setEstado(CourtStatus.DISPONIBLE);

            Court court2 = new Court();
            court2.setNombre("Pista de Pádel 1");
            court2.setTipo(CourtType.PADEL);
            court2.setPrecioPorHora(12.0);
            court2.setEstado(CourtStatus.DISPONIBLE);

            Court court3 = new Court();
            court3.setNombre("Campo de Fútbol 1");
            court3.setTipo(CourtType.FUTBOL);
            court3.setPrecioPorHora(25.0);
            court3.setEstado(CourtStatus.DISPONIBLE);

            Court court4 = new Court();
            court4.setNombre("Piscina 1");
            court4.setTipo(CourtType.PISCINA);
            court4.setPrecioPorHora(10.0);
            court4.setEstado(CourtStatus.DISPONIBLE);

            courtRepository.save(court1);
            courtRepository.save(court2);
            courtRepository.save(court3);
            courtRepository.save(court4);

            System.out.println("Pistas de ejemplo creadas.");
        }
    }
}

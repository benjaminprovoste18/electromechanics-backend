package cl.duoc.emterreno.jornadas.repository;

import cl.duoc.emterreno.jornadas.model.RegistroJornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroJornadaRepository extends JpaRepository<RegistroJornada, String> {

    List<RegistroJornada> findAllByOrderByFechaHoraDesc();

    List<RegistroJornada> findByEmpleadoIdOrderByFechaHoraDesc(String empleadoId);

    List<RegistroJornada> findByFechaHoraBetweenOrderByFechaHoraAsc(Long desde, Long hasta);
}

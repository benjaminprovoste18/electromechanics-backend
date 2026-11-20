package cl.duoc.emterreno.ordenes.repository;

import cl.duoc.emterreno.ordenes.model.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, String> {

    List<OrdenTrabajo> findAllByOrderByFechaProgramadaDesc();

    List<OrdenTrabajo> findByTecnicoIdOrderByFechaProgramadaAsc(String tecnicoId);
}

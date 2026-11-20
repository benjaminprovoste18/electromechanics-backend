package cl.duoc.emterreno.ordenes.repository;

import cl.duoc.emterreno.ordenes.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, String> {

    List<Actividad> findAllByOrderByHoraRegistroDesc();

    List<Actividad> findByOrdenIdOrderByHoraRegistroDesc(String ordenId);

    void deleteByOrdenId(String ordenId);
}

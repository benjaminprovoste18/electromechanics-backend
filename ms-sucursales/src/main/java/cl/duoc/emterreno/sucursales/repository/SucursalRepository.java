package cl.duoc.emterreno.sucursales.repository;

import cl.duoc.emterreno.sucursales.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, String> {

    List<Sucursal> findAllByOrderByNombreClienteAsc();

    boolean existsByNombreClienteIgnoreCaseAndIdNot(String nombreCliente, String id);
}

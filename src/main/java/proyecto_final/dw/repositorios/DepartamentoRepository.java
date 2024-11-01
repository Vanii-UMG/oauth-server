package proyecto_final.dw.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto_final.dw.modelos.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
}

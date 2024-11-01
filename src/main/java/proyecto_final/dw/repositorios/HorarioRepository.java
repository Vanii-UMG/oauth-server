package proyecto_final.dw.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto_final.dw.modelos.Horario;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,Long> {

}

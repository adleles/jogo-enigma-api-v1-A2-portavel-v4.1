package br.edu.exemplo.jogoenigma.repository;
import br.edu.exemplo.jogoenigma.entity.Participante; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface ParticipanteRepository extends JpaRepository<Participante,Long>{ Optional<Participante> findByEmail(String email); }

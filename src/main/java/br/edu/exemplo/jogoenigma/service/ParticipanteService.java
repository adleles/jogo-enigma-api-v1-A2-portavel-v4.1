package br.edu.exemplo.jogoenigma.service;
import br.edu.exemplo.jogoenigma.dto.*; import br.edu.exemplo.jogoenigma.entity.Participante; import br.edu.exemplo.jogoenigma.repository.ParticipanteRepository; import br.edu.exemplo.jogoenigma.vo.Email; import org.springframework.stereotype.Service; import java.util.List;
@Service public class ParticipanteService { private final ParticipanteRepository repo; public ParticipanteService(ParticipanteRepository repo){this.repo=repo;}
 public ParticipanteResponse criar(ParticipanteRequest r){ if(repo.findByEmail(r.email()).isPresent()) throw new IllegalArgumentException("E-mail já cadastrado"); return toResponse(repo.save(new Participante(r.nome(),new Email(r.email()),r.pontos()))); }
 public List<ParticipanteResponse> listar(){return repo.findAll().stream().map(this::toResponse).toList();} private ParticipanteResponse toResponse(Participante p){return new ParticipanteResponse(p.getId(),p.getNome(),p.getEmail(),p.getPontos(),p.getNivel());}}

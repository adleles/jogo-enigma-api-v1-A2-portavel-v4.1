package br.edu.exemplo.jogoenigma.controller;
import br.edu.exemplo.jogoenigma.dto.*; import br.edu.exemplo.jogoenigma.service.ParticipanteService; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/participantes") @CrossOrigin(origins="*") public class ParticipanteController { private final ParticipanteService service; public ParticipanteController(ParticipanteService service){this.service=service;}
 @PostMapping public ResponseEntity<ParticipanteResponse> criar(@Valid @RequestBody ParticipanteRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(r));} @GetMapping public List<ParticipanteResponse> listar(){return service.listar();}}

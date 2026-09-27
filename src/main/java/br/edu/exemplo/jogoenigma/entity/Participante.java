package br.edu.exemplo.jogoenigma.entity;
import br.edu.exemplo.jogoenigma.vo.Email; import jakarta.persistence.*;
@Entity @Table(name="participantes") public class Participante {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false) private String nome; @Column(nullable=false,unique=true) private String email; @Column(nullable=false) private Integer pontos;
 protected Participante(){} public Participante(String nome, Email email, Integer pontos){this.nome=nome;this.email=email.valor();this.pontos=pontos==null?0:pontos;}
 public Long getId(){return id;} public String getNome(){return nome;} public String getEmail(){return email;} public Integer getPontos(){return pontos;} public String getNivel(){return pontos>=70?"MESTRE":pontos>=40?"DETETIVE":"INICIANTE";}
}

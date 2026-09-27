package br.edu.exemplo.jogoenigma.repository;
import br.edu.exemplo.jogoenigma.entity.Participante;
import br.edu.exemplo.jogoenigma.vo.Email;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Testcontainers @AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE) class ParticipanteRepositoryTest {
    @Container
    @ServiceConnection static PostgreSQLContainer<?> pg=new PostgreSQLContainer<>("postgres:17-alpine"); @Autowired ParticipanteRepository repo; @Test void devePersistirNoPostgres(){repo.save(new Participante("Ana",new Email("ana@teste.com"),50)); assertTrue(repo.findByEmail("ana@teste.com").isPresent());}}

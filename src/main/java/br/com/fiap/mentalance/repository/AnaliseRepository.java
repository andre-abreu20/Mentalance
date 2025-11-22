package br.com.fiap.mentalance.repository;

import br.com.fiap.mentalance.model.Analise;
import br.com.fiap.mentalance.model.Checkin;
import br.com.fiap.mentalance.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnaliseRepository extends JpaRepository<Analise, Long> {

    Optional<Analise> findByCheckin(Checkin checkin);

    List<Analise> findTop20ByCheckinUsuarioOrderByCriadoEmDesc(Usuario usuario);
}


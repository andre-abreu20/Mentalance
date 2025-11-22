package br.com.fiap.mentalance.repository;

import br.com.fiap.mentalance.model.Analise;
import br.com.fiap.mentalance.model.Checkin;
import br.com.fiap.mentalance.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnaliseRepository extends JpaRepository<Analise, Long> {

    Optional<Analise> findByCheckin(Checkin checkin);

    List<Analise> findTop20ByCheckinUsuarioOrderByCriadoEmDesc(Usuario usuario);

    @Query("select a from Analise a where a.checkin.usuario = :usuario order by a.criadoEm desc")
    Page<Analise> findByUsuarioOrderByCriadoEmDesc(Usuario usuario, Pageable pageable);
}


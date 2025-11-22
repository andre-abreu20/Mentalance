package br.com.fiap.mentalance.repository;

import br.com.fiap.mentalance.model.Checkin;
import br.com.fiap.mentalance.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {

    List<Checkin> findTop7ByUsuarioOrderByDataDesc(Usuario usuario);

    Page<Checkin> findAllByUsuarioOrderByDataDesc(Usuario usuario, Pageable pageable);

    List<Checkin> findAllByUsuarioOrderByDataDesc(Usuario usuario);

    Page<Checkin> findAllByOrderByDataDesc(Pageable pageable);

    List<Checkin> findTop10ByOrderByDataDesc();

    @Query("select c from Checkin c where c.usuario = :usuario and c.data between :inicio and :fim order by c.data")
    List<Checkin> buscarPorPeriodo(Usuario usuario, LocalDate inicio, LocalDate fim);

    @Query("select c.humor, count(c) from Checkin c where c.usuario = :usuario and c.data between :inicio and :fim group by c.humor")
    List<Object[]> contarPorHumor(Usuario usuario, LocalDate inicio, LocalDate fim);
}


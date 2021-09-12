package com.vquirino.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vquirino.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}

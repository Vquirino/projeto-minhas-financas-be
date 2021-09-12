package com.vquirino.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vquirino.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

}

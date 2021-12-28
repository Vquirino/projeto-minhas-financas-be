package com.vquirino.minhasfinancas.model.repository;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.vquirino.minhasfinancas.model.entity.Usuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//AÇÃO - EXECUÇÃO
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//VERIFICAÇÃO
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {		
		//AÇÃO - EXECUÇÃO
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//VERIFICAÇÃO
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		
		//AÇÃO - EXECUÇÃO
		Usuario usuarioSalvo = repository.save(usuario);
		
		//VERIFICAÇÃO
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//CENÁRIO
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//AÇÃO - EXECUÇÃO
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		//VERIFICAÇÃO
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarOUsuarioPorEmailQuandoNaoExisteNaBase() {		
		//AÇÃO - EXECUÇÃO
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		//VERIFICAÇÃO
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	public static Usuario criarUsuario() {
		return Usuario
				.builder()
				.nome("usuario")
				.email("usuario@email.com")
				.senha("senha")
				.build();
	}
}

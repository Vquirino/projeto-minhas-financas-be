package com.vquirino.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.vquirino.minhasfinancas.exception.ErroAutenticacao;
import com.vquirino.minhasfinancas.exception.RegraNegocioException;
import com.vquirino.minhasfinancas.model.entity.Usuario;
import com.vquirino.minhasfinancas.model.repository.UsuarioRepository;
import com.vquirino.minhasfinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuario() {
		//CENÁRIO
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				.id(1l)
				.nome("nome")
				.email("email@email.com")
				.senha("senha")
				.build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//AÇÃO - EXECUÇÃO
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//VERIFICAÇÃO
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//CENÁRIO
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//AÇÃO
		service.salvarUsuario(usuario);
		
		//VERIFICAÇÃO
		Mockito.verify(repository,  Mockito.never()).save(usuario);
	}
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//CENÁRIO
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//AÇÃO - EXECUÇÃO
		Usuario result = service.autenticar(email, senha);
		
		//VERIFICAÇÃO
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		//CENÁRIO
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//AÇÃO - EXECUÇÂO
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "senha") );
		
		//VERIFICAÇÃO
		Assertions.assertThat(exception)
			.isInstanceOf(ErroAutenticacao.class)
			.hasMessage("Usuário não encontrado para o email informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		//CENÁRIO
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//AÇÃO - EXECUÇÂO
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "123") );
		
		//VERIFICAÇÃO
		Assertions.assertThat(exception)
			.isInstanceOf(ErroAutenticacao.class)
			.hasMessage("Senha inválida.");
		
	}
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		//CENÁRIO		
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//AÇÃO - EXECUÇÃO
		service.validarEmail("email@email.com");
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//CENÁRIO
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//AÇÃO - EXECUÇÃO
		service.validarEmail("email@email.com");
	}

}

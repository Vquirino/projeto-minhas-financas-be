package com.vquirino.minhasfinancas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.vquirino.minhasfinancas.exception.RegraNegocioException;
import com.vquirino.minhasfinancas.model.entity.Lancamento;
import com.vquirino.minhasfinancas.model.entity.Usuario;
import com.vquirino.minhasfinancas.model.enums.StatusLancamento;
import com.vquirino.minhasfinancas.model.enums.TipoLancamento;
import com.vquirino.minhasfinancas.model.repository.LancamentoRepository;
import com.vquirino.minhasfinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//CENÁRIO
		Lancamento lancamentoASalvar = criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//AÇÃO - EXECUÇÃO
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//VERIFICAÇÃO
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//CENÁRIO
		Lancamento lancamentoASalvar = criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		//EXECUÇÃO E VERIFICAÇÃO
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//CENÁRIO
		Lancamento lancamentoSalvo = criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//AÇÃO - EXECUÇÃO
		service.atualizar(lancamentoSalvo);
		
		//VERIFICAÇÃO
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancaErroAoTenterAtualizarUmLancamentoQuaAindaNãoFoiSalvo() {
		//CENÁRIO
		Lancamento lancamento = criarLancamento();
		
		//EXECUÇÃO E VERIFICAÇÃO
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		
		//AÇÃO - EXECUÇÃO
		service.deletar(lancamento);
		
		//VERIFICAÇÃO
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancaErroAoTenterDeletarUmLancamentoQuaAindaNãoFoiSalvo() {
		//CENÁRIO
		Lancamento lancamento = criarLancamento();
		
		//AÇÃO - EXECUÇÃO
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
		
		//VERIFICAÇÃO
		Mockito.verify(repository, Mockito.never()).save(lancamento);	
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		//CENÁRIO
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//EXECUÇÃO
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//VERIFICAÇÕES
		Assertions
			.assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//CENÁRIO
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//EXECUÇÃO
		service.atualizarStatus(lancamento, novoStatus);
		
		//VERIFICAÇÂO
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		//CENÁRIO
		Long id = 1l;
		
		Lancamento lancamento = criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//EXECUÇÃO
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//VERIFICAÇÃO
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
 	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		//CENÁRIO
		Long id = 1l;
		
		Lancamento lancamento = criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//EXECUÇÃO
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//VERIFICAÇÃO
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmUsuario() {
		Lancamento lancamento = new Lancamento();
		//TRATAMENTO PARA DESCRIÇÃO
		Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("Salario");
		
		//TRATAMENTO PARA MÊS
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(0);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(13);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(1);
		
		//TRATAMENTO PARA ANO
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(202);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(2020);
		
		//TRATAMENTO PARA USUÁRIO
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Usuário.");
		
		lancamento.getUsuario().setId(1l);
		
		//TRATAMENTO PARA VALOR
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(1));
		
		//TRATAMENTO PARA TIPO
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
			.hasMessage("Informe o tipo de Lançamento.");
		
	}
	
	private Lancamento criarLancamento() {
		return Lancamento.builder()
				.ano(2019)
				.mes(1)
				.descricao("lançamento qualquer")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
	}

}

package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.Endereco;
import br.com.alura.domain.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.domain.http.AgenciaHttp;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.repository.AgenciaRepository;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class AgenciaServiceTest {

    private static final String CNPJ = "12345678000199";
    private static final String NOME = "nome";
    private static final String RAZAO_SOCIAL = "razao";
    private static final String RUA = "rua";
    private static final String LOGRADOURO = "abc";
    private static final long ID = 1L;

    @InjectMock
    private AgenciaRepository agenciaRepository;

    @InjectMock
    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    @Inject
    private AgenciaService agenciaService;


    @Test
    void testCadastrarErroCNPJInativo() {
        Agencia agencia = criarAgencia();

        when(situacaoCadastralHttpService.buscarPorCnpj(CNPJ)).thenReturn(null);

        assertThrows(AgenciaNaoAtivaOuNaoEncontradaException.class, () -> agenciaService.cadastrar(agencia));

        verify(agenciaRepository, never()).persist(agencia);
    }

    @Test
    void testCadastrarComSucesso() {
        Agencia agencia = criarAgencia();
        AgenciaHttp agenciaHttp = new AgenciaHttp(NOME, RAZAO_SOCIAL, CNPJ, SituacaoCadastral.ATIVO);

        when(situacaoCadastralHttpService.buscarPorCnpj(CNPJ)).thenReturn(agenciaHttp);
        doNothing().when(agenciaRepository).persist(any(Agencia.class));

        agenciaService.cadastrar(agencia);

        verify(agenciaRepository, times(1)).persist(agencia);
    }

    private Agencia criarAgencia() {
        Endereco endereco = new Endereco(ID, RUA, LOGRADOURO, "", 123);
        return new Agencia(ID, NOME, RAZAO_SOCIAL, CNPJ, endereco);
    }

    @Test
    void testBuscarPorId() {
        when(agenciaRepository.findById(anyLong())).thenReturn(criarAgencia());

        Agencia agencia = agenciaService.buscarPorId(ID);

        verify(agenciaRepository, times(1)).findById(ID);
        assertNotNull(agencia);
    }

    @Test
    void testRemover() {
        when(agenciaRepository.deleteById(anyLong())).thenReturn(true);

        agenciaService.remover(ID);

        verify(agenciaRepository, times(1)).deleteById(ID);
    }

    @Test
    void atestAlterar() {
        Agencia agencia = criarAgencia();
        when(agenciaRepository.update(anyString(), any(Object[].class))).thenReturn(1);

        agenciaService.alterar(agencia);

        verify(agenciaRepository, times(1)).update(anyString(), eq(new Object[]{NOME, RAZAO_SOCIAL, CNPJ, ID}));
    }
}
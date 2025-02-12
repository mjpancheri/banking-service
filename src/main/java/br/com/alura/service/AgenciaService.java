package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.domain.http.AgenciaHttp;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.repository.AgenciaRepository;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AgenciaService {

    @RestClient
    SituacaoCadastralHttpService situacaoCadastralHttpService;

    private final AgenciaRepository agenciaRepository;
    private final MeterRegistry meterRegistry;

    public AgenciaService(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry) {
        this.agenciaRepository = agenciaRepository;
        this.meterRegistry = meterRegistry;
    }

    public void cadastrar(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());

        if (agenciaHttp == null || !SituacaoCadastral.ATIVO.equals(agenciaHttp.getSituacaoCadastral())) {
            Log.info("Erro ao cadastrar a agencia: " + agencia);
            meterRegistry.counter("agencia-erro-counter").increment();
            throw new AgenciaNaoAtivaOuNaoEncontradaException();
        }
        agenciaRepository.persist(agencia);
        Log.info("Agencia cadastrada com sucesso: " + agencia);
        meterRegistry.counter("agencia-adicionada-counter").increment();
    }

    public Agencia buscarPorId(Long id) {
        meterRegistry.counter("agencia-busca-counter").increment();
        return agenciaRepository.findById(id);
    }

    public void remover(Long id) {
        if (!agenciaRepository.deleteById(id)) {
            Log.info("Erro ao remover a agencia com id: " + id);
            throw new AgenciaNaoAtivaOuNaoEncontradaException();
        }
        Log.info("Agencia removida, id: " + id);
    }

    public void alterar(Agencia agencia) {
        agenciaRepository.update("nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4",
                agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId());
        Log.info("Agencia atualizada: " + agencia);
    }

}

package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.domain.http.AgenciaHttp;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.repository.AgenciaRepository;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AgenciaService {

    @RestClient
    SituacaoCadastralHttpService situacaoCadastralHttpService;

    private final AgenciaRepository agenciaRepository;

    public AgenciaService(AgenciaRepository agenciaRepository) {
        this.agenciaRepository = agenciaRepository;
    }

    public void cadastrar(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());

        if (agenciaHttp == null || !SituacaoCadastral.ATIVO.equals(agenciaHttp.getSituacaoCadastral())) {
            Log.info("Erro ao cadastrar a agencia: " + agencia);
            throw new AgenciaNaoAtivaOuNaoEncontradaException();
        }
        agenciaRepository.persist(agencia);
        Log.info("Agencia cadastrada com sucesso: " + agencia);
    }

    public Agencia buscarPorId(Long id) {
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

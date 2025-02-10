package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.domain.http.AgenciaHttp;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AgenciaService {

    @RestClient
    SituacaoCadastralHttpService situacaoCadastralHttpService;

    private final List<Agencia> agencias = new ArrayList<>();

    public void cadastrar(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());

        if (agenciaHttp == null || !SituacaoCadastral.ATIVO.equals(agenciaHttp.getSituacaoCadastral())) {
            throw new AgenciaNaoAtivaOuNaoEncontradaException();
        }
        agencias.add(agencia);
    }

    public Agencia buscarPorId(Integer id) {
        return agencias.stream().filter(agencia -> agencia.getId().equals(id)).toList().getFirst();
    }

    public void remover(Integer id) {
        agencias.removeIf(agencia -> agencia.getId().equals(id));
    }

    public void alterar(Agencia agencia) {
        remover(agencia.getId());

        agencias.add(agencia);
    }

}

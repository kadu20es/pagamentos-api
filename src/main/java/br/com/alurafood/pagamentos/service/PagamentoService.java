package br.com.alurafood.pagamentos.service;


import br.com.alurafood.pagamentos.dto.PagamentoDTO;
import br.com.alurafood.pagamentos.http.PedidoClient;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository; // injeção de dependência

    @Autowired
    private ModelMapper modelMapper; // permite transferir dados de forma facilitada entre a entidade e o DTO

    @Autowired
    private PedidoClient pedido;

    // retorna paginado os objetos do tipo PagamentoDTO
    public Page<PagamentoDTO> obterTodos(Pageable paginacao) {
        return repository
                .findAll(paginacao) // pega todos os pagamentos
                .map(p -> modelMapper.map(p, PagamentoDTO.class)); // mapeia para DTO. O repository entende pgmento
    }

    // busca dados baseados em um ID
    public PagamentoDTO obterPorId(Long id){
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        PagamentoDTO dto = modelMapper.map(pagamento, PagamentoDTO.class);
        dto.setItens(pedido.obterItensDoPedido(pagamento.getPedidoId()).getItens()); // integra os dados da api de pedidos
        return dto;
        //return modelMapper.map(pagamento, PagamentoDTO.class);
    }

    // chega um json pagamentodto, ele foi serializado e vai ser lido, transforma de dto para pagamento e
    // coloca o status dele como criado e salva no banco
    // transforma para DTO e devolve para o controller
    public PagamentoDTO criarPagamento(PagamentoDTO dto){
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        repository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDTO.class);
    }

    public PagamentoDTO atualizarPagamento (Long id, PagamentoDTO dto){
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setId(id);
        pagamento = repository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoDTO.class);
    }

    public void excluirPagamento (Long id){
        repository.deleteById(id);
    }

    public void confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()){
            throw new EntityNotFoundException("Pagamento não encontrado");
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        repository.save(pagamento.get());
        pedido.atualizarPagamento(pagamento.get().getPedidoId());
    }

    public void alterarStatus(Long id) {
        Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()){
            throw new EntityNotFoundException("Pagamento não encontrado");
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        repository.save(pagamento.get());
    }
}
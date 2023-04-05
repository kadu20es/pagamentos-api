package br.com.alurafood.pagamentos.controller;

import br.com.alurafood.pagamentos.dto.PagamentoDTO;
import br.com.alurafood.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService service;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public Page<PagamentoDTO> listar(@PageableDefault(size = 10) Pageable paginacao) {
        return service.obterTodos(paginacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> detalhar(@PathVariable @NotNull Long id) {
        PagamentoDTO dto = service.obterPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PagamentoDTO> cadastrar(@RequestBody @Valid PagamentoDTO dto, UriComponentsBuilder uriBuilder){
        PagamentoDTO pagamento = service.criarPagamento(dto);
        URI endereco = uriBuilder.path("/pagamentos/{id}")
                .buildAndExpand(pagamento.getId()).toUri();

        // cria mensagem que será enviada para a fila rabbitmq
        // .getBytes() converte a mensagem para bytes
        // Message message = new Message(("Criado pagamento com ID: " + pagamento.getId()).getBytes());
        // rabbitTemplate.send("pagamento.concluido", message);

        // agora usa apenas um Jackson2JsonMessageConverter e envia a mensagem
        // rabbitTemplate.convertAndSend("pagamento.concluido", pagamento); // substituída pela exchange

        // agora usa apenas um Jackson2JsonMessageConverter e envia a mensagem e envia pela exchange pagmentos.ex
        rabbitTemplate.convertAndSend("pagamentos.ex", "", pagamento);

        return ResponseEntity.created(endereco).body(pagamento);
    }

    // @RequestBody serve para que o Spring recupere os dados enviados no corpo da requisição
    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDTO dto) {
        PagamentoDTO atualizado = service.atualizarPagamento(id, dto);

        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoDTO> exluirPagamento(@PathVariable @NotNull Long id){
        service.excluirPagamento(id);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente") // ----> tenta comunicar com a segunda instância se a primeira tentativa falhar
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        service.confirmarPagamento(id);

    }

    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        service.alterarStatus(id);

    }

}

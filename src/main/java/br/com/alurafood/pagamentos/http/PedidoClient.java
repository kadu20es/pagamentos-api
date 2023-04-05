package br.com.alurafood.pagamentos.http;

import br.com.alurafood.pagamentos.model.Pedido;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.GET;

/* integra-se à pedidos-clientes-api para informar que um pedido foi pago de maneira síncrona
 a implementação dessa interface está na classe pagamentoService
 e também no controller (confirmarPagamento)
*/

@FeignClient("pedidos-clientes-api")
public interface PedidoClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/pedidos/{id}/pago")
    void atualizarPagamento(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/pedidos/{id}")
    Pedido obterItensDoPedido(@PathVariable Long id);
}

package br.com.alurafood.pagamentos.dto;

import br.com.alurafood.pagamentos.model.ItemDoPedido;
import br.com.alurafood.pagamentos.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author: Carlos Eduardo Carmo do Val
 * Created: 16/03/2023
 * Description: Classe que faz a transferência entre o DTO e a entidade
 */

@Getter
@Setter
public class PagamentoDTO {

    private Long id;
    private BigDecimal valor;
    private String nome;
    private String numero;
    private String expiracao;
    private String codigo;
    private Status status;
    private Long pedidoId;
    private Long formaDePagamentoId;
    private List<ItemDoPedido> itens;
}

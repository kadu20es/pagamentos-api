package br.com.alurafood.pagamentos.model;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * author: Carlos Eduardo Carmo do Val
 * created: 16/03/2023 (ddMMaaaa)
 * description: classe que representa a tabela no banco de dados
 */

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento {

    @Id // para a tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // como é gerado o ID?
    private Long id;

    @NotNull
    @Positive
    private BigDecimal valor;

    @NotBlank
    @Size(max = 100)
    private String nome;

    @Size(max = 19)
    private String numero;

    @Size(max = 7)
    private String expiracao;

    @Size(min = 3, max = 3)
    private String codigo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private Long pedidoId;

    @NotNull
    private Long formaDePagamentoId;

}

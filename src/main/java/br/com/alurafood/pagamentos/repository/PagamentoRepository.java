package br.com.alurafood.pagamentos.repository;


import br.com.alurafood.pagamentos.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: Carlos Eduardo Carmo do Val
 * Created: 16/03/2023
 * Description: interface que representa a manipulação e o que se precisa entre a entidade e o banco de dados
 * representa um Bean do spring. É necessário indicar qual é a classe que representa a entidade (pagamento) e o id (Long)
 * Essa interface e o modelo Pagamento+Enumerador Status é o mínimo necessário para fazer um CRUD
 */

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}

package br.com.alurafood.pagamentos.amqp;


import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cria fila do RabbitMQ
 */
@Configuration
public class PagamentoAMQPConfiguration {

    /*@Bean
    public Queue criarFila(){
        //return new Queue("pagamento.concluido", false);
        return QueueBuilder.nonDurable("pagamento.concluido").build();
    }*/ // fila substituída pela exchange

    /**
     * cria uma nova exchange
     */
    @Bean
    public FanoutExchange exchange(){
        return new FanoutExchange("pagamentos.ex");
    }

    @Bean
    public RabbitAdmin criarRabbitAdmin(ConnectionFactory conn){
        return new RabbitAdmin(conn);
    }

    // inicializa o rabbitAdmin
    // com isso ele consegue executar a aplicação, criar a fila no rabbit e detectar que houve conexão
    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializarAdmin(RabbitAdmin rabbitAdmin){
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}

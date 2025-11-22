package br.com.fiap.mentalance.listener;

import br.com.fiap.mentalance.config.RabbitMQConfig;
import br.com.fiap.mentalance.dto.CheckinMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Listener opcional para processar mensagens de check-in do RabbitMQ.
 * Pode ser usado para notificações, logs, integrações externas, etc.
 * 
 * Este listener só será ativado se:
 * 1. spring.rabbitmq.host estiver configurado
 * 2. A conexão com RabbitMQ for bem-sucedida
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.host", matchIfMissing = false)
public class CheckinMessageListener {

    /**
     * Processa mensagens da fila de check-ins.
     * Este é um exemplo de como você pode consumir as mensagens.
     * Você pode adicionar lógica para:
     * - Enviar notificações por email
     * - Integrar com sistemas externos
     * - Gerar relatórios em lote
     * - etc.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_CHECKIN, autoStartup = "${spring.rabbitmq.listener.simple.auto-startup:false}")
    public void processarCheckin(CheckinMessageDTO message) {
        log.info("Mensagem de check-in recebida: checkinId={}, usuario={}, humor={}, energia={}, sono={}",
                message.getCheckinId(),
                message.getUsuarioNome(),
                message.getHumor(),
                message.getEnergia(),
                message.getSono());

        // Exemplo: Aqui você pode adicionar lógica adicional
        // - Enviar email de confirmação
        // - Atualizar estatísticas em tempo real
        // - Integrar com serviços externos
        // - etc.
    }
}


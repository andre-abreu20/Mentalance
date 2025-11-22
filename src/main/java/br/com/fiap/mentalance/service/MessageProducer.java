package br.com.fiap.mentalance.service;

import br.com.fiap.mentalance.config.RabbitMQConfig;
import br.com.fiap.mentalance.dto.CheckinMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Serviço para enviar mensagens ao RabbitMQ.
 * Desabilitado automaticamente se spring.rabbitmq.host não estiver configurado.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Envia uma mensagem de check-in para a fila.
     *
     * @param message DTO com os dados do check-in
     */
    public void enviarCheckin(CheckinMessageDTO message) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_CHECKIN, message);
            log.info("Mensagem de check-in enviada para a fila: checkinId={}, usuarioId={}",
                    message.getCheckinId(), message.getUsuarioId());
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem de check-in para o RabbitMQ", e);
            // Não lança exceção para não interromper o fluxo principal
        }
    }
}


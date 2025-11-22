package br.com.fiap.mentalance.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuração de cache usando Caffeine.
 * Define diferentes caches com TTL (Time To Live) apropriados para cada tipo de dado.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache para check-ins recentes do dashboard.
     * TTL: 5 minutos (dados podem mudar frequentemente)
     */
    public static final String CACHE_CHECKINS_RECENTES = "checkinsRecentes";

    /**
     * Cache para resumo semanal do dashboard.
     * TTL: 10 minutos (estatísticas semanais mudam menos frequentemente)
     */
    public static final String CACHE_RESUMO_SEMANAL = "resumoSemanal";

    /**
     * Cache para análises de IA.
     * TTL: 30 minutos (análises históricas raramente mudam)
     */
    public static final String CACHE_ANALISES_IA = "analisesIa";

    /**
     * Cache para estatísticas globais (admin).
     * TTL: 5 minutos (pode mudar com novos check-ins)
     */
    public static final String CACHE_ESTATISTICAS_GLOBAIS = "estatisticasGlobais";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                CACHE_CHECKINS_RECENTES,
                CACHE_RESUMO_SEMANAL,
                CACHE_ANALISES_IA,
                CACHE_ESTATISTICAS_GLOBAIS
        );

        // Configuração padrão para todos os caches
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(500) // Máximo de 500 entradas por cache
                        .expireAfterWrite(10, TimeUnit.MINUTES) // TTL padrão: 10 minutos
                        .recordStats() // Habilita estatísticas de cache para monitoramento
        );

        return cacheManager;
    }
}


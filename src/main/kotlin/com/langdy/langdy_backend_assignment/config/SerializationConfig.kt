package com.langdy.langdy_backend_assignment.config

import com.langdy.langdy_backend_assignment.serializers.LocalDateTimeSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class SerializationConfig {
    private val module =
        SerializersModule {
            contextual(LocalDateTime::class, LocalDateTimeSerializer)
        }

    @Bean
    fun json(): Json =
        Json {
            serializersModule = module
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
}

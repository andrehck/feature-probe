# Feature Probe SDK

**Feature Probe** é uma biblioteca Kotlin pensada para facilitar a ativação controlada de features em ambientes de experimentação (alpha), utilizando **allow list** e **rollout percentual**, de forma flexível e transparente para os times de desenvolvimento.

---

## Visão Geral

Essa SDK permite que você:

- Libere novas features somente para usuários autorizados (via allow list)
- Gradualmente exponha novas funcionalidades usando rollout baseado em ID
- Combine ambas as estratégias, sem precisar escrever código repetitivo em cada nova feature
- Esconda configurações sensíveis como `baseUrl` e `apiKey` da aplicação final

---

## Instalação

Adicione a SDK ao seu projeto Kotlin/Java como módulo ou via JitPack (em breve).

---

## Uso

### 1. Inicialização

```kotlin
FeatureProbe.init(
    FeatureProbeConfig(
        rolloutPercentage = 30,      // libera para 30% dos IDs
        useAllowList = true,         // ativa validação na allow list
        useRollout = true            // ativa rollout
    )
)
```

### 2. Verificação de feature

```kotlin
val liberado = FeatureProbe.isEnabled("nova-feature", "user-abc123")

if (liberado) {
    // mostrar nova funcionalidade
}
```

---

## Configuração

### `FeatureProbeConfig`

| Campo              | Tipo     | Descrição |
|--------------------|----------|-----------|
| `rolloutPercentage`| `Int`    | Percentual de usuários liberados (0 a 100) |
| `useAllowList`     | `Boolean`| Ativa ou desativa a validação com a allow list |
| `useRollout`       | `Boolean`| Ativa ou desativa o rollout percentual |

> O `baseUrl`, `apiKey` e `timeout` são internos à SDK e não precisam ser configurados pelo time consumidor.

---

## Estratégia

A SDK segue essa lógica:

1. Se `useAllowList` for `true`, consulta o serviço externo com `feature` e `id`:
   - Se a resposta for `false`, a feature não será liberada.
2. Se `useRollout` for `true`, calcula:  
   `hash(id) % 100 < rolloutPercentage`
   - Exemplo: se `rollout = 30`, só IDs cujo hash % 100 < 30 passam.
3. Se nenhuma verificação estiver ativa, a feature será liberada (`true` por padrão).

---

## Testes Unitários

Testes estão incluídos no módulo de testes:

**Exemplo de teste:**
```kotlin
@Test
fun `should enable feature based on rollout`() {
    val enabled = FeatureProbe.isEnabled("teste-feature", "user-id-123")
    val expected = "user-id-123".hashCode().absoluteValue % 100 < 50
    assertEquals(expected, enabled)
}
```

---

## Estrutura do Projeto

```
feature-probe-sdk/
├── src/
│   ├── main/
│   │   └── kotlin/com/seunome/featureprobe/
│   │       ├── FeatureProbe.kt
│   │       ├── FeatureProbeConfig.kt
│   │       ├── FeatureProbeException.kt
│   │       └── ProbeClient.kt
│   └── test/
│       └── kotlin/com/seunome/featureprobe/
│           └── FeatureProbeTest.kt
```

---

## Próximos passos

- [ ] Suporte a múltiplos ambientes (ex: QA, staging, prod)
- [ ] Plugin para injeção automática em serviços Web
- [ ] Publicar via JitPack ou Maven Central

---

## Licença

MIT - sinta-se livre para usar, melhorar e contribuir!

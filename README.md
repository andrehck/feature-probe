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


---

## 🎯 Estudo de Caso: Nova Jornada de Pagamento de Fatura

### Cenário

Sua squad está desenvolvendo uma **nova experiência de pagamento de fatura** no aplicativo mobile do banco. Antes de liberar para todos os clientes, vocês desejam:

- Testar a jornada com um grupo pequeno de usuários internos (via allow list)
- Depois liberar gradualmente com rollout percentual
- Ter a possibilidade de reverter sem precisar de deploy
- Não adicionar blocos de código repetitivos em cada tela/serviço

### Solução com `feature-probe`

1. Definam um identificador único para a feature:
   - `nova-tela-pagamento-fatura`

2. Inicializem a SDK com a configuração desejada:

```kotlin
FeatureProbe.init(
    FeatureProbeConfig(
        rolloutPercentage = 20,     // libera para 20% dos usuários
        useAllowList = true,        // ativa controle por allow list
        useRollout = true           // ativa rollout incremental
    )
)
```

3. No momento da decisão, use a SDK:

```kotlin
val podeUsarNovaTela = FeatureProbe.isEnabled("nova-tela-pagamento-fatura", userId)

if (podeUsarNovaTela) {
    navController.navigate("novaTelaPagamento")
} else {
    navController.navigate("telaPagamentoAtual")
}
```

### Benefícios dessa abordagem

✅ Centralização da regra de rollout  
✅ Evita uso de variáveis de ambiente ou `if` espalhados no código  
✅ Liberação segura e gradual  
✅ Foco total do time na experiência, não no controle de acesso  
✅ Possibilidade de reuso da regra em Android, iOS e backend  

---

---

## 🧠 Estudo de Caso Backend: Nova Lógica de Cálculo de Juros para Pagamento Atrasado

### Cenário

Sua squad do backend está trabalhando em uma **nova regra de cálculo de juros** para pagamentos atrasados de fatura. Essa nova regra precisa ser validada com:

- Usuários em uma **allow list de testes internos**
- Liberação gradual via **rollout controlado**

Você quer evitar `if` espalhado, variáveis de ambiente ou feature flags manuais, e deseja usar o `feature-probe` para controlar a ativação da lógica de forma centralizada e limpa.

---

### Exemplo de uso no código backend (Kotlin + Spring Boot)

```kotlin
@RestController
@RequestMapping("/api/fatura")
class FaturaController {

    @GetMapping("/{userId}/calcular-juros")
    fun calcularJuros(@PathVariable userId: String): ResponseEntity<JurosResponse> {
        val usarNovaRegra = FeatureProbe.isEnabled("nova-regra-juros", userId)

        val juros = if (usarNovaRegra) {
            calcularJurosNovaLogica(userId)
        } else {
            calcularJurosAtual(userId)
        }

        return ResponseEntity.ok(JurosResponse(juros))
    }
}
```

---

## Licença

MIT - sinta-se livre para usar, melhorar e contribuir!

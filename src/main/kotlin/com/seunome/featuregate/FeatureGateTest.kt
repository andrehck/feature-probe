import kotlin.test.*
import org.junit.BeforeTest
import org.junit.Test

class FeatureProbeTest {

    @BeforeTest
    fun setup() {
        FeatureProbe.init(
            FeatureProbeConfig(
                rolloutPercentage = 50,
                useAllowList = true,
                useRollout = true
            )
        )
    }

    @Test
    fun `retorna true se id estiver na allow list`() {
        FeatureProbe.allowListService = FakeAllowListService(allowed = true)
        assertTrue(FeatureProbe.isEnabled("feature-x", "user-123"))
    }

    @Test
    fun `retorna false se id não estiver na allow list e rollout desativado`() {
        FeatureProbe.init(
            FeatureProbeConfig(
                rolloutPercentage = 0,
                useAllowList = true,
                useRollout = false
            )
        )
        FeatureProbe.allowListService = FakeAllowListService(allowed = false)
        assertFalse(FeatureProbe.isEnabled("feature-x", "user-123"))
    }

    @Test
    fun `retorna true se id cair no rollout mesmo fora da allow list`() {
        FeatureProbe.init(
            FeatureProbeConfig(
                rolloutPercentage = 100,
                useAllowList = true,
                useRollout = true
            )
        )
        FeatureProbe.allowListService = FakeAllowListService(allowed = false)
        assertTrue(FeatureProbe.isEnabled("feature-x", "qualquer-id"))
    }

    @Test
    fun `retorna false se rollout e allow list não autorizarem`() {
        FeatureProbe.init(
            FeatureProbeConfig(
                rolloutPercentage = 0,
                useAllowList = true,
                useRollout = true
            )
        )
        FeatureProbe.allowListService = FakeAllowListService(allowed = false)
        assertFalse(FeatureProbe.isEnabled("feature-x", "qualquer-id"))
    }
}

class FakeAllowListService(private val allowed: Boolean) : AllowListService {
    override fun isAllowed(featureId: String, userId: String): Boolean = allowed
}

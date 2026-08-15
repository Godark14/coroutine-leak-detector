package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class FlowCollectWithoutLifecycleInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/inspections"

    fun testFlowCollectWithoutLifecycle() {
        myFixture.enableInspections(FlowCollectWithoutLifecycleInspection::class.java)
        myFixture.configureByFiles(
            "FlowCollectWithoutLifecycle.kt",
            "androidx/lifecycle/Lifecycle.kt"
        )
        myFixture.checkHighlighting(true, false, false)
    }

    fun testSuggestsWrapQuickFixInFragmentContext() {
        myFixture.enableInspections(FlowCollectWithoutLifecycleInspection::class.java)
        myFixture.configureByFiles(
            "FlowCollectInFragment.kt",
            "androidx/lifecycle/Lifecycle.kt",
            "androidx/fragment/app/Fragment.kt"
        )

        val highlights = myFixture.doHighlighting()
        val warning = highlights.firstOrNull {
            it.description?.contains("Collecting a Flow here") == true
        }
        assertNotNull("Expected the inspection to report a warning", warning)

        myFixture.editor.caretModel.moveToOffset(warning!!.startOffset)

        val quickFix = myFixture.getAvailableIntention(
            "Wrap with repeatOnLifecycle(Lifecycle.State.STARTED)"
        )
        assertNotNull(
            "Expected the context-aware quick fix to be available in a Fragment",
            quickFix
        )
    }
}
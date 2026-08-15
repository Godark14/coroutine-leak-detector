package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RunBlockingUsageInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/inspections"

    fun testRunBlockingUsage() {
        myFixture.enableInspections(RunBlockingUsageInspection::class.java)
        myFixture.testHighlighting(true, false, false, "RunBlockingUsage.kt")
    }

    fun testSuggestsViewModelScopeReplacementInViewModelContext() {
        myFixture.enableInspections(RunBlockingUsageInspection::class.java)
        myFixture.configureByFiles(
            "RunBlockingInViewModel.kt",
            "androidx/lifecycle/ViewModel.kt"
        )

        val highlights = myFixture.doHighlighting()
        val warning = highlights.firstOrNull {
            it.description?.contains("runBlocking blocks the calling thread") == true
        }
        assertNotNull("Expected the inspection to report a warning", warning)

        myFixture.editor.caretModel.moveToOffset(warning!!.startOffset)

        val quickFix = myFixture.getAvailableIntention(
            "Add TODO to replace runBlocking with viewModelScope"
        )
        assertNotNull(
            "Expected the context-aware quick fix suggesting viewModelScope",
            quickFix
        )
    }
}
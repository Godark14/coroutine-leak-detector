package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class UncancelledScopeInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/inspections"

    fun testUncancelledScope() {
        myFixture.enableInspections(UncancelledScopeInspection::class.java)
        myFixture.configureByFiles(
            "UncancelledScope.kt",
            "androidx/lifecycle/ViewModel.kt"
        )
        myFixture.checkHighlighting(true, false, false)
    }
}
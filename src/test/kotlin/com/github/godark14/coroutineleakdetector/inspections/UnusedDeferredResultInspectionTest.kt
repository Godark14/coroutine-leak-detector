package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class UnusedDeferredResultInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/inspections"

    fun testUnusedDeferredResult() {
        myFixture.enableInspections(UnusedDeferredResultInspection::class.java)
        myFixture.testHighlighting(true, false, false, "UnusedDeferredResult.kt")
    }
}
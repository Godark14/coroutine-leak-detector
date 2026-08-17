package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class HardcodedDispatcherInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/inspections"

    fun testHardcodedDispatcher() {
        myFixture.enableInspections(HardcodedDispatcherInspection::class.java)
        myFixture.testHighlighting(true, false, false, "HardcodedDispatcher.kt")
    }
}
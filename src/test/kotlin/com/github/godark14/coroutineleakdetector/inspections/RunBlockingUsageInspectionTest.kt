package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RunBlockingUsageInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/inspections"

    fun testRunBlockingUsage() {
        myFixture.enableInspections(RunBlockingUsageInspection::class.java)
        myFixture.testHighlighting(true, false, false, "RunBlockingUsage.kt")
    }
}
package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class GlobalScopeInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/inspections"

    fun testGlobalScopeUsage() {
        myFixture.enableInspections(GlobalScopeInspection::class.java)
        myFixture.testHighlighting(true, false, false, "GlobalScopeUsage.kt")
    }
}
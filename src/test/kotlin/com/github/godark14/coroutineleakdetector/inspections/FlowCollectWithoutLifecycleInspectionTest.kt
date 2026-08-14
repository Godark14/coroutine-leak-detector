package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class FlowCollectWithoutLifecycleInspectionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData/inspections"

    fun testFlowCollectWithoutLifecycle() {
        myFixture.enableInspections(FlowCollectWithoutLifecycleInspection::class.java)
        myFixture.testHighlighting(true, false, false, "FlowCollectWithoutLifecycle.kt")
    }
}
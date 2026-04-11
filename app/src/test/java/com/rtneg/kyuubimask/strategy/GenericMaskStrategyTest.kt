/*
 * Copyright (c) 2026 soraiyu
 *
 * SPDX-License-Identifier: MIT
 */
package com.rtneg.kyuubimask.strategy

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GenericMaskStrategyTest {

    // --- canHandle tests ---

    @Test
    fun `canHandle returns true for the package it was created with`() {
        val strategy = GenericMaskStrategy("com.example.myapp")
        assertTrue(strategy.canHandle("com.example.myapp"))
    }

    @Test
    fun `canHandle returns false for a different package`() {
        val strategy = GenericMaskStrategy("com.example.myapp")
        assertFalse(strategy.canHandle("com.example.otherapp"))
    }

    @Test
    fun `canHandle returns false for empty string`() {
        val strategy = GenericMaskStrategy("com.example.myapp")
        assertFalse(strategy.canHandle(""))
    }

    @Test
    fun `canHandle is case-sensitive`() {
        val strategy = GenericMaskStrategy("com.Example.MyApp")
        assertFalse(strategy.canHandle("com.example.myapp"))
        assertFalse(strategy.canHandle("COM.EXAMPLE.MYAPP"))
        assertTrue(strategy.canHandle("com.Example.MyApp"))
    }

    @Test
    fun `canHandle returns false for package that merely contains the target`() {
        val strategy = GenericMaskStrategy("com.example.app")
        assertFalse(strategy.canHandle("com.example.app.extra"))
        assertFalse(strategy.canHandle("prefix.com.example.app"))
    }

    @Test
    fun `two instances with different packages are independent`() {
        val strategyA = GenericMaskStrategy("com.app.a")
        val strategyB = GenericMaskStrategy("com.app.b")
        assertTrue(strategyA.canHandle("com.app.a"))
        assertFalse(strategyA.canHandle("com.app.b"))
        assertTrue(strategyB.canHandle("com.app.b"))
        assertFalse(strategyB.canHandle("com.app.a"))
    }

    @Test
    fun `canHandle with empty-string package at construction only matches empty string`() {
        val strategy = GenericMaskStrategy("")
        assertTrue(strategy.canHandle(""))
        assertFalse(strategy.canHandle("com.anything"))
    }
}
